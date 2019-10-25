package yokohama.murataku.trade.evaluation.formula

import java.time.LocalDate

import org.apache.commons.math3.distribution.NormalDistribution
import wvlet.log.{LogLevel, LogSupport}
import yokohama.murataku.trade.product.indexoption.PutOrCall

object BlackScholesFormula extends LogSupport {
  val `365`: Double = 365.0d

  import scala.math._
  private val nd = new NormalDistribution()

  def price(putOrCall: PutOrCall,
            underlying: Double,
            strike: Double,
            vol: Double,
            expiry: LocalDate,
            today: LocalDate): Double =
    innerPrice(putOrCall: PutOrCall,
               underlying: Double,
               strike: Double,
               vol: Double,
               expiry: LocalDate,
               today: LocalDate,
               logLevel = LogLevel.DEBUG)

  private def innerPrice(putOrCall: PutOrCall,
                         underlying: Double,
                         strike: Double,
                         vol: Double,
                         expiry: LocalDate,
                         today: LocalDate,
                         logLevel: LogLevel): Double = {
    val `s/x`: Double = underlying / strike
    val `T-t`: Double = (expiry.toEpochDay - today.toEpochDay).toDouble / `365`
    val `log(s/x)` = log(`s/x`)

    implicit class NumOps(org: Double) {
      def ^(exponent: Double): Double = pow(org, exponent)
    }

    logAt(logLevel, s"log(s/x): ${`log(s/x)`}")

    val d1 = (`log(s/x)` + (vol ^ 2) / 2 * `T-t`) / (vol * sqrt(`T-t`))
    val d2 = (`log(s/x)` - (vol ^ 2) / 2 * `T-t`) / (vol * sqrt(`T-t`))

    logAt(logLevel, s"d1: $d1")
    logAt(logLevel, s"d2: $d2")

    val N: Double => Double = nd.cumulativeProbability

    val factor = putOrCall.factor
    val `N(d1)` = N(factor * d1)
    val `N(d2)` = N(factor * d2)

    logAt(logLevel, s"N(d1): ${`N(d1)`}")
    logAt(logLevel, s"N(d2): ${`N(d2)`}")

    val former = `N(d1)` * underlying
    val latter = `N(d2)` * strike * exp(0 * -`T-t`)

    logAt(logLevel, s"former: $former")
    logAt(logLevel, s"latter: $latter")

    factor * (former - latter)
  }

  def impliedVol(putOrCall: PutOrCall,
                 underlying: Double,
                 strike: Double,
                 expiry: LocalDate,
                 today: LocalDate,
                 price: Double): Double = {
    val bin = 0.0001
    Stream
      .iterate(0.0)(_ + bin)
      .takeWhile(_ <= 1)
      .take((1 / bin).toInt + 1)
      .map(v => {
        val c = this.innerPrice(putOrCall = putOrCall,
                                underlying = underlying,
                                strike = strike,
                                vol = v,
                                expiry = expiry,
                                today = today,
                                logLevel = LogLevel.TRACE)
        logAt(LogLevel.TRACE, c)
        (v, c)
      })
      .minBy { case (_, calcPrice) => abs(calcPrice - price) }
      ._1
  }
}
