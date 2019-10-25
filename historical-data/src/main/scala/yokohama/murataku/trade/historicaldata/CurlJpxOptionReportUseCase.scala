package yokohama.murataku.trade.historicaldata

import java.io.BufferedOutputStream
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import better.files.Dsl.unzip
import better.files.File
import com.twitter.util.{Await, Future}
import wvlet.airframe._
import wvlet.log.LogSupport
import yokohama.murataku.trade.historicaldata.database.RawJpxOptionPrice
import yokohama.murataku.trade.holiday.HolidayRepository
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.product.indexoption.{IndexOptionName, PutOrCall}
import yokohama.murataku.trade.product.{
  IndexName,
  IndexOptionFactory,
  IndexOptionRepository
}

trait CurlJpxOptionReportUseCase extends LogSupport {
  private val priceRepo = bind[DailyMarketPriceRepository]
  private val productRepo = bind[IndexOptionRepository]
  private val calendar = bind[HolidayRepository]

  def run(today: LocalDate): Unit = {
    val nk225 = IndexName("NK225E")

    info(s"today: $today")

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
                val delivery: YearMonth =
                  YearMonth.fromSixNum(row.deliveryLimit)
                val indexOptionName = IndexOptionName(
                  if (poc.isCall) row.callProductCode else row.putProductCode)

                val option =
                  new IndexOptionFactory(calendar).createNew(nk225,
                                                             indexOptionName,
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
                PutOrCall.both.flatMap { poc =>
                  raw.toDatabaseObject(today, poc)
              })
              .map(priceRepo.store)
          }
          .map(_.sum)
          .onSuccess(c => info(s"Done: $c"))

        Await.result {
          productMasterPersistenceResult
            .flatMap(_ => pricePersistenceResult).unit
        }
      }
    }
  }
}
