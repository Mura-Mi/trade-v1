package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import com.twitter.util.{Await, Future}
import wvlet.airframe._
import wvlet.log.LogSupport
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.IndexOptionRepository

trait CurlJpxOptionReportUseCase extends LogSupport {
  private val jpxOptionPriceReader = bind[JpxOptionPriceReader]
  private val priceRepo =
    bind[DailyMarketPriceRepository[TwFutureTatriaContext]]
  private val productRepo = bind[IndexOptionRepository]

  def run(today: LocalDate): Future[(Long, Long)] = {
    implicit val c = new TwFutureTatriaContext
    info(s"today: $today")
    jpxOptionPriceReader
      .get(today).flatMap(
        result => {
          val futPro =
            Future
              .collect { result.productMaster.map(productRepo.store) }.map(
                _.sum).onSuccess(v => info(s"product: $v"))
          val futPrice = c
            .collect { result.prices.map(priceRepo.store(_)) }.map(_.sum)

          for {
            product <- futPro
            price <- futPrice.underlying.onSuccess(v => info(s"price: $v"))
          } yield (product, price)
        }
      )
  }
}
