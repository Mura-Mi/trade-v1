package yokohama.murataku.trade.historicaldata

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import com.twitter.util.{Await, Future}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import yokohama.murataku.trade.lib.batch.StandardBatch
import yokohama.murataku.trade.persistence.TwFutureTatriaContext
import yokohama.murataku.trade.product.ProductType

import scala.collection.JavaConverters._

object Curl225Navi extends StandardBatch {
  case class DataSource(productName: String, url: String)

  val nk225Large = DataSource("NK225", "http://225navi.com/data/")
  val nk225Mini = DataSource("NK225-mini", "http://225navi.com/data/data3/")
  val dataSources = Seq(nk225Large, nk225Mini)

  implicit val tatriaContext: TwFutureTatriaContext =
    new TwFutureTatriaContext
  di.design.build[DailyMarketPriceRepository[TwFutureTatriaContext]] { repo =>
    dataSources
      .map { source =>
        info(s"start fetching: ${source.productName}")
        val doc: Document = Jsoup.connect(source.url).get()

        val count: tatriaContext.Result[Throwable, Long] =
          tatriaContext
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

                  repo.store(ProductType.IndexFuture,
                             source.productName,
                             LocalDate.parse(date, p),
                             BigDecimal(open),
                             BigDecimal(high),
                             BigDecimal(low),
                             BigDecimal(close))
                }
            }
            .map(_.sum).onSuccess(count =>
              info(s"${source.productName}: $count"))
        count
      }.map(result => tatriaContext.unsafeGet(result))
  }
}
