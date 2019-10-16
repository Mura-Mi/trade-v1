package yokohama.murataku.trade.analysis.vol

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.log.LogSupport
import yokohama.murataku.trade.evaluation.HistoricalVolatilityCalculator
import yokohama.murataku.trade.historicaldata.HistoricalPriceRepository
import yokohama.murataku.trade.lib.date._
import yokohama.murataku.trade.persistence.finagle.TmtPersistenceContext

class CalculateHistoricalVolatilityUseCase(
    persistenceContext: TmtPersistenceContext
) extends LogSupport {

  def extract(productName: String,
              since: LocalDate,
              to: LocalDate): Future[Seq[DailyVolatility]] = {

    val priceRepo = new HistoricalPriceRepository(persistenceContext)

    val futureHistory =
      priceRepo.fetchFuturePrice(productName, since.minusMonths(3), to)

    for {
      history <- futureHistory
    } yield {
      history
        .map(_.date)
        .filter(_.isAfter(since))
        .map(date => {
          val volSourceStart = date.minusMonths(3)
          val vol = HistoricalVolatilityCalculator.from(
            history
              .filter(_.date.isBetween(volSourceStart, date))
              .map(_.close.toDouble)
          )
          DailyVolatility(date, vol)
        })
        .sorted
    }
  }
}
