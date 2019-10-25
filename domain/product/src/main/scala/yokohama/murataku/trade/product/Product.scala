package yokohama.murataku.trade.product

trait Product

trait NumUtil {
  def max(a: BigDecimal, b: BigDecimal): BigDecimal =
    if (a > b) a else b
}

object NumUtil extends NumUtil
