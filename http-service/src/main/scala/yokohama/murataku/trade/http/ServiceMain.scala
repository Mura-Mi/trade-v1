package yokohama.murataku.trade.http

import com.twitter.finagle.Http
import com.twitter.util.Await
import io.circe.generic.auto._
import io.circe.{Encoder, Json}
import io.finch.circe._
import wvlet.log.Logger
import yokohama.murataku.trade.holiday.{Calendar, HolidayRepository}
import yokohama.murataku.trade.http.filters.{Filters, OptionRouting}
import yokohama.murataku.trade.lib.date.{CurrentTimeProvider, YearMonth}
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.persistence.finagle.ActualPersistenceContextDesign
import yokohama.murataku.trade.product.{IndexName, ListProductForDeliveryUseCase}
import yokohama.murataku.trade.product.indexoption.PutOrCall
import yokohama.murataku.trade.{historicaldata, product}

object ServiceMain extends StandardHttpService {
  Logger.scheduleLogLevelScan
  val design =
    ActualPersistenceContextDesign.design +
      product.design + historicaldata.design
      .bind[CurrentTimeProvider].toInstance(CurrentTimeProvider.system())
      .bind[Calendar].to[HolidayRepository]
      .bind[ListProductForDeliveryUseCase[TwFutureTatriaContext]].toSingleton

  implicit val encoderPoC: Encoder[PutOrCall] = new Encoder[PutOrCall] {
    override def apply(a: PutOrCall): Json = Json.fromString(a.value)
  }
  implicit val encoderYearMonth: Encoder[YearMonth] = new Encoder[YearMonth] {
    override def apply(a: YearMonth): Json = Json.fromString(a.toString)
  }

  class Routes(
      val a: HealthCheckRouting,
      val b: ProductRouting,
      val c: AnalysisRouting,
      val d: OptionRouting
  ) {
    //noinspection TypeAnnotation
    def ep = a.ep :+: b.ep :+: c.ep :+: d.ep
  }

  val router = design.build[Routes] { routes =>
    val service = Filters.AccessLogging andThen Filters.HeaderAdding andThen routes.ep.toService //andThen Filters.TmtNotFound
    Await.ready {
      Http.server.serve(":8080", service)
    }
  }
}
