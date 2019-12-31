package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.airframe._
import wvlet.log.LogSupport
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.IndexOptionRepositoryImpl

trait CurlJpxOptionReportUseCase extends LogSupport {
  private implicit val tatriaContext: TwFutureTatriaContext =
    bind[TwFutureTatriaContext]
  private val jpxOptionPriceReader = bind[JpxOptionPriceReader]
  private val priceRepo =
    bind[DailyMarketPriceRepository[TwFutureTatriaContext]]
  private val productRepo = bind[IndexOptionRepositoryImpl]

  def run(today: LocalDate): Future[(Long, Long)] = {
    info(s"today: $today")
    jpxOptionPriceReader
      .get(today).flatMap(
        result => {
          val futPro =
            tatriaContext
              .collect { result.productMaster.map(productRepo.store(_)) }.map(_.sum).onSuccess(v =>
                info(s"product: $v"))
          val futPrice = tatriaContext
            .collect { result.prices.map(priceRepo.store(_)) }.map(_.sum)

          (for {
            product <- futPro
            price <- futPrice.onSuccess(v => info(s"price: $v [date=$today]"))
          } yield (product, price)).underlying
        }
      )
  }
}
