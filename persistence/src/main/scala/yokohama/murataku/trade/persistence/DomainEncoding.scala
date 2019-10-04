package yokohama.murataku.trade.persistence

import java.util.UUID

import io.getquill.MappedEncoding
import yokohama.murataku.trade.holiday.YearMonth

trait DomainEncoding {

  implicit val uuidEncoding: MappedEncoding[String, UUID] = MappedEncoding(
    UUID.fromString)
  implicit val uuidDecoding: MappedEncoding[UUID, String] = MappedEncoding(
    _.toString)

  implicit val yearMonthEncoding: MappedEncoding[String, YearMonth] =
    MappedEncoding(YearMonth.decode)
  implicit val yearMonthDecoding: MappedEncoding[YearMonth, String] =
    MappedEncoding(_.toString)

}
