package yokohama.murataku.trade.product

import java.time.LocalDate
import java.util.UUID

import yokohama.murataku.trade.holiday.YearMonth

trait Product

case class Index(id: UUID, name: String)

case class IndexFuture(indexId: UUID,
                       productName: String,
                       deliveryLimit: YearMonth,
                       deliveryDate: LocalDate)
    extends Product

case class IndexOption(indexId: UUID,
                       productName: String,
                       putOrCall: PutOrCall,
                       deliveryLimit: String,
                       strike: BigDecimal)
    extends Product
