package yokohama.murataku.trade.historicaldata.database

import java.time.LocalDate

import yokohama.murataku.trade.historicaldata.PutOrCall

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
  def toDatabaseObject(date: LocalDate, poc: PutOrCall): JpxOptionPrice = {
    JpxOptionPrice(
      date = date,
      putOrCall = poc,
      optionProductCode = this.productCode,
      productCode = if (poc.isCall) callProductCode else putProductCode,
      productType = productType,
      deliveryLimit = deliveryLimit,
      strike = strike,
      note1 = note1,
      closePrice = if (poc.isCall) callClosePrice else putClosePrice,
      spare = if (poc.isCall) callSpare else putSpare,
      theoreticalPrice =
        if (poc.isCall) callTheoreticalPrice else putTheoreticalPrice,
      volatility = if (poc.isCall) callVolatility else putVolatility,
    )
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
