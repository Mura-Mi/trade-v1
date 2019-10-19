package yokohama.murataku.trade.http

import wvlet.airframe.http.{Endpoint, HttpMethod}

@Endpoint(path = "")
trait OptionRouting {
  @Endpoint(path = "/*any", method = HttpMethod.OPTIONS)
  def option(any: String): String = any
}
