package yokohama.murataku.trade.holiday

import java.time.{DayOfWeek, LocalDate, Month}

import yokohama.murataku.testutil.MyTestSuite
import yokohama.murataku.trade.lib.date.YearMonth

class YearMonthTest extends MyTestSuite {
  "allDays returns all days for 2019/7" in {
    val allDays = YearMonth(2019, Month.JULY).allDays
    assert(allDays.size == 31)
    assert(allDays.head == LocalDate.of(2019, 7, 1))
    assert(allDays.last == LocalDate.of(2019, 7, 31))
  }

  "can find 2nd Friday of a month" in {
    YearMonth(2019, 9).find(2, DayOfWeek.FRIDAY) shouldBe LocalDate.of(2019,
                                                                       9,
                                                                       13)
  }

  "format toString" in {
    YearMonth(1989, 4).toString shouldBe "1989/04"
  }

  "decode from raw string with slash" in {
    YearMonth.decode("1989/04") shouldBe YearMonth(1989, 4)
  }

  "decode from raw string without slash" in {
    assertThrows[IllegalArgumentException] {
      YearMonth.decode("198904") shouldBe YearMonth(1989, 4)
    }
  }

  "fromSixNum from raw string without slash" in {
    YearMonth.fromSixNum("198904") shouldBe YearMonth(1989, 4)
  }
}
