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
            underlying: Double,
            strike: Double,
            vol: Double,
            expiry: LocalDate,
            today: LocalDate): Double = {
    val `s/x`: Double = underlying / strike
    val `T-t`: Double = (expiry.toEpochDay - today.toEpochDay).toDouble / `365`
    val `log(s/x)` = log(`s/x`)

    implicit class NumOps(org: Double) {
      def ^(exponent: Double): Double = pow(org, exponent)
    }

    debug(s"log(s/x): ${`log(s/x)`}")

    val d1 = (`log(s/x)` + (vol ^ 2) / 2 * `T-t`) / (vol * sqrt(`T-t`))
    val d2 = (`log(s/x)` - (vol ^ 2) / 2 * `T-t`) / (vol * sqrt(`T-t`))

    debug(s"d1: $d1")
    debug(s"d2: $d2")

    val N: Double => Double = nd.cumulativeProbability

    val factor = putOrCall.factor
    val `N(d1)` = N(factor * d1)
    val `N(d2)` = N(factor * d2)

    debug(s"N(d1): ${`N(d1)`}")
    debug(s"N(d2): ${`N(d2)`}")

    val former = `N(d1)` * underlying
    val latter = `N(d2)` * strike * exp(0 * -`T-t`)

    debug(s"former: $former")
    debug(s"latter: $latter")

    factor * (former - latter)
  }
}
