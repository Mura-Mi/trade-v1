package yokohama.murataku.trade.historicaldata

trait ProductType {
}

object ProductType {
  case object IndexFuture extends ProductType
  case object IndexOption extends ProductType
}


