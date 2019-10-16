package yokohama.murataku.trade.analysis.vol

import java.time.LocalDate

import com.twitter.util.{Await, Future}
import wvlet.log.LogSupport
import yokohama.murataku.trade.evaluation.HistoricalVolatilityCalculator
import yokohama.murataku.trade.historicaldata.HistoricalPriceRepository
import yokohama.murataku.trade.lib.date._
import yokohama.murataku.trade.persistence.finagle.PersistenceContextProvider

class CalculateHistoricalVolatilityUseCase extends LogSupport {

  def extract(productName: String,
              since: LocalDate,
              to: LocalDate): Future[Seq[(LocalDate, Double)]] = {

    val ctx = PersistenceContextProvider.getContext

    val priceRepo = new HistoricalPriceRepository(ctx)

    val futureHistory =
      priceRepo.fetchFuturePrice(productName, since.minusMonths(3), to)

    info(futureHistory)

    for {
      history <- futureHistory
    } yield {
      history
        .map(_.date)
        .filter(_.isAfter(since))
        .map(date => {
          val volSourceStart = date.minusMonths(3)
          date -> HistoricalVolatilityCalculator.from(
            history
              .filter(_.date.isBetween(volSourceStart, date))
              .map(_.close.toDouble)
          )
        })
        .sortBy(_._1.toEpochDay)
    }
  }
}
