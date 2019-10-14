package yokohama.murataku.trade.persistence.finagle

import io.getquill.{FinaglePostgresContext, SnakeCase}

trait PersistenceContextProvider {
  def getContext: FinaglePostgresContext[SnakeCase]
}

object PersistenceContextProvider extends PersistenceContextProvider {
  override lazy val getContext: TmtPersistenceContext =
    new TmtPersistenceContext(SnakeCase, "ctx")
}
