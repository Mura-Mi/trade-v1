package yokohama.murataku.trade.evaluation.formula

case object HistoricalVolatilityFormula {
  def from(prices: Seq[Double]): Double = {
    val priceChangeRatio = prices.zip(prices.drop(1)).map {
      case (before, after) => (after - before) / before
    }
    val sum = priceChangeRatio.sum
    val cnt = priceChangeRatio.size
    val avg = sum / cnt

    Math.sqrt(priceChangeRatio.map(v => math.pow(v - avg, 2)).sum / cnt)
  }
}
