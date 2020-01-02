package yokohama.murataku.trade.product.indexoption

import io.circe.Encoder
import shapeless.Generic

case class Strike(value: BigDecimal)

object Strike {
  //noinspection TypeAnnotation
  implicit val gen = Generic[Strike]
  implicit val enc: Encoder[Strike] = Encoder.encodeBigDecimal.contramap(_.value)
}
