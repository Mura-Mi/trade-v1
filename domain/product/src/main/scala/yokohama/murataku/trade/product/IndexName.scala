package yokohama.murataku.trade.product

import shapeless.Generic

case class IndexName(value: String)

//noinspection TypeAnnotation
object IndexName {
  implicit val gen = Generic[IndexName]

  /** 日経平均 */
  final val nk225: IndexName = IndexName("NK225")
}
