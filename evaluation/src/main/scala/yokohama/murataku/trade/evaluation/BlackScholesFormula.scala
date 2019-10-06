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

    info(s"log(s/x): ${`log(s/x)`}")

    val d1 = (`log(s/x)` + pow(vol.toDouble, 2) / 2 * `T-t`) /
      (vol * sqrt(`T-t`))
    val d2 = (`log(s/x)` - pow(vol.toDouble, 2) / 2 * `T-t`) /
      (vol * sqrt(`T-t`))

    info(s"d1: $d1")
    info(s"d2: $d2")

    val N: Double => Double = nd.cumulativeProbability

    val factor = putOrCall.factor
    val former = N(factor * d1) * underlying.toDouble
    val latter = N(factor * d2) * strike.toDouble * exp(0 * -`T-t`)

    info(s"former: $former")
    info(s"latter: $latter")

    factor * (former - latter)
  }
}
