package yokohama.murataku.trade.holiday

import java.time.LocalDate

import yokohama.murataku.testutil.MyTestSuite
import yokohama.murataku.trade.lib.date.CurrentTimeProvider

//noinspection AccessorLikeMethodIsUnit
class CalendarTest extends MyTestSuite {
  private val sut = new Calendar {
    override def holidays: Seq[Holiday] = Seq(
      Holiday(LocalDate.of(2019, 9, 16), Market.Jpx, "敬老の日"),
      Holiday(LocalDate.of(2019, 9, 23), Market.Jpx, "秋分の日")
    )

    override protected val currentTimeProvider: CurrentTimeProvider =
      CurrentTimeProvider.system()
  }

  "isBusinessDay can determine business day" in {
    sut.isBusinessDay(LocalDate.of(2019, 9, 17)) shouldBe true
  }

  "isBusinessDay can determine weekend" in {
    sut.isBusinessDay(LocalDate.of(2019, 9, 8)) shouldBe false
  }

  "isBusinessDay can determine holiday" in {
    sut.isBusinessDay(LocalDate.of(2019, 9, 16)) shouldBe false
  }

  ".nextBusinessDay works" in {
    import sut._
    LocalDate.of(2019, 9, 13).nextBusinessDay() shouldBe LocalDate.of(2019,
                                                                      9,
                                                                      17)
  }
}
