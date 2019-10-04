package yokohama.murataku.trade.product

import java.time.LocalDate
import java.util.UUID

import shapeless.Generic
import yokohama.murataku.trade.holiday.YearMonth

trait Product

case class Index(id: UUID, name: String)
case class IndexName(value: String)

//noinspection TypeAnnotation
object IndexName {
  implicit val gen = Generic[IndexName]
}

case class IndexFuture(id: UUID,
                       indexId: UUID,
                       productName: IndexFutureName,
                       deliveryLimit: YearMonth,
                       deliveryDate: LocalDate)
    extends Product

case class IndexFutureName(value: String) {
  def this(indexName: IndexName, deliveryLimit: YearMonth) =
    this(s"${indexName.value}-${deliveryLimit.toString}")
}

object IndexFutureName {
  implicit val gen = Generic[IndexFutureName]
}

case class IndexOption(indexId: UUID,
                       productName: IndexOptionName,
                       putOrCall: PutOrCall,
                       deliveryLimit: YearMonth,
                       deliveryDate: LocalDate,
                       strike: BigDecimal)
    extends Product
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
