package yokohama.murataku.trade.historicaldata

import java.io.BufferedOutputStream
import java.net.URL
import java.time.{LocalDate, LocalDateTime}

import better.files.Dsl._
import better.files.File
import wvlet.airframe.codec.MessageCodec
import yokohama.murataku.support.StandardBatch
import yokohama.murataku.trade.historicaldata.database.RawJpxOptionPrice

object CurlJpxOptionReport extends StandardBatch {

  override def main(args: Array[String]): Unit = {

    val today = args.headOption.map(LocalDate.parse(_)).getOrElse(LocalDate.now)

    val is = new URL(
      s"https://www.jpx.co.jp/markets/derivatives/option-price/data/ose${today.formatted("yyyyMMdd")}tp.zip")
      .openConnection()
      .getInputStream

    val tmp = File.newTemporaryFile()
    info(tmp.path.toAbsolutePath.toUri.toString)

    val wri = new BufferedOutputStream(tmp.newFileOutputStream())

    Stream
      .continually(is.read()) //
      .takeWhile(_ != -1) //
      .foreach(wri.write)

    wri.flush()

    val unzipDir = File.newTemporaryDirectory()
    info(unzipDir.path.toUri)
    unzip(tmp)(unzipDir)

    tmp.delete()

    import kantan.csv._
    import kantan.csv.ops._
    import kantan.csv.generic._

    unzipDir.children
      .find(f => {
        info(f.pathAsString)
        f.isRegularFile && f.extension.contains(".csv")
      })
      .map(
        _.url
          .asCsvReader[RawJpxOptionPrice](rfc.withoutHeader)
          .foreach(info(_)))
  }
}
