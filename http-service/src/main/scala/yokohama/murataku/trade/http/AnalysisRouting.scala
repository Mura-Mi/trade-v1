package yokohama.murataku.trade.http

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.airframe._
import wvlet.airframe.http.Endpoint
import yokohama.murataku.trade.evaluation.option.OptionPayoff
import yokohama.murataku.trade.holiday.Calendar
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.ProductType
import yokohama.murataku.trade.product.indexoption.{
  IndexOptionRepository,
  PutOrCall
}
import yokohama.murataku.trade.volatility.{
  CalculateHistoricalVolatilityUseCase,
  CalculateOptionGreeksUseCase
}

@Endpoint(path = "")
trait AnalysisRouting extends TatriaCodecFactory {
  private val historicalVolUseCase = bind[CalculateHistoricalVolatilityUseCase]
  private val greeksUseCase = bind[CalculateOptionGreeksUseCase]
  private val productRepository =
    bind[IndexOptionRepository[TwFutureTatriaContext]]
  private implicit val tatriaContext: TwFutureTatriaContext =
    bind[TwFutureTatriaContext]
  private val calendar: Calendar = bind[Calendar]

  @Endpoint(path = "/vol")
  def vol(from: Option[String], to: Option[String]): Future[String] = {
    val fromDate =
      from.map(LocalDate.parse).getOrElse(LocalDate.now().minusYears(3))
    val toDate = to.map(LocalDate.parse).getOrElse(LocalDate.now())
    historicalVolUseCase
      .extract(ProductType.IndexFuture, "NK225", fromDate, toDate).toJsonResponse
  }

  @Endpoint(path = "/greeks/:delivery/:strike/:poc")
  def greeksJson(delivery: String,
                 strike: String,
                 poc: String,
                 date: Option[String]): Future[String] = {
    val valuationDate =
      date.map(LocalDate.parse).getOrElse(calendar.latestBusinessDay)
    (
      for {
        n <- productRepository
          .findBy(BigDecimal(strike),
                  PutOrCall.of(poc),
                  YearMonth.fromSixNum(delivery)).map(_.productName).underlying
        gs <- greeksUseCase.run(n, valuationDate)
      } yield {
        OptionValuation(Greeks(
                          Some(gs.price),
                          gs.greeks.delta.map(_.value),
                          gs.greeks.vega.map(_.value),
                          gs.greeks.theta.map(_.value)
                        ),
                        gs.payoff)
      }
    ).toJsonResponse
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
