package yokohama.murataku.trade.persistence.finagle

import wvlet.airframe._
import yokohama.murataku.trade.persistence.TwFutureTatriaContext

object ActualPersistenceContextDesign {
  val design: Design = newDesign
    .bind[TmtPersistenceContext].toInstance(
      PersistenceContextProvider.getContext)
    .bind[TwFutureTatriaContext].toSingleton
}
