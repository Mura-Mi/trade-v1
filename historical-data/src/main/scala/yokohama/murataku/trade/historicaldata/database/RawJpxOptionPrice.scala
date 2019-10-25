package yokohama.murataku.trade.historicaldata.database

import java.time.LocalDate

import yokohama.murataku.trade.historicaldata.DailyMarketPrice
import yokohama.murataku.trade.product.ProductType
import yokohama.murataku.trade.product.ProductType.IndexOption
import yokohama.murataku.trade.product.indexoption.PutOrCall

case class RawJpxOptionPrice(
    productCode: String,
    productType: String,
    deliveryLimit: String,
    strike: BigDecimal,
    note1: String,
    putProductCode: String,
    putClosePrice: BigDecimal,
    putSpare: String,
    putTheoreticalPrice: BigDecimal,
    putVolatility: BigDecimal,
    callProductCode: String,
    callClosePrice: BigDecimal,
    callSpare: String,
    callTheoreticalPrice: BigDecimal,
    callVolatility: BigDecimal,
    underlyingClose: BigDecimal,
    underlyingBaseVolatility: BigDecimal
) {
  def toDatabaseObject(date: LocalDate,
                       poc: PutOrCall): Option[DailyMarketPrice] = {
    Option(if (poc.isCall) callClosePrice else putClosePrice)
      .filter(_ != BigDecimal(0)).map { price =>
        DailyMarketPrice(
          date,
          productType = ProductType.IndexOption,
          productName = this.putProductCode,
          close = Some(price)
        )
      }

  }
}

case class JpxOptionPrice(
    date: LocalDate,
    putOrCall: PutOrCall,
    optionProductCode: String,
    productCode: String,
    productType: String,
    deliveryLimit: String,
    strike: BigDecimal,
    note1: String,
    closePrice: BigDecimal,
    spare: String,
    theoreticalPrice: BigDecimal,
    volatility: BigDecimal,
)
