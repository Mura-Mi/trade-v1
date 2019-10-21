package yokohama.murataku.trade.lib

import java.time.{Clock, LocalDateTime, ZoneId, ZonedDateTime}

import yokohama.murataku.trade.lib.CurrentTimeProvider.zone

trait CurrentTimeProvider {
  protected def baseClock: Clock

  private def clock: Clock = baseClock.withZone(CurrentTimeProvider.zone)

  def now(): ZonedDateTime = ZonedDateTime.now(clock)
}

class MutableCurrentTimeProvider(var time: LocalDateTime)
    extends CurrentTimeProvider {
  override protected def baseClock: Clock =
    Clock.fixed(ZonedDateTime.of(time, zone).toInstant, zone)
}

object CurrentTimeProvider {
  def zone: ZoneId = ZoneId.of("Asia/Tokyo")

  def fixed(localDateTime: LocalDateTime): CurrentTimeProvider =
    new CurrentTimeProvider {
      override protected def baseClock: Clock =
        Clock.fixed(ZonedDateTime.of(localDateTime, zone).toInstant, zone)
    }

  def mutable(initial: LocalDateTime): MutableCurrentTimeProvider =
    new MutableCurrentTimeProvider(initial)

  def system(): CurrentTimeProvider = new CurrentTimeProvider {
    override protected def baseClock: Clock = Clock.system(zone)
  }
}
