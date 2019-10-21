package yokohama.murataku.trade.product

import io.getquill.MappedEncoding
import yokohama.murataku.trade.holiday.YearMonth

trait Encodings {

  implicit val yearMonthEncoding: MappedEncoding[String, YearMonth] =
    MappedEncoding(YearMonth.decode)
  implicit val yearMonthDecoding: MappedEncoding[YearMonth, String] =
    MappedEncoding(_.toString)

}
