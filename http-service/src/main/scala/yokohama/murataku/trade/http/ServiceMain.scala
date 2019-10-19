package yokohama.murataku.trade.http

import com.twitter.finagle.{Filter, Service, SimpleFilter}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Future
import wvlet.airframe.http.Router
import wvlet.airframe.http.finagle.FinagleServer.FinagleService
import wvlet.airframe.http.finagle._
import wvlet.log.LogSupport
import yokohama.murataku.trade.persistence.finagle.ActualPersistenceContextDesign

object ServiceMain extends StandardHttpService {
  val router = Router
    .add[HealthCheckRouting]
    .add[AnalysisRouting]
    .add[StaticFileRouting]

  trait CustomFinagleServerFactory extends FinagleServerFactory {
    override def newService(finagleRouter: FinagleRouter): FinagleService =
      new SimpleFilter[Request, Response] with LogSupport {
        override def apply(
            request: Request,
            service: Service[Request, Response]): Future[Response] = {
          info(s"${request.method} ${request.uri}")
          service.apply(request)
        }

      } andThen finagleRouter andThen FinagleServer.notFound
  }

  val design =
    newFinagleServerDesign(port = 8080, name = "tmt-http", router = router) +
      ActualPersistenceContextDesign.design
        .bind[FinagleServerFactory].to[CustomFinagleServerFactory]

  design.build[FinagleServer] { server =>
    server.waitServerTermination
  }
}
