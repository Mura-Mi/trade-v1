package yokohama.murataku.trade.product

import com.twitter.util.Future
import yokohama.murataku.trade.persistence.PersistenceSupport
import yokohama.murataku.trade.persistence.finagle.TmtPersistenceContext
import yokohama.murataku.trade.product.indexoption.{IndexOption, IndexOptionName}

class IndexOptionRepository(ctx: TmtPersistenceContext)
    extends PersistenceSupport
    with Encodings {

  import ctx._

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
