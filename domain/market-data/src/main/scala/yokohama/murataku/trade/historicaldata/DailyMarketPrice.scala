package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import yokohama.murataku.trade.product.ProductType

case class DailyMarketPrice(
    date: LocalDate,
    productType: ProductType,
    productName: String,
    open: Option[BigDecimal] = None,
    high: Option[BigDecimal] = None,
    low: Option[BigDecimal] = None,
    close: Option[BigDecimal] = None
)
