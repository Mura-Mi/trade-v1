package yokohama.murataku.trade.http

import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.util.{Future, Stopwatch}
import wvlet.log.LogSupport

import scala.util.control.NonFatal

object Filters {
  object TmtNotFound extends Service[Request, Response] with LogSupport {
    override def apply(request: Request): Future[Response] = {
      info(s"NOT FOUND ${request.method} ${request.uri}")
      Future.value(Response(Status.NotFound))
    }
  }

  object AccessLogging extends SimpleFilter[Request, Response] with LogSupport {
    override def apply(
        request: Request,
        service: Service[Request, Response]): Future[Response] = {
      val start = Stopwatch.systemMillis()
      service
        .apply(request).rescue {
          case NonFatal(e) =>
            error(e)
            Future.exception(e)
        }.onSuccess(_ => {
          val stop = Stopwatch.systemMillis()
          info(
            s"${request.method} ${request.uri} ${BigDecimal(stop - start) / BigDecimal(1000)}(sec)")
        })
    }
  }

  object HeaderAdding extends SimpleFilter[Request, Response] {
    override def apply(request: Request,
                       service: Service[Request, Response]): Future[Response] =
      service.apply(request).map { resp =>
        resp.headerMap.add("Access-Control-Allow-Origin", "*")
        resp.headerMap.add("Access-Control-Allow-Headers", "content-type")

        resp
      }
  }
}
