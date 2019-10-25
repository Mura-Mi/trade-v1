package yokohama.murataku.trade.lib.date

import java.time.{DayOfWeek, LocalDate, Month}

import scala.util.{Failure, Success, Try}

case class YearMonth(year: Int, month: Month) {
  def firstDay: LocalDate = LocalDate.of(year, month, 1)

  def allDays: Seq[LocalDate] =
    Stream
      .iterate(firstDay)(_.plusDays(1))
      .takeWhile(d => d.getYear == this.year && d.getMonth == month)

  def find(nth: Int, dow: DayOfWeek): LocalDate =
    allDays
      .filter(_.getDayOfWeek == dow)
      .drop(nth - 1)
      .headOption
      .getOrElse(throw new IllegalArgumentException(
        s"Unsupported: ${nth}th $dow in $this "))

  override def toString: String =
    year.toString + '/' + "%02d".format(month.getValue)

  def toStringWithoutSlash: String =
    year.toString + "%02d".format(month.getValue)
}

object YearMonth {
  def apply(year: Int, month: Int): YearMonth = YearMonth(year, Month.of(month))

  def of(year: Int, month: Int): YearMonth = apply(year, month)

  @throws[IllegalArgumentException]
  def decode(s: String): YearMonth = {
    Try {
      val shouldBeSlash = s.charAt(4)
      if (shouldBeSlash != '/' || s.length != 7)
        throw new IllegalArgumentException(s"$s must be yyyy/mm")
      val year = s.substring(0, 4).toInt
      val month = s.substring(5, 7).toInt

      YearMonth(year, month)
    } match {
      case Success(ym)                              => ym
      case Failure(other: IllegalArgumentException) => throw other
      case Failure(other) =>
        throw new IllegalArgumentException(s"$s is not able to be parsed",
                                           other)
    }
  }

  @throws[IllegalArgumentException]
  def fromSixNum(s: String): YearMonth =
    Try {
      val year = s.substring(0, 4).toInt
      val month = Month.of(s.substring(4, 6).toInt)
      return YearMonth(year, month)
    } match {
      case Success(ym) => ym
      case Failure(e) =>
        throw new IllegalArgumentException(s"$s is not able to be parsed", e)
    }
}
