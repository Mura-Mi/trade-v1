package yokohama.murataku.trade.product

import enumeratum.values.{StringEnum, StringEnumEntry}
import yokohama.murataku.trade.lib.enum.EnumUtils

sealed abstract class ProductType(override val value: String)
    extends StringEnumEntry {}

object ProductType extends StringEnum[ProductType] {
  case object IndexFuture extends ProductType("IF")
  case object IndexOption extends ProductType("IO")

  //noinspection TypeAnnotation
  override def values = findValues

  implicit val enumUtil: EnumUtils[String, ProductType] = new EnumUtils(this)
}
