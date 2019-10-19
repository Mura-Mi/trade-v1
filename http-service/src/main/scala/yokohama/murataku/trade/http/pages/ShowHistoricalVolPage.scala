package yokohama.murataku.trade.http.pages

import scalatags.Text.all._
import yokohama.murataku.trade.analysis.vol.DailyVolatility

class ShowHistoricalVolPage(vols: Seq[DailyVolatility]) {
  def toHtml(): String = {
    val header = tr(th("num"), th("name"))
    val records = vols.map(vol => tr(td(vol.date.toString), td(vol.vol)))
    html(
      head(
        link(
          rel := "stylesheet",
          href := "https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.min.css")
      ),
      body(
        div(
          height := 600,
          width := 600,
          canvas(id := "mychart", height := 400, width := 400),
        ),
        script(
          src := "https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.js"),
        script(
          src := "https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.8.0/Chart.js"),
        script(src := "http://localhost:8080/static/js/vol/show.js")
      )
    ).render
  }
}
