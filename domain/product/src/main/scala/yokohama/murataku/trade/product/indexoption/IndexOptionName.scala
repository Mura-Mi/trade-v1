package yokohama.murataku.trade.product.indexoption

case class IndexOptionName(value: String)

object IndexOptionName {
  //noinspection TypeAnnotation
  implicit val gen = shapeless.Generic[IndexOptionName]
}
