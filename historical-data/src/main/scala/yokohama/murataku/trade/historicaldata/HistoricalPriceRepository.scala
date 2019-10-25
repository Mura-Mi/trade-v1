package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import com.twitter.util.Future
import io.getquill.{FinaglePostgresContext, SnakeCase}
import yokohama.murataku.trade.historicaldata.database.{
  JpxOptionPrice,
  LatestFuturePrice
}
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.PersistenceSupport
import yokohama.murataku.trade.persistence.finagle.TmtPersistenceContext
import yokohama.murataku.trade.product.indexfuture.IndexFutureName
import yokohama.murataku.trade.product.indexoption.PutOrCall

class HistoricalPriceRepository(ctx: TmtPersistenceContext)
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
            lift(
              LatestFuturePrice(IndexFutureName(productName),
                                date,
                                open,
                                high,
                                low,
                                close)))
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

  def fetchFuturePrice(productName: IndexFutureName,
                       from: LocalDate = null,
                       to: LocalDate = null): Future[Seq[LatestFuturePrice]] = {
    val actFrom = Option(from).getOrElse(LocalDate.of(2000, 1, 1))
    val actTo = Option(to).getOrElse(LocalDate.of(2100, 12, 31))

    run {
      quote {
        query[LatestFuturePrice]
          .filter(_.productName == lift(productName))
        // TODO うまく行かないので一旦全フェッチ
        //          .filter(_.date >= lift(actFrom))
        //          .filter(_.date <= lift(actTo))
      }
    }
  }

  def fetchOptionPrice(date: LocalDate,
                       putOrCall: PutOrCall,
                       deliveryLimit: YearMonth,
                       optionProductCode: String,
                       strike: BigDecimal): Future[JpxOptionPrice] = {
    run {
      quote {
        query[JpxOptionPrice].filter(
          row =>
            row.date == lift(date)
              && row.putOrCall == lift(putOrCall)
              && row.deliveryLimit == lift(deliveryLimit.toStringWithoutSlash)
              && row.optionProductCode == lift(optionProductCode)
              && row.strike == lift(strike))
      }
    }.map(_.head)
  }
}
