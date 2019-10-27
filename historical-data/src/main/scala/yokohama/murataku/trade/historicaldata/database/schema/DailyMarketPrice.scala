package yokohama.murataku.trade.historicaldata.database.schema

import java.time.{LocalDate, ZonedDateTime}
import java.util.UUID

import yokohama.murataku.trade.historicaldata.{DailyMarketPrice => DomainModel}
import yokohama.murataku.trade.product.ProductType

case class DailyMarketPrice(
    id: UUID,
    date: LocalDate,
    productType: ProductType,
    productName: String,
    open: Option[BigDecimal],
    high: Option[BigDecimal],
    low: Option[BigDecimal],
    close: Option[BigDecimal],
    createdAt: Option[ZonedDateTime]
) {
  def toDomain: DomainModel =
    DomainModel(date, productType, productName, open, high, low, close)
}
