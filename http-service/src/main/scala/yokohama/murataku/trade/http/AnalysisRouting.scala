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
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.indexoption.{
  IndexOptionRepository,
  PutOrCall
}
import yokohama.murataku.trade.product.{IndexOptionRepositoryImpl, ProductType}
import yokohama.murataku.trade.volatility.{
  CalculateHistoricalVolatilityUseCase,
  CalculateOptionGreeksUseCase,
  DailyVolatility
}

@Endpoint(path = "")
trait AnalysisRouting {
  private val historicalVolUseCase = bind[CalculateHistoricalVolatilityUseCase]
  private val greeksUseCase = bind[CalculateOptionGreeksUseCase]
  private val productRepository =
    bind[IndexOptionRepository[TwFutureTatriaContext]]
  private implicit val tatriaContext = bind[TwFutureTatriaContext]

  @Endpoint(path = "/vol")
  def vol: Future[String] = {
    historicalVolUseCase
      .extract(ProductType.IndexFuture,
               "NK225",
               LocalDate.now().minusYears(3),
               LocalDate.now)
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
      vs <- historicalVolUseCase.extract(ProductType.IndexFuture,
                                         "NK225",
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
    for {
      n <- productRepository
        .findBy(BigDecimal(strike),
                PutOrCall.of(poc),
                YearMonth.fromSixNum(delivery)).map(_.productName).underlying
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
