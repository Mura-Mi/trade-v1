package yokohama.murataku.trade.holiday

import com.twitter.util.{Await, Future}
import kantan.csv.DecodeError.TypeError
import kantan.csv._
import kantan.codecs.strings.java8._
import kantan.csv.ops._
import kantan.csv.generic._
import wvlet.log.LogSupport

object HolidaySetup extends App with LogSupport {
  implicit val marketDecoder: CellDecoder[Market] = CellDecoder.from(f =>
    Market.withValueOpt(f) match {
      case Some(market) => Right(market)
      case None         => Left(TypeError(s"not found: $f"))
  })

  val holidayRepository = new HolidayRepository

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
