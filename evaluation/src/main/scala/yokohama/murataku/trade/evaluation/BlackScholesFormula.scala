package yokohama.murataku.trade.evaluation

import java.time.LocalDate

import org.apache.commons.math3.distribution.NormalDistribution
import yokohama.murataku.trade.product.PutOrCall

object BlackScholesFormula {

  import scala.math._

  def price(putOrCall: PutOrCall,
            underlying: BigDecimal,
            strike: BigDecimal,
            vol: Double,
            expiry: LocalDate,
            today: LocalDate): BigDecimal = {
    val `s/x`: Double = strike.toDouble / underlying.toDouble
    val `T-t`: Double = expiry.toEpochDay - today.toEpochDay

    val d1 = (log(`s/x`) + pow(vol.toDouble, 2) * `T-t`) / (vol * sqrt(`T-t`))
    val d2 = (log(`s/x`) - pow(vol.toDouble, 2) * `T-t`) / (vol * sqrt(`T-t`))

    new NormalDistribution().probability(d1) * underlying

    ???
  }
}
