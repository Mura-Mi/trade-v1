package yokohama.murataku.trade.analysis.vol

import java.time.LocalDate

import com.twitter.util.Await
import yokohama.murataku.trade.evaluation.HistoricalVolatilityCalculator
import yokohama.murataku.trade.historicaldata.HistoricalPriceRepository
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.persistence.finagle.PersistenceContextProvider

object FutureHistoricalPriceViewer extends StandardBatch {
  val today = LocalDate.now
  info(today)
  val volHistStart = today.minusYears(3)

  Await.result {
    for {
      volatility <- new CalculateHistoricalVolatilityUseCase()
        .extract("NK225", volHistStart, today)
    } yield {
      volatility.foreach(info(_))
    }
  }
}
