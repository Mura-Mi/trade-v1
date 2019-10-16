package yokohama.murataku.trade.persistence.finagle

import wvlet.airframe._

object ActualPersistenceContextDesign {
  val design: Design = newDesign
    .bind[TmtPersistenceContext].toInstance(
      PersistenceContextProvider.getContext)
}
