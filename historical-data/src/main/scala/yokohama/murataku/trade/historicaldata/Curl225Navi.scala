package yokohama.murataku.trade.historicaldata

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.twitter.util.{Await, Future}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import wvlet.log.LogSupport

import scala.collection.JavaConverters._

object Curl225Navi extends App with LogSupport {
  case class DataSource(productName: String, url: String)

  val nk225Large = DataSource("NK225", "http://225navi.com/data/")
  val nk225Mini = DataSource("NK225-mini", "http://225navi.com/data/data3/")
  val dataSources = Seq(nk225Large, nk225Mini)

  val repo = new HistoricalPriceRepository

  dataSources.map { source =>
    val doc: Document = Jsoup.connect(source.url).get()

    val count = Await.result {
      val count = Future
        .collect {
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

              repo.store(source.productName,
                         LocalDate.parse(date, p),
                         BigDecimal(open),
                         BigDecimal(high),
                         BigDecimal(low),
                         BigDecimal(close))
            }
        }
        .map(_.sum)
      count
    }

    info(s"${source.productName}: $count")
  }
}
