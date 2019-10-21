package yokohama.murataku.trade.http

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.airframe._
import wvlet.airframe.codec.{MessageCodecFactory, MessageValueCodec}
import wvlet.airframe.http.Endpoint
import wvlet.airframe.json.JSON.JSONArray
import wvlet.airframe.msgpack.spi.Value
import wvlet.airframe.msgpack.spi.Value.StringValue
import wvlet.airframe.surface.Surface
import yokohama.murataku.trade.http.pages.ShowHistoricalVolPage
import yokohama.murataku.trade.volatility.{
  CalculateHistoricalVolatilityUseCase,
  DailyVolatility
}

@Endpoint(path = "")
trait AnalysisRouting {
  private val uc = bind[CalculateHistoricalVolatilityUseCase]

  @Endpoint(path = "/vol")
  def vol: Future[String] = {
    uc.extract("NK225", LocalDate.now().minusYears(3), LocalDate.now)
      .map(record => new ShowHistoricalVolPage(record).toHtml())
  }

  @Endpoint(path = "/vol.json")
  def volJson: Future[String] = {
    val f = MessageCodecFactory.defaultFactory.withCodecs(
      Map(Surface.of[LocalDate] -> new MessageValueCodec[LocalDate] {
        override def packValue(v: LocalDate): Value = StringValue(v.toString)

        override def unpackValue(v: Value): LocalDate =
          LocalDate.parse(v.toString())
      }))

    val codec = f.of[DailyVolatility]
    for {
      vs <- uc.extract("NK225", LocalDate.now().minusYears(3), LocalDate.now)
    } yield {
      JSONArray(vs.map(codec.toJSONObject).toIndexedSeq).toJSON
    }

  }
}
