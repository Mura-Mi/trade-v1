package yokohama.murataku.trade.product.indexfuture

import java.time.LocalDate
import java.util.UUID

import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.product.{IndexName, Product}

case class IndexFuture(id: UUID,
                       indexName: IndexName,
                       productName: IndexFutureName,
                       deliveryLimit: YearMonth,
                       deliveryDate: LocalDate)
  extends Product
