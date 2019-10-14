package yokohama.murataku.trade.analysis

import java.time.LocalDate

import yokohama.murataku.trade.historicaldata.HistoricalPriceRepository
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.persistence.finagle.PersistenceContextProvider

object FutureHistoricalPriceViewer extends StandardBatch {
  val ctx = PersistenceContextProvider.getContext

  val priceRepo = new HistoricalPriceRepository(ctx)

  val today = LocalDate.now

  val history = priceRepo.fetchFuturePrice("NK225", today.minusYears(3), today)
}
