package yokohama.murataku.trade.http

import wvlet.airframe.http.Router
import wvlet.airframe.http.finagle._
import yokohama.murataku.trade.persistence.finagle.ActualPersistenceContextDesign

object ServiceMain extends StandardHttpService {
  val router = Router
    .add[HealthCheckRouting]
    .add[AnalysisRouting]
    .add[StaticFileRouting]

  val design =
    newFinagleServerDesign(port = 8080, name = "tmt-http", router = router) +
      ActualPersistenceContextDesign.design

  design.build[FinagleServer] { server =>
    server.waitServerTermination
  }
}
