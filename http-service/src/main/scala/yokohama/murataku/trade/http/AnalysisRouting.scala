package yokohama.murataku.trade.http

import java.time.LocalDate

import com.twitter.util.Try
import io.finch.syntax._
import io.finch.{paramOption, _}
import yokohama.murataku.trade.evaluation.option.OptionPayoff
import yokohama.murataku.trade.holiday.Calendar
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.ProductType
import yokohama.murataku.trade.product.indexoption.{IndexOptionRepository, PutOrCall}
import yokohama.murataku.trade.volatility.{CalculateHistoricalVolatilityUseCase, CalculateOptionGreeksUseCase}

class AnalysisRouting(
    private val historicalVolUseCase: CalculateHistoricalVolatilityUseCase,
    private val greeksUseCase: CalculateOptionGreeksUseCase,
    private val productRepository: IndexOptionRepository[TwFutureTatriaContext],
    private implicit val tatriaContext: TwFutureTatriaContext,
    private val calendar: Calendar
) {

  val vol = get("vol" :: paramOption[String]("from") :: paramOption[String]("to")) {
    (from: Option[String], to: Option[String]) =>
      val fromDate =
        from.map(LocalDate.parse).getOrElse(LocalDate.now().minusYears(3))
      val toDate = to.map(LocalDate.parse).getOrElse(LocalDate.now())
      historicalVolUseCase
        .extract(ProductType.IndexFuture, "NK225", fromDate, toDate).map(Ok)
  }

  implicit def date: DecodeEntity[LocalDate] = s => Try { LocalDate.parse(s) }

  val greeksJson =
    get(paramOption[LocalDate]("date") :: "greeks" :: path[String] :: path[String] :: path[String]) {
      (date: Option[LocalDate], delivery: String, strike: String, poc: String) =>
        val valuationDate = date.getOrElse(calendar.latestBusinessDay)
        (
          for {
            n <- productRepository
              .findBy(BigDecimal(strike), PutOrCall.of(poc), YearMonth.fromSixNum(delivery)).map(_.productName).underlying
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
        ).map(Ok)
    }

  val ep = vol :+: greeksJson

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
