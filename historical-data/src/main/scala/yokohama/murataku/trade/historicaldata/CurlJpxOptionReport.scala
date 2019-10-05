package yokohama.murataku.trade.historicaldata

import java.io.BufferedOutputStream
import java.net.URL
import java.time.{LocalDate, LocalDateTime}
import java.time.format.DateTimeFormatter

import better.files.Dsl._
import better.files.File
import com.twitter.util.{Await, Future}
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.historicaldata.database.RawJpxOptionPrice
import yokohama.murataku.trade.product.{IndexOptionRepository, PutOrCall}

object CurlJpxOptionReport extends StandardBatch {
  val nk225 = "NK225E"

  val today = args.headOption
    .map(LocalDate.parse(_))
    .getOrElse(LocalDateTime.now.minusHours(18).toLocalDate) // 多分ここまでには発表されてるはず

  val priceRepo = new HistoricalPriceRepository
  val productRepo = new IndexOptionRepository

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
              .filter(_.productCode == nk225)
        )
        .getOrElse {
          error("csv file is not found")
          Nil
        }

//      val productMasterPersistenceResult = Future.collect {
//        csvFile.flatMap { row =>
//          }
//      }

      val pricePersistenceResult = Future.collect {
        csvFile
          .flatMap(raw =>
            PutOrCall.both.map { poc =>
              raw.toDatabaseObject(today, poc)
          })
          .map(priceRepo.store)
      }

      Await.result {
        pricePersistenceResult.map(_.sum).onSuccess(c => info(s"Done: $c"))
      }
    }
  }
}
