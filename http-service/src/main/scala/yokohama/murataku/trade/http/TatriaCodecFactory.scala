package yokohama.murataku.trade.http

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.airframe.codec.{
  INVALID_DATA,
  MessageCodec,
  MessageCodecException,
  MessageCodecFactory,
  MessageHolder,
  MessageValueCodec
}
import wvlet.airframe.msgpack.spi.Value.StringValue
import wvlet.airframe.msgpack.spi.{Packer, Unpacker, Value, ValueType}
import wvlet.airframe.surface.Surface

import scala.reflect.runtime.universe.TypeTag

trait TatriaCodecFactory {
  protected def codecOf[A: TypeTag]: MessageCodec[A] = codecFactory.of[A]
  protected val codecFactory: MessageCodecFactory =
    wvlet.airframe.codec.MessageCodecFactory.defaultFactory.withObjectMapCodec
      .withCodecs(
        Map(
          Surface.of[LocalDate] -> new MessageValueCodec[LocalDate] {
            override def packValue(v: LocalDate): Value =
              StringValue(v.toString)

            override def unpackValue(v: Value): LocalDate =
              v match {
                case StringValue(str) => LocalDate.parse(str)
                case other =>
                  throw new IllegalArgumentException(s"unexpected: $other")
              }
          },
          Surface.of[BigDecimal] -> new MessageCodec[scala.BigDecimal] {
            override def pack(p: Packer, v: scala.BigDecimal): Unit = {
              if (v % 1 == BigDecimal(0))
                p.packBigInteger(v.toBigInt().bigInteger)
              else
                p.packDouble(
                  v.toDouble // should consider using string and client side parse
                )
            }
            override def unpack(u: Unpacker, v: MessageHolder): Unit = {
              u.getNextValueType match {
                case ValueType.STRING =>
                  val s = u.unpackString
                  v.setObject(scala.BigDecimal(s))
                case ValueType.INTEGER =>
                  val l = u.unpackLong
                  v.setObject(scala.BigDecimal(l))
                case ValueType.FLOAT =>
                  val f = u.unpackDouble
                  v.setObject(scala.BigDecimal(f))
                case other =>
                  v.setError(
                    new MessageCodecException(
                      INVALID_DATA,
                      this,
                      s"Cannot construct java.math.BigDecimal from ${other} type")
                  )
              }
            }
          }
        )
      )

  implicit class FutureExt[A](future: Future[A]) {
    def toJsonResponse(implicit typeTag: TypeTag[A]): Future[String] =
      future.map(codecOf[A].toJson)
  }
}

object TatriaCodecFactory {}
