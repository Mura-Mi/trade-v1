package yokohama.murataku.trade.holiday

import java.time.{DayOfWeek, LocalDate, Month}

import wvlet.airspec._

class YearMonthTest extends AirSpec {
  def `allDays returns all days for 2019/7`(): Unit = {
    val allDays = YearMonth(2019, Month.JULY).allDays
    assert(allDays.size == 31)
    assert(allDays.head == LocalDate.of(2019, 7, 1))
    assert(allDays.last == LocalDate.of(2019, 7, 31))
  }

  def `can find 2nd Friday of a month`(): Unit = {
    YearMonth(2019, 9).find(2, DayOfWeek.FRIDAY) shouldBe LocalDate.of(2019,
                                                                       9,
                                                                       13)
  }
}
