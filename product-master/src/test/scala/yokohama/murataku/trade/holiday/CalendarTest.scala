package yokohama.murataku.trade.holiday

import java.time.LocalDate

import wvlet.airspec.AirSpec

//noinspection AccessorLikeMethodIsUnit
class CalendarTest extends AirSpec {
  private val sut = new Calendar {
    override def holidays: Seq[Holiday] = Seq(
      Holiday(LocalDate.of(2019, 9, 16), Market.Jpx, "敬老の日"),
      Holiday(LocalDate.of(2019, 9, 23), Market.Jpx, "秋分の日")
    )
  }

  def `isBusinessDay can determine business day`(): Unit = {
    sut.isBusinessDay(LocalDate.of(2019, 9, 17)) shouldBe true
  }

  def `isBusinessDay can determine weekend`(): Unit = {
    sut.isBusinessDay(LocalDate.of(2019, 9, 8)) shouldBe false
  }

  def `isBusinessDay can determine holiday`(): Unit = {
    sut.isBusinessDay(LocalDate.of(2019, 9, 16)) shouldBe false
  }

  def `.nextBusinessDay works`(): Unit = {
    import sut._
    LocalDate.of(2019, 9, 13).nextBusinessDay() shouldBe LocalDate.of(2019,
                                                                      9,
                                                                      17)
  }
}
