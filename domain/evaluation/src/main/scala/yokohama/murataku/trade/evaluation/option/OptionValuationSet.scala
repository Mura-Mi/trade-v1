package yokohama.murataku.trade.evaluation.option

import java.time.LocalDate
import java.util.UUID

import enumeratum.values.{StringEnum, StringEnumEntry}

import scala.collection.immutable

case class OptionValuationSet(
    subjectId: UUID,
    valuationBaseDate: LocalDate,
    price: BigDecimal,
    greeks: Seq[OptionGreeks]
)

case class OptionGreeks(
    formula: GreeksFormula,
    value: BigDecimal
)

sealed trait GreeksFormula

object GreeksFormula {

  case class Delta(sigma: BigDecimal, way: DeltaWay) extends GreeksFormula

  case class Theta(day: Int, unit: ThetaUnit) extends GreeksFormula

  case class Vega(diff: BigDecimal, way: DeltaWay) extends GreeksFormula

}

sealed abstract class ThetaUnit(override val value: String)
    extends StringEnumEntry

object ThetaUnit extends StringEnum[ThetaUnit] {

  case object CalendarDay extends ThetaUnit("c")

  case object BusinessDay extends ThetaUnit("b")

  override def values: immutable.IndexedSeq[ThetaUnit] = findValues
}

sealed abstract class DeltaWay(override val value: String)
    extends StringEnumEntry

object DeltaWay {

  case object Forward extends DeltaWay("fw")

  case object Backward extends DeltaWay("bw")

  case object BothSide extends DeltaWay("bt")

}
