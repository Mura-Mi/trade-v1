package yokohama.murataku.trade.historicaldata.database

import java.time.LocalDate

case class LatestFuturePrice(productName: String,
                             date: LocalDate,
                             open: BigDecimal,
                             high: BigDecimal,
                             low: BigDecimal,
                             close: BigDecimal) {}
