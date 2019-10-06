package yokohama.murataku.trade.evaluation

import java.time.LocalDate

import yokohama.murataku.trade.product.PutOrCall

object BlackScholesFormula {

  import scala.math._

  def price(putOrCall: PutOrCall,
            underlying: BigDecimal,
            strike: BigDecimal,
            vol: BigDecimal,
            expiry: LocalDate,
            today: LocalDate): BigDecimal = {
    val `s/x` = strike.toDouble / underlying.toDouble
    val `T-t` = expiry.toEpochDay - today.toEpochDay

    val d1 = (log(`s/x`) + pow(vol.toDouble, 2) * `T-t`) / (vol * sqrt(`T-t`))
    val d2 = (log(`s/x`) - pow(vol.toDouble, 2) * `T-t`) / (vol * sqrt(`T-t`))

    ???
  }
}
