package yokohama.murataku.trade.http

import wvlet.airframe.http.Endpoint
import yokohama.murataku.trade.http.pages.ShowHistoricalVol

@Endpoint(path = "")
trait AnalysisRouting {
  @Endpoint(path = "/vol")
  def vol: String = ShowHistoricalVol.toHtml()
}
