package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import com.twitter.util.Future
import io.getquill.{FinaglePostgresContext, SnakeCase}
import yokohama.murataku.trade.historicaldata.database.{
  JpxOptionPrice,
  LatestFuturePrice
}
import yokohama.murataku.trade.persistence.PersistenceSupport

class HistoricalPriceRepository(ctx: FinaglePostgresContext[SnakeCase])
    extends PersistenceSupport {
  import ctx._

  def store(productName: String,
            date: LocalDate,
            open: BigDecimal,
            high: BigDecimal,
            low: BigDecimal,
            close: BigDecimal): Future[Long] =
    run {
      quote(
        query[LatestFuturePrice]
          .insert(
            lift(LatestFuturePrice(productName, date, open, high, low, close)))
          .onConflictIgnore(_.productName, _.date)
      )
    }

  def store(jpxOptionPrice: JpxOptionPrice): Future[Long] =
    run {
      quote {
        query[JpxOptionPrice]
          .insert(lift(jpxOptionPrice))
          .onConflictIgnore(_.productCode, _.date)
      }
    }
}
