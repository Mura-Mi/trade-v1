package yokohama.murataku.trade.persistence

import java.util.UUID

import io.getquill.MappedEncoding

trait DomainEncoding {

  implicit val uuidEncoding: MappedEncoding[String, UUID] = MappedEncoding(
    UUID.fromString)
  implicit val uuidDecoding: MappedEncoding[UUID, String] = MappedEncoding(
    _.toString)

}
