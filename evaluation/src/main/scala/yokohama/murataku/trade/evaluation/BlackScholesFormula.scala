package yokohama.murataku.trade.evaluation

import java.time.LocalDate

import org.apache.commons.math3.distribution.NormalDistribution
import wvlet.log.LogSupport
import yokohama.murataku.trade.product.PutOrCall

object BlackScholesFormula extends LogSupport {
  val `365`: Double = 365.0d

  import scala.math._
  private val nd = new NormalDistribution()

  def price(putOrCall: PutOrCall,
            underlying: BigDecimal,
            strike: BigDecimal,
            vol: Double,
            expiry: LocalDate,
            today: LocalDate): Double = {
    val `s/x`: Double = underlying.toDouble / strike.toDouble
    val `T-t`: Double = (expiry.toEpochDay - today.toEpochDay).toDouble / `365`
    val `log(s/x)` = log(`s/x`)

    implicit class NumOps(org: Double) {
      def ^(exponent: Double): Double = pow(org, exponent)
    }

    debug(s"log(s/x): ${`log(s/x)`}")

    val d1 = (`log(s/x)` + (vol.toDouble ^ 2 / 2 * `T-t`)) /
      (vol * sqrt(`T-t`))
    val d2 = (`log(s/x)` - (vol.toDouble ^ 2 / 2 * `T-t`)) /
      (vol * sqrt(`T-t`))

    debug(s"d1: $d1")
    debug(s"d2: $d2")

    val N: Double => Double = nd.cumulativeProbability

    val factor = putOrCall.factor
    val former = N(factor * d1) * underlying.toDouble
    val latter = N(factor * d2) * strike.toDouble * exp(0 * -`T-t`)

    debug(s"former: $former")
    debug(s"latter: $latter")

    factor * (former - latter)
  }
}
