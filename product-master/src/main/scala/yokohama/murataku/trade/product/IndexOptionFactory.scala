package yokohama.murataku.trade.product

import java.time.DayOfWeek
import java.util.UUID

import yokohama.murataku.trade.holiday.{
  Calendar,
  HolidayAdjustMethod,
  YearMonth
}

class IndexOptionFactory(calendar: Calendar) {
  import calendar._

  def createNew(indexName: IndexName,
                putOrCall: PutOrCall,
                deliveryLimit: YearMonth,
                strike: BigDecimal): IndexOption = {
    val deliveryDate = deliveryLimit
      .find(2, DayOfWeek.FRIDAY)
      .adjust(HolidayAdjustMethod.Preceding)
      .previousBusinessDay()

    IndexOption(
      indexId = UUID.randomUUID(),
      productName = IndexOptionName(indexName, putOrCall, deliveryLimit, strike),
      putOrCall = putOrCall,
      deliveryLimit = deliveryLimit,
      deliveryDate = deliveryDate,
      strike = strike
    )
  }
}
