package yokohama.murataku.trade.holiday

import java.time.{DayOfWeek, LocalDate}

import yokohama.murataku.trade.holiday.HolidayAdjustMethod.{
  Following,
  Preceding
}

trait Calendar {
  private val weekends = Seq(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

  def holidays: Seq[Holiday]

  def isBusinessDay(date: LocalDate): Boolean = !isHoliday(date)

  def isHoliday(date: LocalDate): Boolean =
    weekends.contains(date.getDayOfWeek) || holidays.exists(_.date == date)

  implicit class LocalDateExtension(underlying: LocalDate) {
    private def seekBusinessDay(way: HolidayAdjustMethod,
                                dropFirst: Int): LocalDate =
      Stream
        .iterate(underlying)(_.plusDays(way.factor))
        .drop(dropFirst)
        .filter(isBusinessDay)
        .head

    def adjust(way: HolidayAdjustMethod): LocalDate = seekBusinessDay(way, 0)

    def previousBusinessDay(): LocalDate = seekBusinessDay(Preceding, 1)

    def nextBusinessDay(): LocalDate = seekBusinessDay(Following, 1)
  }
}

sealed trait HolidayAdjustMethod {
  val factor: Short = this match {
    case Preceding => -1
    case Following => +1
  }
}

object HolidayAdjustMethod {

  case object Preceding extends HolidayAdjustMethod
  case object Following extends HolidayAdjustMethod
}
