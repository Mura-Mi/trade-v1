package yokohama.murataku.trade.lib.enum

import enumeratum.values.{ValueEnum, ValueEnumEntry}

class EnumUtils[V, E <: ValueEnumEntry[V]](self: ValueEnum[V, E]) {
  def toRaw(e: E): V = e.value
  def toEnum(v: V): E = self.withValue(v)
}
