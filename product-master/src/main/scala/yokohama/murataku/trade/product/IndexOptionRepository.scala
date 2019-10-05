package yokohama.murataku.trade.product

import com.twitter.util.Future
import io.getquill.{FinaglePostgresContext, SnakeCase}
import yokohama.murataku.trade.persistence.PersistenceSupport

class IndexOptionRepository(ctx: FinaglePostgresContext[SnakeCase])
    extends PersistenceSupport {
  import ctx._
  def store(indexOption: IndexOption): Future[Long] =
    run {
      quote {
        query[IndexOption]
          .insert(lift(indexOption))
          .onConflictIgnore(_.indexName, _.putOrCall, _.deliveryLimit, _.strike)
      }
    }
}
