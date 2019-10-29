package yokohama.murataku.trade.http

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.airframe._
import wvlet.airframe.http.Endpoint
import wvlet.airframe.json.JSON.JSONArray
import yokohama.murataku.trade.evaluation.option.OptionPayoff
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.ProductType
import yokohama.murataku.trade.product.indexoption.{
  IndexOptionRepository,
  PutOrCall
}
import yokohama.murataku.trade.volatility.{
  CalculateHistoricalVolatilityUseCase,
  CalculateOptionGreeksUseCase,
  DailyVolatility
}

@Endpoint(path = "")
trait AnalysisRouting extends TatriaCodeFactory {
  private val historicalVolUseCase = bind[CalculateHistoricalVolatilityUseCase]
  private val greeksUseCase = bind[CalculateOptionGreeksUseCase]
  private val productRepository =
    bind[IndexOptionRepository[TwFutureTatriaContext]]
  private implicit val tatriaContext: TwFutureTatriaContext =
    bind[TwFutureTatriaContext]

  @Endpoint(path = "/vol.json")
  def volJson: Future[JSONArray] = {
    val codec = codecOf[DailyVolatility]
    for {
      vs <- historicalVolUseCase.extract(ProductType.IndexFuture,
                                         "NK225",
                                         LocalDate.now().minusYears(3),
                                         LocalDate.now)
    } yield {
      JSONArray(vs.map(codec.toJSONObject).toIndexedSeq)
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
      codecOf[OptionValuation].toJson(
        OptionValuation(Greeks(
                          Some(gs.price),
                          gs.greeks.delta.map(_.value),
                          gs.greeks.vega.map(_.value),
                          gs.greeks.theta.map(_.value)
                        ),
                        gs.payoff))
    }
  }

  case class OptionValuation(
      greeks: Greeks,
      payoff: Seq[OptionPayoff]
  )

  case class Greeks(
      marketPrice: Option[BigDecimal],
      delta: Option[BigDecimal],
      vega: Option[BigDecimal],
      theta: Option[BigDecimal],
  )

}
