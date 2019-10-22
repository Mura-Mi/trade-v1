package yokohama.murataku.trade.volatility

import java.time.LocalDate

case class DailyVolatility(date: LocalDate, vol: Double)

object DailyVolatility {
  implicit val ordering: Ordering[DailyVolatility] =
    Ordering.by(_.date.toEpochDay)
}
