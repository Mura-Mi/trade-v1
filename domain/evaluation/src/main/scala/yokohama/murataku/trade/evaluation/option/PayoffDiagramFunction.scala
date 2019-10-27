package yokohama.murataku.trade.evaluation.option

import java.time.LocalDate

import wvlet.log.LogSupport
import yokohama.murataku.trade.evaluation.formula.BlackScholesFormula
import yokohama.murataku.trade.product.indexoption.IndexOption

case class OptionPayoff(atUnderlying: BigDecimal,
                        optionValue: Double,
                        intrinsic: Double)

object PayoffDiagramFunction extends LogSupport {
  def eval(product: IndexOption,
           optionMarketPrice: BigDecimal,
           underlyingPrice: BigDecimal,
           today: LocalDate)(from: BigDecimal,
                             to: BigDecimal,
                             bin: BigDecimal): Seq[OptionPayoff] = {
    val impliedVol = BlackScholesFormula.impliedVol(product.putOrCall,
                                                    underlyingPrice.toDouble,
                                                    product.strike.toDouble,
                                                    product.deliveryDate,
                                                    today,
                                                    optionMarketPrice.toDouble)

    from.to(to, bin).map { underlyingWhen =>
      val optV = BlackScholesFormula.price(product.putOrCall,
                                           underlyingWhen.toDouble,
                                           product.strike.toDouble,
                                           impliedVol,
                                           product.deliveryDate,
                                           today)
      val intrinsic = product.intrinsicValueAt(underlyingWhen).toDouble
      OptionPayoff(underlyingWhen, optV, intrinsic)
    }
  }
}
