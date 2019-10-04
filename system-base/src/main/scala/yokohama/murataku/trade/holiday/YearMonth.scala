package yokohama.murataku.trade.holiday

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
        s"Unsupported: ${nth}th $dow in $this"))

  override def toString: String =
    year.toString + '/' + "%02d".format(month.getValue)
}

object YearMonth {
  def apply(year: Int, month: Int): YearMonth = YearMonth(year, Month.of(month))

  @throws[IllegalArgumentException]
  def decode(s: String): YearMonth = {
    Try {
      val arr = s.split('/')
      val year = arr.head.toInt
      val month = arr(1).toInt
      YearMonth(year, month)
    } match {
      case Success(ym)                          => ym
      case Failure(e: IllegalArgumentException) => throw e
      case Failure(other)                       => throw new IllegalArgumentException(other)
    }
  }
}
