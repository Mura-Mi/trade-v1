package yokohama.murataku.trade.persistence

import enumeratum.values.{StringEnum, StringEnumEntry, ValueEnumEntry}
import io.getquill.MappedEncoding
import shapeless._
import yokohama.murataku.trade.lib.enum.EnumUtils

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

  implicit def enumEncoding[V, E <: ValueEnumEntry[V]](
      implicit u: EnumUtils[V, E]): MappedEncoding[V, E] =
    MappedEncoding(v => u.toEnum(v))
  implicit def enumDecoding[E <: ValueEnumEntry[V], V](
      implicit u: EnumUtils[V, E]): MappedEncoding[E, V] =
    MappedEncoding(v => u.toRaw(v))
}
