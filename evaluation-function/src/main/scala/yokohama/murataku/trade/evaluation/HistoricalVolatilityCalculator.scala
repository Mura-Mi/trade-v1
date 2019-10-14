package yokohama.murataku.trade.evaluation

case object HistoricalVolatilityCalculator {
  def from(prices: Seq[Double]): Double = {
    val sum = prices.sum
    val cnt = prices.size
    val avg = sum / cnt

    Math.sqrt(prices.map(v => math.pow(v - avg, 2)).sum)
  }
}
