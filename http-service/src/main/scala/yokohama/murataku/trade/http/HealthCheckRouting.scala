package yokohama.murataku.trade.http

import io.finch.{Endpoint, _}
import io.finch.syntax._

class HealthCheckRouting {
  val ep: Endpoint[String] = get("hc") {
    Ok("hello")
  }
}
