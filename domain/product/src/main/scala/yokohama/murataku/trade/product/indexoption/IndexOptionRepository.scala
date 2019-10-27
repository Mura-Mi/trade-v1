package yokohama.murataku.trade.product.indexoption

import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.typedef.TatriaContext

trait IndexOptionRepository[Context <: TatriaContext] {

  def findBy(strike: BigDecimal,
             putOrCall: PutOrCall,
             deliveryLimit: YearMonth)(
      implicit ctx: Context): ctx.Result[Throwable, IndexOption]

  def find(productName: IndexOptionName)(
      implicit ctx: Context): ctx.Result[Throwable, IndexOption]

  def store(indexOption: IndexOption)(
      implicit ctx: Context): ctx.Result[Throwable, Long]
}
