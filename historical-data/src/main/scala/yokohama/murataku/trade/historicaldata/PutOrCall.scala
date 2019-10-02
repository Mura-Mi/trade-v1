package yokohama.murataku.trade.historicaldata

sealed trait PutOrCall {
  def isCall: Boolean
}

object PutOrCall {
  case object Put extends PutOrCall {
    override def isCall: Boolean = false
  }
  case object Call extends PutOrCall {
    override def isCall: Boolean = true
  }
}
