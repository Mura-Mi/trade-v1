package yokohama.murataku.trade.http.finch

import com.twitter.util.Future
import io.finch.Endpoint
import yokohama.murataku.trade.http.IllegalPathArgumentException
import yokohama.murataku.trade.lib.date.YearMonth

trait ValueObjectPath {
  val yearMonthPath: Endpoint[YearMonth] =
    io.finch
      .path[String].mapAsync(raw =>
        Future(YearMonth.fromSixNum(raw)).rescue {
          case e: IllegalArgumentException => Future.exception(IllegalPathArgumentException(e.getMessage, e))
      })
}
