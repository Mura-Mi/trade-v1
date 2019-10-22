package yokohama.murataku.trade.volatility

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.airframe._
import yokohama.murataku.trade.evaluation.option.{
  OptionEvaluationFunction,
  OptionValuationSet
}
import yokohama.murataku.trade.historicaldata.HistoricalPriceRepository
import yokohama.murataku.trade.holiday.{Calendar, HolidayRepository}
import yokohama.murataku.trade.product.{IndexOptionName, IndexOptionRepository}

trait CalculateOptionGreeksUseCase {
  private val optionRepository = bind[IndexOptionRepository]
  private val priceRepository = bind[HistoricalPriceRepository]
  private val holidayRepository = bind[HolidayRepository]

  def run(productName: IndexOptionName,
          valuationDate: LocalDate): Future[OptionValuationSet] = {
    for {
      indexOption <- optionRepository.find(productName)
      optionPrice <- priceRepository.fetchOptionPrice(valuationDate,
                                                      indexOption.putOrCall,
                                                      indexOption.deliveryLimit,
                                                      "NK225E",
                                                      indexOption.strike)
      futurePrice <- priceRepository
        .fetchFuturePrice("NK225", valuationDate, valuationDate).map(
          _.filter(_.date == valuationDate).head)
    } yield {
      implicit val cal: Calendar = holidayRepository
      OptionEvaluationFunction.apply(indexOption,
                                     optionPrice.closePrice,
                                     futurePrice.close,
                                     valuationDate)
    }
  }
}
