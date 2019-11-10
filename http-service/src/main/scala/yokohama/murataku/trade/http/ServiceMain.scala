package yokohama.murataku.trade.http

import wvlet.airframe.http.Router
import wvlet.airframe.http.finagle.FinagleServer.FinagleService
import wvlet.airframe.http.finagle._
import wvlet.log.Logger
import yokohama.murataku.trade.holiday.{Calendar, HolidayRepository}
import yokohama.murataku.trade.http.filters.{Filters, OptionRouting}
import yokohama.murataku.trade.lib.date.CurrentTimeProvider
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.persistence.finagle.ActualPersistenceContextDesign
import yokohama.murataku.trade.product.ListProductForDeliveryUseCase
import yokohama.murataku.trade.{historicaldata, product}

object ServiceMain extends StandardHttpService {
  Logger.scheduleLogLevelScan
  val router = Router
    .add[HealthCheckRouting]
    .add[ProductRouting]
    .add[AnalysisRouting]
    .add[OptionRouting]

  trait CustomFinagleServerFactory extends FinagleServerFactory {
    override def newService(finagleRouter: FinagleRouter): FinagleService =
      Filters.AccessLogging andThen Filters.HeaderAdding andThen finagleRouter andThen Filters.TmtNotFound
  }

  val design =
    newFinagleServerDesign(port = 8080, name = "tmt-http", router = router) +
      ActualPersistenceContextDesign.design +
      product.design + historicaldata.design
      .bind[CurrentTimeProvider].toInstance(CurrentTimeProvider.system())
      .bind[Calendar].to[HolidayRepository]
      .bind[FinagleServerFactory].to[CustomFinagleServerFactory]
      .bind[ListProductForDeliveryUseCase[TwFutureTatriaContext]].toSingleton

  design.build[FinagleServer] { server =>
    server.waitServerTermination
  }
}
