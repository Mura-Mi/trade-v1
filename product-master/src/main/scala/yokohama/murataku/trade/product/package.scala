package yokohama.murataku.trade

import wvlet.airframe.Design
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.indexoption.IndexOptionRepository

package object product {
  lazy val design: Design = wvlet.airframe.newDesign
    .bind[IndexOptionRepository[TwFutureTatriaContext]].to[
      IndexOptionRepositoryImpl]
}
