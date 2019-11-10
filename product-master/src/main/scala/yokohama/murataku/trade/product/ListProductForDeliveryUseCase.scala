package yokohama.murataku.trade.product

import cats.data.NonEmptyList
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.typedef.TatriaContext
import yokohama.murataku.trade.product.indexoption.{IndexOption, IndexOptionRepository}

class ListProductForDeliveryUseCase[Context <: TatriaContext](val repository: IndexOptionRepository[Context]) {

  def run(delivery: YearMonth)(implicit ctx: Context): ctx.Result[Throwable, NonEmptyList[IndexOption]] = {
    repository.listAll(delivery)
  }
}
