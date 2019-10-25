package yokohama.murataku.trade.evaluation.option

import java.time.LocalDate

import wvlet.log.LogSupport
import yokohama.murataku.trade.evaluation.formula.BlackScholesFormula
import yokohama.murataku.trade.evaluation.option.GreeksFormula.{Delta, Theta, Vega}
import yokohama.murataku.trade.holiday.Calendar
import yokohama.murataku.trade.product.indexoption.IndexOption

object OptionEvaluationFunction extends LogSupport {
  def apply(product: IndexOption,
            optionMarketPrice: BigDecimal,
            underlyingPrice: BigDecimal,
            today: LocalDate)(implicit cal: Calendar): OptionValuationSet = {
    debug(s"price: $optionMarketPrice")
    val impliedVol = BlackScholesFormula.impliedVol(product.putOrCall,
                                                    underlyingPrice.toDouble,
                                                    product.strike.toDouble,
                                                    product.deliveryDate,
                                                    today,
                                                    optionMarketPrice.toDouble)

    val sigma = 10.0
    val deltaUp = BlackScholesFormula.price(product.putOrCall,
                                            underlyingPrice.toDouble + sigma,
                                            product.strike.toDouble,
                                            impliedVol,
                                            product.deliveryDate,
                                            today)
    val deltaDown = BlackScholesFormula.price(product.putOrCall,
                                              underlyingPrice.toDouble - sigma,
                                              product.strike.toDouble,
                                              impliedVol,
                                              product.deliveryDate,
                                              today)
    val delta =
      OptionGreeks(Delta(sigma, DeltaWay.BothSide),
                   (deltaUp - deltaDown) / (sigma * 2))
    debug(s"deltaUp: $deltaUp")
    debug(s"deltaDown: $deltaDown")
    debug(s"delta: $delta")

    val vegaSigma = 0.01
    val vegaUp = BlackScholesFormula.price(product.putOrCall,
                                           underlyingPrice.toDouble,
                                           product.strike.toDouble,
                                           impliedVol + vegaSigma,
                                           product.deliveryDate,
                                           today)
    val vegaDown = BlackScholesFormula.price(product.putOrCall,
                                             underlyingPrice.toDouble,
                                             product.strike.toDouble,
                                             impliedVol - vegaSigma,
                                             product.deliveryDate,
                                             today)
    val vega =
      OptionGreeks(Vega(vegaSigma, DeltaWay.BothSide), (vegaUp - vegaDown) / 2)

    import cal._
    val `t+1` = BlackScholesFormula.price(product.putOrCall,
                                          underlyingPrice.toDouble,
                                          product.strike.toDouble,
                                          impliedVol,
                                          product.deliveryDate,
                                          today.nextBusinessDay())
    val theta =
      OptionGreeks(Theta(1, ThetaUnit.BusinessDay), `t+1` - optionMarketPrice)
    debug(s"T+1: ${`t+1`}")
    debug(s"theta: $theta")

    OptionValuationSet(product.id,
                       today,
                       optionMarketPrice,
                       GreeksSet(Seq(delta, vega, theta)))
  }
}
