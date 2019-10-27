package yokohama.murataku.trade.persistence.typedef

trait TatriaContext {
  type Result[+E, +A] <: TatriaResult[Result, E, A]

  def success[E, A](a: A): Result[E, A]

  def collect[E, A](results: Seq[Result[E, A]]): Result[E, Seq[A]]

  def unsafeGet[E, A](result: Result[E, A]): A
}

trait TatriaResult[F[+ _, + _], +E, +A] { self =>
  def map[B](f: A => B): F[E, B]

  def flatMap[B, EE >: E](f: A => F[EE, B]): F[EE, B]

  def onSuccess(f: A => Unit): F[E, A]
}
