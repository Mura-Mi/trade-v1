package yokohama.murataku.trade.volatility

import java.time.LocalDate

import com.twitter.util.Await
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.persistence.finagle.ActualPersistenceContextDesign
import yokohama.murataku.trade.product.indexfuture.IndexFutureName

object FutureHistoricalPriceViewer extends StandardBatch {

  ActualPersistenceContextDesign.design
    .build[CalculateHistoricalVolatilityUseCase] { uc =>
      val today = LocalDate.now
      info(today)
      val volHistStart = today.minusYears(3)

      Await.result {
        for {
          volatility <- uc.extract(IndexFutureName("NK225"),
                                   volHistStart,
                                   today)
        } yield {
          volatility.foreach(info(_))
        }
      }
    }
}