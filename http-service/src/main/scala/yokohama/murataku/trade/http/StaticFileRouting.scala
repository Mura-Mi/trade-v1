package yokohama.murataku.trade.http

import wvlet.airframe.http.Endpoint

import scala.io.Source

@Endpoint(path = "/static")
trait StaticFileRouting {
  @Endpoint(path = "/hoge")
  def hoge(): String = {
    "fuga"
  }

  @Endpoint(path = "/js/:category/:filename")
  def js(category: String, filename: String): String = {
    Source.fromResource(s"public/js/$category/$filename").mkString
  }
}
