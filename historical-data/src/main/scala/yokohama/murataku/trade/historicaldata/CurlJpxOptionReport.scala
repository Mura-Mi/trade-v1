package yokohama.murataku.trade.historicaldata

import java.io.BufferedOutputStream
import java.net.URL
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import better.files.Dsl._
import better.files.File
import com.twitter.util.{Await, Future}
import io.getquill.{FinaglePostgresContext, SnakeCase}
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.historicaldata.database.RawJpxOptionPrice
import yokohama.murataku.trade.holiday.{HolidayRepository, YearMonth}
import yokohama.murataku.trade.product.{
  IndexName,
  IndexOptionFactory,
  IndexOptionRepository,
  PutOrCall
}

object CurlJpxOptionReport extends StandardBatch {
  val nk225 = "NK225E"

  val today = args.headOption
    .map(LocalDate.parse(_))
    .getOrElse(LocalDateTime.now.minusHours(18).toLocalDate) // 多分ここまでには発表されてるはず

  val ctx: FinaglePostgresContext[SnakeCase] =
    new FinaglePostgresContext(SnakeCase, "ctx")
  val priceRepo = new HistoricalPriceRepository(ctx)
  val productRepo = new IndexOptionRepository(ctx)
  val calendar = new HolidayRepository(ctx)

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
      import kantan.csv.ops._
      import kantan.csv.generic._

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
              .filter(_.productCode.trim() == nk225)
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
                new IndexOptionFactory(calendar).createNew(IndexName(nk225),
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
