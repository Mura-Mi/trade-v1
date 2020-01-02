package yokohama.murataku.trade.product

import io.circe.Encoder
import shapeless.Generic

case class IndexName(value: String)

//noinspection TypeAnnotation
object IndexName {
  implicit val gen = Generic[IndexName]
  implicit val enc: Encoder[IndexName] = Encoder.encodeString.contramap(_.value)

  /** 日経平均 */
  final val nk225: IndexName = IndexName("NK225")
}
