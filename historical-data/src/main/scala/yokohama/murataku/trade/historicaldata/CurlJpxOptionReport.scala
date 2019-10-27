package yokohama.murataku.trade.historicaldata

import java.time.{LocalDate, LocalDateTime}

import com.twitter.util.{Await, Future}
import yokohama.murataku.trade.holiday.{Calendar, HolidayAdjustMethod}
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.persistence.finagle.ActualPersistenceContextDesign

object CurlJpxOptionReport extends StandardBatch {

  val design = di.design + ActualPersistenceContextDesign.design

  design.build[Calendar] { calendar =>
    {
      import calendar._

      val dayOne = args.headOption
        .map(LocalDate.parse(_))
        .getOrElse(
          LocalDateTime.now
            .minusHours(18)
            .toLocalDate
            .adjust(HolidayAdjustMethod.Preceding)) // 多分ここまでには発表されてるはずdesign.build[Calendar] {calendar => {

      val maybeDayTwo = args.drop(1).headOption.map(LocalDate.parse(_))

      design.build[CurlJpxOptionReportUseCase] { uc =>
        import yokohama.murataku.trade.lib.date._

        Await.result {
          (maybeDayTwo match {
            case None => uc.run(dayOne)
            case Some(dayTwo) =>
              Future.collect {
                (dayOne to dayTwo).filter(calendar.isBusinessDay).map(uc.run)
              }
          }).unit
        }
      }
    }
  }
}
