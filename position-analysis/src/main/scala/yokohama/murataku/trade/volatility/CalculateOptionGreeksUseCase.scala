package yokohama.murataku.trade.volatility

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.airframe._
import yokohama.murataku.trade.evaluation.option.{
  OptionEvaluationFunction,
  OptionValuationSet
}
import yokohama.murataku.trade.historicaldata.DailyMarketPriceRepository
import yokohama.murataku.trade.holiday.{Calendar, HolidayRepository}
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.ProductType
import yokohama.murataku.trade.product.indexoption.{
  IndexOptionName,
  IndexOptionRepository
}

trait CalculateOptionGreeksUseCase {
  private val optionRepository =
    bind[IndexOptionRepository[TwFutureTatriaContext]]
  private val priceRepository =
    bind[DailyMarketPriceRepository[TwFutureTatriaContext]]
  private val holidayRepository = bind[HolidayRepository]
  private implicit val ct: TwFutureTatriaContext = bind[TwFutureTatriaContext]

  def run(productName: IndexOptionName,
          valuationDate: LocalDate): Future[OptionValuationSet] = {
    val r = for {
      indexOption <- optionRepository.find(productName)
      optionPrice <- priceRepository
        .find(ProductType.IndexOption,
              indexOption.productName.value,
              valuationDate)
      futurePrice <- priceRepository
        .find(ProductType.IndexFuture, "NK225", valuationDate)
    } yield {
      implicit val cal: Calendar = holidayRepository
      OptionEvaluationFunction.apply(indexOption,
                                     optionPrice.flatMap(_.close).get,
                                     futurePrice.flatMap(_.close).get,
                                     valuationDate)
    }

    r.underlying
  }
}
