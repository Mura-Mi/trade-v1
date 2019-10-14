package yokohama.murataku.trade.persistence.finagle

import java.time.LocalDate

import com.twitter.finagle.postgres.PostgresClient
import io.getquill.util.LoadConfig
import io.getquill.{
  FinaglePostgresContext,
  FinaglePostgresContextConfig,
  SnakeCase
}

class TmtPersistenceContext(override val naming: SnakeCase,
                            client: PostgresClient)
    extends FinaglePostgresContext[SnakeCase](naming, client) {

  def this(naming: SnakeCase, configPrefix: String) =
    this(naming, FinaglePostgresContextConfig(LoadConfig(configPrefix)).client)

  implicit class DateOps(underlying: LocalDate) {
    def <=(other: LocalDate): Quoted[Boolean] =
      quote(infix"$underlying <= $other".as[Boolean])

    def >=(other: LocalDate): Quoted[Boolean] =
      quote(infix"$underlying >= $other".as[Boolean])

    def <(other: LocalDate): Quoted[Boolean] =
      quote(infix"$underlying < $other".as[Boolean])

    def >(other: LocalDate): Quoted[Boolean] =
      quote(infix"$underlying > $other".as[Boolean])
  }
}
