package yokohama.murataku.trade.historicaldata

import java.io.BufferedOutputStream
import java.net.URL
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import better.files.Dsl._
import better.files.File
import com.twitter.util.{Await, Future}
import io.getquill.{FinaglePostgresContext, SnakeCase}
import yokohama.murataku.trade.historicaldata.database.RawJpxOptionPrice
import yokohama.murataku.trade.holiday.{HolidayAdjustMethod, HolidayRepository}
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.finagle.PersistenceContextProvider
import yokohama.murataku.trade.product.indexoption.PutOrCall
import yokohama.murataku.trade.product.{IndexConstant, IndexOptionFactory, IndexOptionRepository}

object CurlJpxOptionReport extends StandardBatch {
  val ctx = PersistenceContextProvider.getContext
  val nk225 = IndexConstant.nk225E
  val calendar = new HolidayRepository(ctx)

  import calendar._

  val today = args.headOption
    .map(LocalDate.parse(_))
    .getOrElse(
      LocalDateTime.now
        .minusHours(18)
        .toLocalDate
        .adjust(HolidayAdjustMethod.Preceding)) // 多分ここまでには発表されてるはず

  info(s"today: $today")

  val priceRepo = new HistoricalPriceRepository(ctx)
  val productRepo = new IndexOptionRepository(ctx)

  val fmt = DateTimeFormatter.ofPattern("yyyyMMdd")

  val is = new URL(
    s"https://www.jpx.co.jp/markets/derivatives/option-price/data/ose${fmt.format(today)}tp.zip")
    .openConnection()
    .getInputStream

  File.temporaryFile() { tmp =>
    info(tmp.path.toAbsolutePath.toUri.toString)

    val wri = new BufferedOutputStream(tmp.newFileOutputStream())

    Stream
      .continually(is.read()) //
      .takeWhile(_ != -1) //
      .foreach(wri.write)

    wri.flush()

    File.temporaryDirectory() { unzipDir =>
      info(unzipDir.path.toUri)
      unzip(tmp)(unzipDir)

      import kantan.csv._
      import kantan.csv.generic._
      import kantan.csv.ops._

      val csvFile: Seq[RawJpxOptionPrice] = unzipDir.children
        .find(f => {
          info(f.pathAsString)
          f.isRegularFile && f.extension.contains(".csv")
        })
        .map(
          e =>
            e.url
              .asCsvReader[RawJpxOptionPrice](rfc.withoutHeader)
              .toSeq
              .flatMap {
                case Right(raw) => Option(raw)
                case Left(e) =>
                  error(e)
                  None
              }
              .filter(_.productCode.trim() == nk225.value)
              .map(a =>
                a.copy(
                  productCode = a.productCode.trim(),
                  productType = a.productType.trim(),
                  deliveryLimit = a.deliveryLimit.trim(),
                  note1 = a.note1.trim(),
                  putProductCode = a.putProductCode.trim(),
                  putSpare = a.putSpare.trim(),
                  callProductCode = a.callProductCode.trim(),
                  callSpare = a.callSpare.trim()
              ))
        )
        .getOrElse {
          error("csv file is not found")
          Nil
        }

      info(s"data in csv: ${csvFile.size}")

      val productMasterPersistenceResult = Future
        .collect {
          csvFile.flatMap { row =>
            PutOrCall.both.map { poc =>
              val delivery: YearMonth = YearMonth.decode(row.deliveryLimit)
              val option =
                new IndexOptionFactory(calendar).createNew(nk225,
                                                           poc,
                                                           delivery,
                                                           row.strike)
              productRepo.store(option)
            }
          }
        }
        .map(_.sum)
        .onSuccess(num => info(s"Product persistence: $num"))

      val pricePersistenceResult = Future
        .collect {
          csvFile
            .flatMap(raw =>
              PutOrCall.both.map { poc =>
                raw.toDatabaseObject(today, poc)
            })
            .map(priceRepo.store)
        }
        .map(_.sum)
        .onSuccess(c => info(s"Done: $c"))

      Await.result {
        productMasterPersistenceResult.flatMap(_ => pricePersistenceResult).unit
      }
    }
  }
}
