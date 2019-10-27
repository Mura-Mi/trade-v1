package yokohama.murataku.trade.historicaldata

import java.io.BufferedOutputStream
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import better.files.Dsl.unzip
import better.files.File
import com.twitter.concurrent.AsyncSemaphore
import com.twitter.finagle.Service
import com.twitter.finagle.filter.RequestSemaphoreFilter
import com.twitter.util.{Future, FuturePool}
import wvlet.airframe._
import wvlet.log.LogSupport
import yokohama.murataku.trade.historicaldata.database.RawJpxOptionPrice
import yokohama.murataku.trade.holiday.Calendar
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.product.indexoption.{
  IndexOption,
  IndexOptionName,
  PutOrCall
}
import yokohama.murataku.trade.product.{IndexName, IndexOptionFactory}

case class JpxOptionPriceReportFetchResult(
    date: LocalDate,
    productMaster: Seq[IndexOption],
    prices: Seq[DailyMarketPrice]
)

trait JpxOptionPriceReader extends LogSupport {
  private val calendar = bind[Calendar]
  private val indexOptionFactory = new IndexOptionFactory(calendar)

  private val nk225 = IndexName("NK225E")
  private val fmt = DateTimeFormatter.ofPattern("yyyyMMdd")

  private val service
    : Service[LocalDate, Seq[RawJpxOptionPrice]] = new RequestSemaphoreFilter(
    new AsyncSemaphore(3)) andThen Service.mk(
    date => {
      FuturePool.unboundedPool
        .apply {
          info(s"start fetching $date")
          val dateFormat = fmt.format(date)
          val inputStream = new URL(
            s"https://www.jpx.co.jp/markets/derivatives/option-price/data/ose${dateFormat}tp.zip")
            .openConnection()
            .getInputStream

          File.temporaryFile() {
            tmp =>
              val wri = new BufferedOutputStream(tmp.newFileOutputStream())

              Stream
                .continually(inputStream.read()) //
                .takeWhile(_ != -1) //
                .foreach(wri.write)

              wri.flush()

              File.temporaryDirectory() {
                unzipDir =>
                  info(unzipDir.path.toUri)
                  unzip(tmp)(unzipDir)

                  import kantan.csv._
                  import kantan.csv.generic._
                  import kantan.csv.ops._

                  unzipDir.children
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
                    ).toSeq.flatten
              }
          }
        }.onSuccess(seq => info(s"[date=$date][count=${seq.size}]"))
    }
  )

  def get(date: LocalDate): Future[JpxOptionPriceReportFetchResult] = {
    val fCsvFile =
      service(date)

    for {
      csvFile <- fCsvFile
    } yield {

      val productMaster: Seq[IndexOption] = csvFile
        .flatMap { row =>
          PutOrCall.both
            .map { poc =>
              val delivery: YearMonth =
                YearMonth.fromSixNum(row.deliveryLimit)
              val indexOptionName = IndexOptionName(
                if (poc.isCall) row.callProductCode
                else row.putProductCode)
              indexOptionFactory.createNew(nk225,
                                           indexOptionName,
                                           poc,
                                           delivery,
                                           row.strike)
            }
        }

      val price: Seq[DailyMarketPrice] =
        csvFile
          .flatMap(raw =>
            PutOrCall.both.flatMap { poc =>
              raw.toDatabaseObject(date, poc)
          })

      JpxOptionPriceReportFetchResult(date, productMaster, price)
    }
  }
}
