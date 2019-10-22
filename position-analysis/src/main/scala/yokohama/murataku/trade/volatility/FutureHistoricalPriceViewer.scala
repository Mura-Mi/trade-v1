package yokohama.murataku.trade.volatility

import java.time.LocalDate

import com.twitter.util.Await
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.persistence.finagle.ActualPersistenceContextDesign

object FutureHistoricalPriceViewer extends StandardBatch {

  ActualPersistenceContextDesign.design
    .build[CalculateHistoricalVolatilityUseCase] { uc =>
      val today = LocalDate.now
      info(today)
      val volHistStart = today.minusYears(3)

      Await.result {
        for {
          volatility <- uc.extract("NK225", volHistStart, today)
        } yield {
          volatility.foreach(info(_))
        }
      }
    }
}
