package yokohama.murataku.trade.historicaldata.database

import java.time.LocalDate

import yokohama.murataku.trade.product.indexfuture.IndexFutureName

case class LatestFuturePrice(productName: IndexFutureName,
                             date: LocalDate,
                             open: BigDecimal,
                             high: BigDecimal,
                             low: BigDecimal,
                             close: BigDecimal) {}
