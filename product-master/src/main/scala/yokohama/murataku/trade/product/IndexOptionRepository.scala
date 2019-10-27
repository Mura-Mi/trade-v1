package yokohama.murataku.trade.product

import com.twitter.util.Future
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.PersistenceSupport
import yokohama.murataku.trade.persistence.finagle.TmtPersistenceContext
import yokohama.murataku.trade.product.indexoption.{
  IndexOption,
  IndexOptionName,
  PutOrCall
}

class IndexOptionRepository(ctx: TmtPersistenceContext)
    extends PersistenceSupport
    with Encodings {

  import ctx._

  def findBy(strike: BigDecimal,
             putOrCall: PutOrCall,
             deliveryLimit: YearMonth): Future[IndexOption] =
    run {
      quote {
        query[IndexOption].filter(
          row =>
            row.strike == lift(strike) &&
              row.putOrCall == lift(putOrCall) &&
              row.deliveryLimit == lift(deliveryLimit))
      }
    }.map(_.headOption.getOrElse(throw new IllegalArgumentException(
      s"not found: [$strike $putOrCall $deliveryLimit]")))

  def find(productName: IndexOptionName): Future[IndexOption] =
    run {
      quote {
        query[IndexOption].filter(_.productName == lift(productName))
      }
    }.map(_.headOption.getOrElse(
      throw new IllegalArgumentException(productName.value)))

  def store(indexOption: IndexOption): Future[Long] =
    run {
      quote {
        query[IndexOption]
          .insert(lift(indexOption))
          .onConflictIgnore(_.indexName, _.putOrCall, _.deliveryLimit, _.strike)
      }
    }
}
