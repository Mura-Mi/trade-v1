package yokohama.murataku.trade.product.indexoption

import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.product.IndexName

case class IndexOptionName(value: String)

object IndexOptionName {
  //noinspection TypeAnnotation
  implicit val gen = shapeless.Generic[IndexOptionName]

  def apply(indexName: IndexName,
            putOrCall: PutOrCall,
            deliveryLimit: YearMonth,
            strike: BigDecimal): IndexOptionName =
    apply(
      s"${indexName.value}-${putOrCall.value}-${deliveryLimit.toString}-$strike")
}