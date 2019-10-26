package yokohama.murataku.trade.historicaldata

import java.time.{LocalDate, LocalDateTime}

import com.twitter.util.Await
import yokohama.murataku.trade.holiday.{Calendar, HolidayAdjustMethod}
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.persistence.finagle.ActualPersistenceContextDesign

object CurlJpxOptionReport extends StandardBatch {

  val design = di.design + ActualPersistenceContextDesign.design

  design.build[Calendar] { calendar =>
    {
      import calendar._
      val today = args.headOption
        .map(LocalDate.parse(_))
        .getOrElse(
          LocalDateTime.now
            .minusHours(18)
            .toLocalDate
            .adjust(HolidayAdjustMethod.Preceding)) // 多分ここまでには発表されてるはずdesign.build[Calendar] {calendar => {

      design.build[CurlJpxOptionReportUseCase] { uc =>
        Await.result {
          uc.run(today)
        }
      }
    }
  }
}
