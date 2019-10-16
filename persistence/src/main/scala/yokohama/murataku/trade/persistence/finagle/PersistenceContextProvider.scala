package yokohama.murataku.trade.persistence.finagle

import io.getquill.SnakeCase

trait PersistenceContextProvider {
  def getContext: TmtPersistenceContext
}

object PersistenceContextProvider extends PersistenceContextProvider {
  override lazy val getContext: TmtPersistenceContext =
    new TmtPersistenceContext(SnakeCase, "ctx")
}
