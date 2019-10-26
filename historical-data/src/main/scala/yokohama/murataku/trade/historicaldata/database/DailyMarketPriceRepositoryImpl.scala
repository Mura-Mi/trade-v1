package yokohama.murataku.trade.historicaldata.database

import java.time.LocalDate
import java.util.UUID

import com.twitter.util.Future
import yokohama.murataku.trade.historicaldata.{
  DailyMarketPrice,
  DailyMarketPriceRepository
}
import yokohama.murataku.trade.persistence.PersistenceSupport
import yokohama.murataku.trade.persistence.finagle.TmtPersistenceContext
import yokohama.murataku.trade.product.ProductType

class DailyMarketPriceRepositoryImpl
    extends DailyMarketPriceRepository
    with PersistenceSupport {
  private val ctx = wvlet.airframe.bind[TmtPersistenceContext]

  import ctx._

  type Schema = schema.DailyMarketPrice

  override def store(e: DailyMarketPrice): Future[Long] =
    this.store(e.productType,
               e.productName,
               e.date,
               e.open.orNull,
               e.high.orNull,
               e.low.orNull,
               e.close.orNull)

  override def store(productType: ProductType,
                     productName: String,
                     date: LocalDate,
                     open: BigDecimal = null,
                     high: BigDecimal = null,
                     low: BigDecimal = null,
                     close: BigDecimal = null): Future[Long] =
    run {
      quote {
        query[Schema]
          .insert(
            lift(
              schema.DailyMarketPrice(
                UUID.randomUUID(),
                date,
                productType,
                productName,
                Option(open),
                Option(high),
                Option(low),
                Option(close),
                null
              ))).onConflictIgnore(_.date, _.productType, _.productName)
      }
    }

  override def find(productType: ProductType,
                    productName: String,
                    date: LocalDate): Future[Option[DailyMarketPrice]] =
    run {
      quote {
        query[schema.DailyMarketPrice].filter(
          row =>
            row.productName == lift(productName) &&
              row.productType == lift(productType) &&
              row.date == lift(date)
        )
      }
    }.map(_.headOption.map(_.toDomain))

  override def select(productType: ProductType,
                      productName: String,
                      since: LocalDate,
                      until: LocalDate): Future[Seq[DailyMarketPrice]] = {
    import yokohama.murataku.trade.lib.date._
    run {
      quote {
        query[schema.DailyMarketPrice]
          .filter(row =>
            row.productName == lift(productName) && row.productType == lift(
              productType))
          //          .filter(_.date >= since).filter(_.date <= until)
          .sortBy(_.date)
      }
    }.map(_.filter(row => row.date.isBetween(since, until))) // remove when infix comparison works
      .map(_.map(_.toDomain))
  }
}