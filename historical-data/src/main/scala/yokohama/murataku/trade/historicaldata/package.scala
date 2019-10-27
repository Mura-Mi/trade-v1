package yokohama.murataku.trade

import yokohama.murataku.trade.historicaldata.database.DailyMarketPriceRepositoryImpl
import yokohama.murataku.trade.persistence.TwFutureTatriaContext

package object historicaldata {
  lazy val design = wvlet.airframe.newDesign
    .bind[DailyMarketPriceRepository[TwFutureTatriaContext]].to[
      DailyMarketPriceRepositoryImpl]
}
