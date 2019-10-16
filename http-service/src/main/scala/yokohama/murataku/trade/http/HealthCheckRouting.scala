package yokohama.murataku.trade.http

import wvlet.airframe.http.Endpoint

@Endpoint(path = "")
trait HealthCheckRouting {
  @Endpoint(path = "/hc")
  def healthCheck(): String = "hello"
}
