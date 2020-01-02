package yokohama.murataku.trade.http.products

import cats.data.NonEmptyList
import cats.kernel.Order
import yokohama.murataku.trade.product.indexoption.IndexOption

case class ProductListView(
    strikes: NonEmptyList[BigDecimal],
    callProducts: Seq[IndexOption],
    putProducts: Seq[IndexOption]
)

object ProductListView {
  def apply(options: NonEmptyList[IndexOption]): ProductListView = {
    implicit val ord: cats.Order[BigDecimal] = Order.fromComparable
    val strikes = options.map(_.strike).distinct.sorted
    val callProducts = options.filter(_.putOrCall.isCall).sortBy(_.strike)
    val putProducts = options.filter(_.putOrCall.isPut).sortBy(_.strike)

    ProductListView(strikes, callProducts, putProducts)
  }
}
