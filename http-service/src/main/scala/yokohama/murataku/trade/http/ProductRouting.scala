package yokohama.murataku.trade.http

import io.finch._
import io.finch.syntax._
import yokohama.murataku.trade.http.finch.ValueObjectPath
import yokohama.murataku.trade.http.products.ProductListView
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.ListProductForDeliveryUseCase

class ProductRouting(
    private val listProductForDeliveryUseCase: ListProductForDeliveryUseCase[TwFutureTatriaContext],
    private implicit val ctx: TwFutureTatriaContext
) extends ValueObjectPath {

  val ep: Endpoint[ProductListView] = get("products" :: yearMonthPath) { delivery: YearMonth =>
    listProductForDeliveryUseCase.run(delivery).underlying.map(ProductListView(_)).map(Ok)
  }
}
