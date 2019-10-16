package yokohama.murataku.trade.http.pages

import scalatags.Text.all._

object ShowHistoricalVol {
  def toHtml(): String =
    html(
      body(
        table(tr(th("num"), th("name")),
              tr(td("5"), td("takuro")),
              tr(td("2"), td("haru")),
              tr(td("7"), td("takanori")),
              tr(td("23"), td("rose")))
      )
    ).render
}
