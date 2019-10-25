package yokohama.murataku.trade.product.indexfuture

import shapeless.Generic
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.product.IndexName

case class IndexFutureName(value: String) {
  def this(indexName: IndexName, deliveryLimit: YearMonth) =
    this(s"${indexName.value}-${deliveryLimit.toString}")
}

object IndexFutureName {
  //noinspection TypeAnnotation
  implicit val gen = Generic[IndexFutureName]
}