package yokohama.murataku.trade.holiday

import com.twitter.util.{Await, Future}
import io.getquill.{FinaglePostgresContext, SnakeCase}
import kantan.codecs.strings.java8._
import kantan.csv.DecodeError.TypeError
import kantan.csv._
import kantan.csv.generic._
import kantan.csv.ops._
import yokohama.murataku.trade.lib.batch.StandardBatch

object HolidaySetup extends StandardBatch {
  implicit val marketDecoder: CellDecoder[Market] = CellDecoder.from(f =>
    Market.withValueOpt(f) match {
      case Some(market) => Right(market)
      case None         => Left(TypeError(s"not found: $f"))
  })

  val holidayRepository = new HolidayRepository(
    new FinaglePostgresContext(SnakeCase, "ctx"))

  Await.result {
    Future
      .collect {
        getClass.getClassLoader
          .getResource("holiday.csv")
          .asCsvReader[Holiday](rfc.withHeader())
          .map {
            case Right(holiday) => holidayRepository.store(holiday)
            case Left(e)        => error(e); Future.value(0L)
          }
          .toSeq
      }
      .map(_.sum)
  }
}
