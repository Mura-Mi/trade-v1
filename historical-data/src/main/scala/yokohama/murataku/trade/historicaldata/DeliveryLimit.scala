package yokohama.murataku.trade.historicaldata

import java.time.Month

/** 限月 */
trait DeliveryLimit {

}

object DeliveryLimit {
  case object Latest extends DeliveryLimit
  case class DeliveryMonth(year: Int, month: Month)
}
