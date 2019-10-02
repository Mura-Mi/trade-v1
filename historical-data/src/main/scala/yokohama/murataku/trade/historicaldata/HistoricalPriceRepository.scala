package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import com.twitter.util.Future
import io.getquill.{FinaglePostgresContext, SnakeCase}
import yokohama.murataku.trade.historicaldata.database.LatestFuturePrice

class HistoricalPriceRepository {
  val ctx = new FinaglePostgresContext(SnakeCase, "ctx")
  import ctx._
  def store(productName: String,
            date: LocalDate,
            open: BigDecimal,
            high: BigDecimal,
            low: BigDecimal,
            close: BigDecimal): Future[Long] =
    run {
      quote(
        query[LatestFuturePrice].insert(
          lift(LatestFuturePrice(productName, date, open, high, low, close)))
      )
    }
}
