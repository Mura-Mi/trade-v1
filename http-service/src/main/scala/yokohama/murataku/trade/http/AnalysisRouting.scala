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

  val vol = get("vol" :: paramOption[LocalDate]("from") :: paramOption[LocalDate]("to")) {
    (from: Option[LocalDate], to: Option[LocalDate]) =>
      val fromDate = from.getOrElse(LocalDate.now().minusYears(3))
      val toDate = to.getOrElse(LocalDate.now())
      historicalVolUseCase
        .extract(ProductType.IndexFuture, "NK225", fromDate, toDate).map(Ok)
  }

  implicit def date: DecodeEntity[LocalDate] = s => Try { LocalDate.parse(s) }
  implicit def pocDecoder: DecodeEntity[PutOrCall] = s => Try(PutOrCall.of(s))
  implicit def pathImplicit[A](implicit de: DecodeEntity[A]): DecodePath[A] = s => de.apply(s).toOption
  implicit def deliveryDecoder: DecodeEntity[YearMonth] = s => Try(YearMonth.fromSixNum(s))
  implicit def decimalDecoder: DecodeEntity[BigDecimal] = s => Try(BigDecimal(s))

  val greeksJson =
    get(paramOption[LocalDate]("date") :: "greeks" :: path[YearMonth] :: path[BigDecimal] :: path[PutOrCall]) {
      (date: Option[LocalDate], delivery: YearMonth, strike: BigDecimal, poc: PutOrCall) =>
        val valuationDate = date.getOrElse(calendar.latestBusinessDay)
        (
          for {
            n <- productRepository
              .findBy(strike, poc, delivery).map(_.productName).underlying
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
