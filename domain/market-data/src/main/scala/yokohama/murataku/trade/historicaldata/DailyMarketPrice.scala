package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import yokohama.murataku.trade.product.ProductType

case class DailyMarketPrice(
    date: LocalDate,
    productType: ProductType,
    productName: String,
    open: Option[BigDecimal],
    high: Option[BigDecimal],
    low: Option[BigDecimal],
    close: Option[BigDecimal]
)
