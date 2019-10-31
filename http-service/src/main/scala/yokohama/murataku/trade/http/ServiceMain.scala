package yokohama.murataku.trade.http

import wvlet.airframe.http.Router
import wvlet.airframe.http.finagle.FinagleServer.FinagleService
import wvlet.airframe.http.finagle._
import wvlet.log.Logger
import yokohama.murataku.trade.persistence.finagle.ActualPersistenceContextDesign
import yokohama.murataku.trade.{historicaldata, product}

object ServiceMain extends StandardHttpService {
  Logger.scheduleLogLevelScan
  val router = Router
    .add[HealthCheckRouting]
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
      .bind[FinagleServerFactory].to[CustomFinagleServerFactory]

  design.build[FinagleServer] { server =>
    server.waitServerTermination
  }
}
