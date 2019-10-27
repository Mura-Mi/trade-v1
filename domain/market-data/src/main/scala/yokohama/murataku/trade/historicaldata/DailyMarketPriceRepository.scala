package yokohama.murataku.trade.historicaldata

import java.time.LocalDate

import yokohama.murataku.trade.persistence.typedef.TatriaContext
import yokohama.murataku.trade.product.ProductType

trait DailyMarketPriceRepository[Context <: TatriaContext] {
  def store(e: DailyMarketPrice)(
      implicit ctx: Context): ctx.Result[Throwable, Long]
  def store(productType: ProductType,
            productName: String,
            date: LocalDate,
            open: BigDecimal = null,
            high: BigDecimal = null,
            low: BigDecimal = null,
            close: BigDecimal = null)(
      implicit ctx: Context): ctx.Result[Throwable, Long]

  def find(productType: ProductType,
           productName: String,
           date: LocalDate)(implicit ctx: Context): ctx.Result[
    Throwable,
    Option[DailyMarketPrice]] // TODO replace option with exception

  def select(productType: ProductType,
             productName: String,
             since: LocalDate,
             until: LocalDate)(
      implicit ctx: Context): ctx.Result[Throwable, Seq[DailyMarketPrice]]
}
