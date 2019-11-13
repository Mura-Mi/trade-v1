package yokohama.murataku.trade.http

sealed abstract class TatriaPresentationException(val message: String, val cause: Throwable)
    extends Exception(message, cause)

case class ResourceNotFoundException() extends TatriaPresentationException(null, null)

case class IllegalPathArgumentException(msg: String, override val cause: Throwable)
    extends TatriaPresentationException(msg, cause)
