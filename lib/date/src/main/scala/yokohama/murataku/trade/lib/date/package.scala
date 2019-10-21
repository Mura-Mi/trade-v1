package yokohama.murataku.trade.lib

import java.time.LocalDate

package object date {
  implicit class LocalDateExt(underlying: LocalDate) {
    def <(other: LocalDate): Boolean = underlying.isBefore(other)
    def >(other: LocalDate): Boolean = underlying.isAfter(other)
    def <=(other: LocalDate): Boolean =
      underlying < other || underlying.isEqual(other)
    def >=(other: LocalDate): Boolean =
      underlying > other || underlying.isEqual(other)

    def isBetween(fromInclusive: LocalDate, toInclusive: LocalDate): Boolean =
      fromInclusive <= underlying && underlying <= toInclusive
  }
}
