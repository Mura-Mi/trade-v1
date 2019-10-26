package yokohama.murataku.trade.volatility

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.log.LogSupport
import yokohama.murataku.trade.evaluation.formula.HistoricalVolatilityFormula
import yokohama.murataku.trade.historicaldata.HistoricalPriceRepository
import yokohama.murataku.trade.lib.date._
import yokohama.murataku.trade.persistence.finagle.TmtPersistenceContext
import yokohama.murataku.trade.product.indexfuture.IndexFutureName

class CalculateHistoricalVolatilityUseCase(
    persistenceContext: TmtPersistenceContext
) extends LogSupport {

  def extract(productName: IndexFutureName,
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
          val vol = HistoricalVolatilityFormula.from(
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