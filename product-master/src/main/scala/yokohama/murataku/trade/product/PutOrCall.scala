package yokohama.murataku.trade.product

import enumeratum.values.{
  StringEnum,
  StringEnumEntry,
  ValueEnum,
  ValueEnumEntry
}
import yokohama.murataku.trade.holiday.Market
import yokohama.murataku.trade.persistence.EnumUtils

import scala.collection.immutable
import scala.reflect.ClassTag

sealed abstract class PutOrCall(val value: String) extends StringEnumEntry {
  def isCall: Boolean
}

object PutOrCall extends StringEnum[PutOrCall] {
  case object Put extends PutOrCall("P") {
    override def isCall: Boolean = false
  }
  case object Call extends PutOrCall("C") {
    override def isCall: Boolean = true
  }

  override def values: immutable.IndexedSeq[PutOrCall] = findValues

  val both: Seq[PutOrCall] = Seq(Put, Call)

  implicit val enumUtils: EnumUtils[String, PutOrCall] = new EnumUtils(this)
}
