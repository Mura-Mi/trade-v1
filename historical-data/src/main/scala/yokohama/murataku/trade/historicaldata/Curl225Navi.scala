package yokohama.murataku.trade.historicaldata

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.twitter.util.{Await, Future}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import wvlet.log.LogSupport

import scala.collection.JavaConverters

object Curl225Navi extends App with LogSupport {
  val doc: Document = Jsoup.connect("http://225navi.com/data/").get()

  import JavaConverters._

  val repo = new HistoricalPriceRepository

  Await.result {
    Future.collect {
      doc
        .select(".sc_table tr")
        .asScala //
        .drop(2) // drop headers
        .map { row =>
          val date = row.select("th").asScala.head.text()
          val data = row.select("td").asScala
          val open = data.head.text()
          val high = data(1).text()
          val low = data(2).text()
          val close = data(3).text()

          val p = DateTimeFormatter.ofPattern("yyyy/M/d")

          repo.store("product",
                     LocalDate.parse(date, p),
                     BigDecimal(open),
                     BigDecimal(high),
                     BigDecimal(low),
                     BigDecimal(close))
        }
    }
  }
}
