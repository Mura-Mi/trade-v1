package yokohama.murataku.trade.http.filters

import io.finch.{Endpoint, _}
import io.finch.syntax._

class OptionRouting {
  val ep: Endpoint[String] = options(*) {
    Ok("option ok")
  }
}
