package yokohama.murataku.trade.volatility

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.log.LogSupport
import yokohama.murataku.trade.evaluation.formula.HistoricalVolatilityFormula
import yokohama.murataku.trade.historicaldata.DailyMarketPriceRepository
import yokohama.murataku.trade.lib.date._
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.ProductType

class CalculateHistoricalVolatilityUseCase(
    implicit twFutureTatriaContext: TwFutureTatriaContext,
    marketPriceRepository: DailyMarketPriceRepository[TwFutureTatriaContext]
) extends LogSupport {

  def extract(productType: ProductType,
              productName: String,
              since: LocalDate,
              to: LocalDate): Future[Seq[DailyVolatility]] = {

    val futureHistory =
      marketPriceRepository.select(productType,
                                   productName,
                                   since.minusMonths(3),
                                   to)

    for {
      history <- futureHistory
    } yield {
      history
        .map(_.date)
        .filter(_.isAfter(since))
        .map(date => {
          val volSourceStart = date.minusMonths(3)
          val vol = HistoricalVolatilityFormula.from(
            history
              .filter(_.date.isBetween(volSourceStart, date))
              .flatMap(_.close).map(_.toDouble)
          )
          DailyVolatility(date, vol)
        })
        .sorted
    }
  }.underlying
}
