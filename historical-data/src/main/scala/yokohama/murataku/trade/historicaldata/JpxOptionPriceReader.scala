package yokohama.murataku.trade.historicaldata

import java.nio
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import better.files.Dsl.unzip
import better.files.File
import com.twitter.finagle.Http
import com.twitter.finagle.http.Request
import com.twitter.io.Buf.ByteBuffer
import com.twitter.util.Future
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
  val calendar = bind[Calendar]
  val finagle = Http.newService("www.jpx.co.jp:80", "jpx-option-price")
  val indexOptionFactory = new IndexOptionFactory(calendar)

  val nk225 = IndexName("NK225E")

  def get(date: LocalDate): Future[JpxOptionPriceReportFetchResult] = {
    val fmt = DateTimeFormatter.ofPattern("yyyyMMdd")
    finagle
      .apply(
        Request(
          s"/markets/derivatives/option-price/data/ose${fmt.format(date)}tp.zip"
        )).map(resp => {
        info(resp.status)
        val buf = resp.content
        val twitterByteBuf = ByteBuffer.coerce(buf)

        File.temporaryFile() {
          tmp =>
            val byteBuf = nio.ByteBuffer.allocate(twitterByteBuf.length)
            twitterByteBuf.write(byteBuf)
            val ch = tmp.newFileOutputStream().getChannel
            ch.write(byteBuf)

            File.temporaryDirectory() {
              unzipDir =>
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

                val productMaster: Seq[IndexOption] =
                  csvFile
                    .flatMap {
                      row =>
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

                (productMaster, price)
            }
        }
      }).map {
        case (productMaster, prices) =>
          JpxOptionPriceReportFetchResult(date, productMaster, prices)
      }

  }
}
