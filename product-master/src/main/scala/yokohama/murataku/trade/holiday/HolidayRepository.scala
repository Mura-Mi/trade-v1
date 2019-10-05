package yokohama.murataku.trade.holiday

import com.twitter.util.{Await, Future}
import io.getquill.{FinaglePostgresContext, SnakeCase}
import yokohama.murataku.trade.persistence.PersistenceSupport

class HolidayRepository(ctx: FinaglePostgresContext[SnakeCase])
    extends Calendar
    with PersistenceSupport {
  import ctx._

  def store(holiday: Holiday): Future[Long] = run {
    quote {
      query[Holiday].insert(lift(holiday)).onConflictIgnore(_.date, _.market)
    }
  }

  override val holidays: Seq[Holiday] = Await.result {
    run {
      quote {
        query[Holiday]
      }
    }
  }
}
