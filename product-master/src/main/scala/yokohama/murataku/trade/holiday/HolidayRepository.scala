package yokohama.murataku.trade.holiday

import com.twitter.util.{Await, Future}
import io.getquill.{FinaglePostgresContext, SnakeCase}

class HolidayRepository extends Calendar {

  val ctx = new FinaglePostgresContext(SnakeCase, "ctx")
  import ctx._
  implicit val a: MappedEncoding[String, Market] =
    MappedEncoding[String, Market](Market.withValue)
  implicit val b: MappedEncoding[Market, String] =
    MappedEncoding[Market, String](_.value)

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
