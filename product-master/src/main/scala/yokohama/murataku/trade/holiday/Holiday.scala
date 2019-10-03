package yokohama.murataku.trade.holiday

import java.time.LocalDate

import enumeratum.values.{StringEnum, StringEnumEntry}

import scala.collection.immutable

case class Holiday(date: LocalDate, market: Market, note: String)

sealed abstract class Market(val value: String) extends StringEnumEntry

object Market extends StringEnum[Market] {
  case object Jpx extends Market("jpx")
  override def values: immutable.IndexedSeq[Market] = findValues
}
