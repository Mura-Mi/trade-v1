package yokohama.murataku.trade.holiday

import com.twitter.util.{Await, Future}
import yokohama.murataku.trade.persistence.PersistenceSupport
import yokohama.murataku.trade.persistence.finagle.TmtPersistenceContext

class HolidayRepository(ctx: TmtPersistenceContext)
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
