package yokohama.murataku.trade.product.indexoption

import java.time.LocalDate
import java.util.UUID

import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.product.{IndexName, NumUtil, Product}

case class IndexOption(id: UUID,
                       indexName: IndexName,
                       productName: IndexOptionName,
                       putOrCall: PutOrCall,
                       deliveryLimit: YearMonth,
                       deliveryDate: LocalDate,
                       strike: BigDecimal)
  extends Product {
  def intrinsicValueAt(price: BigDecimal): BigDecimal = {
    import NumUtil._
    max((price - strike) * putOrCall.factor, 0)
  }
}
