package yokohama.murataku.trade.historicaldata

import java.io.BufferedOutputStream
import java.net.URL
import java.time.LocalDateTime

import better.files.Dsl._
import better.files.File
import yokohama.murataku.support.StandardBatch

object CurlJpxOptionReport extends StandardBatch {

  def now = LocalDateTime.now

  val is = new URL(
    "https://www.jpx.co.jp/markets/derivatives/option-price/data/ose20190722tp.zip")
    .openConnection()
    .getInputStream

  val tmp = File.newTemporaryFile(suffix = now.toString)
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

  unzipDir.children
    .find(f => {
      info(f.pathAsString)
      f.isRegularFile && f.extension.contains(".csv")
    })
    .map(_.lines.foreach(info(_)))
}
