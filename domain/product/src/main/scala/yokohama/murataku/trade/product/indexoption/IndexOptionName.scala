package yokohama.murataku.trade.product.indexoption

import io.circe.Encoder

case class IndexOptionName(value: String)

object IndexOptionName {
  //noinspection TypeAnnotation
  implicit val gen = shapeless.Generic[IndexOptionName]
  implicit val enc: Encoder[IndexOptionName] = Encoder.encodeString.contramap(_.value)
}
