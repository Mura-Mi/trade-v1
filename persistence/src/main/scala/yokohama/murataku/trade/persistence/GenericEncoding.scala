package yokohama.murataku.trade.persistence

import enumeratum.values.{StringEnum, StringEnumEntry}
import io.getquill.MappedEncoding
import shapeless._

trait GenericEncoding {
  implicit def stringDecoding[T, C](
      implicit generic: Generic.Aux[T, C :: HNil]): MappedEncoding[C, T] = {
    MappedEncoding(i => generic.from(i :: HNil))
  }

  implicit def stringEncoding[T, C](
      implicit generic: Generic.Aux[T, C :: HNil]): MappedEncoding[T, C] =
    MappedEncoding(v => generic.to(v).head)

  implicit def stringEnumDecoding[T <: StringEnumEntry,
                                  C <: StringEnum[T],
                                  String]: MappedEncoding[T, java.lang.String] =
    MappedEncoding[T, java.lang.String](i => i.value)

  implicit def stringEnumDecoding[T <: StringEnumEntry,
                                  C <: StringEnum[T],
                                  String](
      implicit c: StringEnum[T]): MappedEncoding[java.lang.String, T] =
    MappedEncoding[java.lang.String, T](i => c.withValue(i))
}
