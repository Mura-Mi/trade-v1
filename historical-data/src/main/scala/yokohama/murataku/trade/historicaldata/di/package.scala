package yokohama.murataku.trade.historicaldata

import wvlet.airframe.Design
import yokohama.murataku.trade.historicaldata.database.DailyMarketPriceRepositoryImpl
import yokohama.murataku.trade.holiday.{Calendar, HolidayRepository}
import yokohama.murataku.trade.lib.date.CurrentTimeProvider
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.persistence.finagle.ActualPersistenceContextDesign

package object di {
  val design: Design = wvlet.airframe.newDesign
    .bind[Calendar].to[HolidayRepository]
    .bind[CurrentTimeProvider].toInstance(CurrentTimeProvider.system())
    .bind[DailyMarketPriceRepository[TwFutureTatriaContext]].to[
      DailyMarketPriceRepositoryImpl] + ActualPersistenceContextDesign.design

}
