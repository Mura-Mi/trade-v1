package yokohama.murataku.trade.http

import io.finch._
import io.finch.syntax._
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.ListProductForDeliveryUseCase
import yokohama.murataku.trade.product.indexoption.IndexOption

class ProductRouting(
    private val listProductForDeliveryUseCase: ListProductForDeliveryUseCase[TwFutureTatriaContext],
    private implicit val ctx: TwFutureTatriaContext
) {

  val ep: Endpoint[List[IndexOption]] = get("products" :: path[String]) { delivery: String =>
    val ym = YearMonth.fromSixNum(delivery)
    listProductForDeliveryUseCase.run(ym).underlying.map(_.toList).map(Ok)
  }
}
