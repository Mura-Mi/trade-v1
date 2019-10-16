package yokohama.murataku.trade.http

import java.time.LocalDate

import com.twitter.util.Future
import wvlet.airframe.http.Endpoint
import wvlet.airframe._
import yokohama.murataku.trade.analysis.vol.CalculateHistoricalVolatilityUseCase
import yokohama.murataku.trade.http.pages.ShowHistoricalVolPage

@Endpoint(path = "")
trait AnalysisRouting {
  val uc = bind[CalculateHistoricalVolatilityUseCase]

  @Endpoint(path = "/vol")
  def vol: Future[String] = {
    uc.extract("NK225", LocalDate.now().minusYears(3), LocalDate.now)
      .map(record => new ShowHistoricalVolPage(record).toHtml())
  }
}
