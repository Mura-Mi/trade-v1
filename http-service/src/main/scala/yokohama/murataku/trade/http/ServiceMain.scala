package yokohama.murataku.trade.http

import wvlet.airframe.http.Router
import wvlet.airframe.http.finagle._

object ServiceMain extends StandardHttpService {
  val router = Router
    .add[HealthCheckRouting]
    .add[AnalysisRouting]

  val design =
    newFinagleServerDesign(port = 8080, name = "tmt-http", router = router)

  design.build[FinagleServer] { server =>
    server.waitServerTermination
  }
}
