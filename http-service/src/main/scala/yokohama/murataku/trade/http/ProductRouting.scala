package yokohama.murataku.trade.http

import com.twitter.util.Future
import wvlet.airframe.http.Endpoint
import yokohama.murataku.trade.product.ListProductForDeliveryUseCase
import wvlet.airframe._
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.TwFutureTatriaContext

@Endpoint(path = "/products")
trait ProductRouting extends TatriaCodecFactory {
  private val listProductForDeliveryUseCase = bind[ListProductForDeliveryUseCase[TwFutureTatriaContext]]
  private implicit val ctx = bind[TwFutureTatriaContext]

  @Endpoint(path = "/:delivery")
  def listForDelivery(delivery: String): Future[String] = {
    val ym = YearMonth.fromSixNum(delivery)
    listProductForDeliveryUseCase.run(ym).underlying.toJsonResponse
  }
}
