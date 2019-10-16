package yokohama.murataku.trade.http.pages

import scalatags.Text.all._
import yokohama.murataku.trade.analysis.vol.DailyVolatility

class ShowHistoricalVolPage(vols: Seq[DailyVolatility]) {
  def toHtml(): String = {
    val header = tr(th("num"), th("name"))
    val records = vols.map(vol => tr(td(vol.date.toString), td(vol.vol)))
    html(
      body(
        table(header +: records)
      )
    ).render
  }
}
