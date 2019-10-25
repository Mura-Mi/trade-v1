package yokohama.murataku.trade.product

import shapeless.Generic

case class IndexName(value: String)

//noinspection TypeAnnotation
object IndexName {
  implicit val gen = Generic[IndexName]
}