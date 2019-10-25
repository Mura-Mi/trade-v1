package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import com.twitter.util.Future
import yokohama.murataku.trade.product.ProductType

trait DailyMarketPriceRepository {
  def store(productType: ProductType,
            productName: String,
            date: LocalDate,
            open: BigDecimal = null,
            high: BigDecimal = null,
            low: BigDecimal = null,
            close: BigDecimal = null): Future[Unit]

  def find(productType: ProductType,
           productName: String,
           date: LocalDate): Future[Option[DailyMarketPrice]]

  def select(productType: ProductType,
             productName: String,
             since: LocalDate,
             until: LocalDate): Future[Seq[DailyMarketPrice]]
}
