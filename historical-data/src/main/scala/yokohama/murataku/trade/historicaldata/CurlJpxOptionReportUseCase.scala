package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import com.twitter.util.{Await, Future}
import wvlet.airframe._
import wvlet.log.LogSupport
import yokohama.murataku.trade.product.IndexOptionRepository

trait CurlJpxOptionReportUseCase extends LogSupport {
  private val jpxOptionPriceReader = bind[JpxOptionPriceReader]
  private val priceRepo = bind[DailyMarketPriceRepository]
  private val productRepo = bind[IndexOptionRepository]

  def run(today: LocalDate): Future[(Long, Long)] = {
    info(s"today: $today")
    jpxOptionPriceReader
      .get(today).flatMap(
        result => {
          val futPro =
            Future
              .collect { result.productMaster.map(productRepo.store) }.map(
                _.sum).onSuccess(v => info(s"product: $v"))
          val futPrice = Future
            .collect { result.prices.map(priceRepo.store) }.map(_.sum).onSuccess(
              v => info(s"price: $v"))

          for {
            product <- futPro
            price <- futPrice
          } yield (product, price)
        }
      )
  }
}
