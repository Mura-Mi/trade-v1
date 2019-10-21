package yokohama.murataku.trade.evaluation.option

import java.time.LocalDate

import yokohama.murataku.trade.evaluation.formula.BlackScholesFormula
import yokohama.murataku.trade.evaluation.option.GreeksFormula.{
  Delta,
  Theta,
  Vega
}
import yokohama.murataku.trade.holiday.Calendar
import yokohama.murataku.trade.product.IndexOption

object OptionEvaluationFunction {
  def apply(product: IndexOption,
            optionMarketPrice: BigDecimal,
            underlyingPrice: BigDecimal,
            today: LocalDate)(implicit cal: Calendar): OptionValuationSet = {
    val impliedVol = BlackScholesFormula.impliedVol(product.putOrCall,
                                                    underlyingPrice.toDouble,
                                                    product.strike.toDouble,
                                                    product.deliveryDate,
                                                    today,
                                                    optionMarketPrice.toDouble)

    val sigma = 10
    val deltaUp = BlackScholesFormula.price(product.putOrCall,
                                            underlyingPrice.toDouble + sigma,
                                            product.strike.toDouble,
                                            impliedVol,
                                            product.deliveryDate,
                                            today)
    val deltaDown = BlackScholesFormula.price(product.putOrCall,
                                              underlyingPrice.toDouble + sigma,
                                              product.strike.toDouble,
                                              impliedVol,
                                              product.deliveryDate,
                                              today)
    val delta =
      OptionGreeks(Delta(sigma, DeltaWay.BothSide),
                   (deltaUp - deltaDown) / sigma * 2)

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

    OptionValuationSet(product.id,
                       today,
                       optionMarketPrice,
                       Seq(delta, vega, theta))
  }
}
