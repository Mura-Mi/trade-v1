package yokohama.murataku.trade.analysis

import java.time.LocalDate

import com.twitter.util.Await
import yokohama.murataku.trade.evaluation.HistoricalVolatilityCalculator
import yokohama.murataku.trade.historicaldata.HistoricalPriceRepository
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.persistence.finagle.PersistenceContextProvider
import yokohama.murataku.trade.lib.date._

object FutureHistoricalPriceViewer extends StandardBatch {
  val ctx = PersistenceContextProvider.getContext

  val priceRepo = new HistoricalPriceRepository(ctx)

  val today = LocalDate.now

  info(today)
  val volHistStart = today.minusYears(3)

  val futureHistory =
    priceRepo.fetchFuturePrice("NK225", volHistStart.minusMonths(3), today)

  info(futureHistory)

  Await.result {
    for {
      history <- futureHistory
    } yield {
      history
        .map(_.date)
        .filter(_.isAfter(volHistStart))
        .map(date => {
          val volSourceStart = date.minusMonths(3)
          date -> HistoricalVolatilityCalculator.from(
            history
              .filter(_.date.isBetween(volSourceStart, date))
              .map(_.close.toDouble)
          )
        })
        .sortBy(_._1.toEpochDay)
        .foreach(info(_))
    }
  }
}
