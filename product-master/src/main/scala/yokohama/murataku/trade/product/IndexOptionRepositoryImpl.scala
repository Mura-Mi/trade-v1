package yokohama.murataku.trade.product

import com.twitter.util.Future
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.persistence.{
  PersistenceSupport,
  TwFutureTatriaContext
}
import yokohama.murataku.trade.persistence.finagle.TmtPersistenceContext
import yokohama.murataku.trade.product.indexoption.{
  IndexOption,
  IndexOptionName,
  IndexOptionRepository,
  PutOrCall
}

class IndexOptionRepositoryImpl(tmtCtx: TmtPersistenceContext)
    extends IndexOptionRepository[TwFutureTatriaContext]
    with PersistenceSupport
    with Encodings {

  import tmtCtx._

  override def findBy(strike: BigDecimal,
                      putOrCall: PutOrCall,
                      deliveryLimit: YearMonth)(
      implicit ctx: TwFutureTatriaContext): ctx.Result[Throwable, IndexOption] =
    ctx.fromFuture {
      run {
        quote {
          query[IndexOption].filter(
            row =>
              row.strike == lift(strike) &&
                row.putOrCall == lift(putOrCall) &&
                row.deliveryLimit == lift(deliveryLimit))
        }
      }.map(_.headOption.getOrElse(throw new IllegalArgumentException(
        s"not found: [$strike $putOrCall $deliveryLimit]")))
    }

  override def find(productName: IndexOptionName)(
      implicit ctx: TwFutureTatriaContext): ctx.Result[Throwable, IndexOption] =
    ctx.fromFuture {
      run {
        quote {
          query[IndexOption].filter(_.productName == lift(productName))
        }
      }.map(_.headOption.getOrElse(
        throw new IllegalArgumentException(productName.value)))
    }

  override def store(indexOption: IndexOption)(
      implicit ctx: TwFutureTatriaContext): ctx.Result[Throwable, Long] =
    ctx.fromFuture {
      run {
        quote {
          query[IndexOption]
            .insert(lift(indexOption))
            .onConflictIgnore(_.indexName,
                              _.putOrCall,
                              _.deliveryLimit,
                              _.strike)
        }
      }
    }
}
