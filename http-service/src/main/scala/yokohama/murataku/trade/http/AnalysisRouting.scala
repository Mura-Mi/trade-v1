package yokohama.murataku.trade.http

import java.time.LocalDate
import java.util.UUID

import com.twitter.util.Future
import wvlet.airframe._
import wvlet.airframe.codec.{MessageCodec, MessageCodecFactory, MessageValueCodec}
import wvlet.airframe.http.Endpoint
import wvlet.airframe.json.JSON.JSONArray
import wvlet.airframe.msgpack.spi.Value
import wvlet.airframe.msgpack.spi.Value.StringValue
import wvlet.airframe.surface.Surface
import yokohama.murataku.trade.evaluation.option.OptionValuationSet
import yokohama.murataku.trade.http.pages.ShowHistoricalVolPage
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.product.IndexName
import yokohama.murataku.trade.product.indexoption.{IndexOptionName, PutOrCall}
import yokohama.murataku.trade.volatility.{CalculateHistoricalVolatilityUseCase, CalculateOptionGreeksUseCase, DailyVolatility}

@Endpoint(path = "")
trait AnalysisRouting {
  private val historicalVolUseCase = bind[CalculateHistoricalVolatilityUseCase]
  private val greeksUseCase = bind[CalculateOptionGreeksUseCase]

  @Endpoint(path = "/vol")
  def vol: Future[String] = {
    historicalVolUseCase
      .extract("NK225", LocalDate.now().minusYears(3), LocalDate.now)
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
      vs <- historicalVolUseCase.extract("NK225",
                                         LocalDate.now().minusYears(3),
                                         LocalDate.now)
    } yield {
      JSONArray(vs.map(codec.toJSONObject).toIndexedSeq).toJSON
    }

  }

  @Endpoint(path = "/greeks/:delivery/:strike/:poc/:date")
  def greeksJson(delivery: String,
                 strike: String,
                 poc: String,
                 date: String): Future[String] = {

    val n = IndexOptionName.apply(IndexName("NK225E"),
                                  PutOrCall.of(poc),
                                  YearMonth.decode(delivery),
                                  BigDecimal(strike))
    for {
      gs <- greeksUseCase.run(n, LocalDate.parse(date))
    } yield {
      import io.circe.generic.auto._
      import io.circe.syntax._

      Greeks(
        Some(gs.price),
        gs.greeks.delta.map(_.value),
        gs.greeks.vega.map(_.value),
        gs.greeks.theta.map(_.value)
      ).asJson.noSpaces
    }
  }

  case class Greeks(
      marketPrice: Option[BigDecimal],
      delta: Option[BigDecimal],
      vega: Option[BigDecimal],
      theta: Option[BigDecimal],
  )
}
