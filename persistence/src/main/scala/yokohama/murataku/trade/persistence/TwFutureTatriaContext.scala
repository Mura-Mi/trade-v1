package yokohama.murataku.trade.persistence

import com.twitter.util.{Await, Future}
import yokohama.murataku.trade.persistence.typedef.{TatriaContext, TatriaResult}

class TwFutureTatriaContext extends TatriaContext {

  type Result[+E, +A] = TwFutureTatriaResult[E, A]

  override def success[E, A](a: A): Result[E, A] =
    TwFutureTatriaResult(Future.value(a))

  def fromFuture[A](a: Future[A]): Result[Throwable, A] =
    TwFutureTatriaResult(a)

  override def collect[E, A](results: Seq[TwFutureTatriaResult[E, A]]): Result[E, Seq[A]] =
    TwFutureTatriaResult {
      Future.collect {
        results.map(_.underlying)
      }
    }

  override def unsafeGet[E, A](result: TwFutureTatriaResult[E, A]): A =
    Await.result {
      result.underlying
    }

  case class TwFutureTatriaResult[+E, +A](underlying: Future[A]) extends TatriaResult[TwFutureTatriaResult, E, A] {
    override def map[B](f: A => B): TwFutureTatriaResult[E, B] =
      TwFutureTatriaResult(underlying.map(f))

    override def flatMap[B, EE >: E](f: A => TwFutureTatriaResult[EE, B]): TwFutureTatriaResult[EE, B] =
      TwFutureTatriaResult {
        underlying.flatMap(a => f(a).underlying)
      }

    override def onSuccess(f: A => Unit): TwFutureTatriaResult[E, A] =
      TwFutureTatriaResult {
        underlying.onSuccess(f)
      }
  }
}
