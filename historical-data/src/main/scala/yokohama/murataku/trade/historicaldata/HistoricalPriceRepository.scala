package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import com.twitter.util.Future
import io.getquill.{FinaglePostgresContext, SnakeCase}
import yokohama.murataku.trade.historicaldata.database.{
  JpxOptionPrice,
  LatestFuturePrice
}
import yokohama.murataku.trade.product.PutOrCall

class HistoricalPriceRepository {
  val ctx = new FinaglePostgresContext(SnakeCase, "ctx")
  import ctx._

  implicit val a: MappedEncoding[String, PutOrCall] =
    MappedEncoding[String, PutOrCall](PutOrCall.withValue)
  implicit val b: MappedEncoding[PutOrCall, String] =
    MappedEncoding[PutOrCall, String](_.value)

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
          .onConflictUpdate(_.productName, _.date)(
            (t, e) => t.open -> e.open,
            (t, e) => t.high -> e.high,
            (t, e) => t.low -> e.low,
            (t, e) => t.close -> e.close
          )
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
