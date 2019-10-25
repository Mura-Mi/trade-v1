package yokohama.murataku.trade.product.indexoption

import enumeratum.values.{StringEnum, StringEnumEntry}
import yokohama.murataku.trade.lib.enum.EnumUtils
import yokohama.murataku.trade.product.indexoption.PutOrCall.{Call, Put}

import scala.collection.immutable

sealed abstract class PutOrCall(val value: String) extends StringEnumEntry {
  def isCall: Boolean

  def factor: Int = this match {
    case Call => 1
    case Put => -1
  }
}

object PutOrCall extends StringEnum[PutOrCall] {
  def of(poc: String): PutOrCall = withValueOpt(poc).getOrElse(
    if (poc.equalsIgnoreCase("Put")) Put
    else if (poc.equalsIgnoreCase("Call")) Call
    else throw new IllegalArgumentException(s"$poc is not found")
  )

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
