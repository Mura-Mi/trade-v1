package yokohama.murataku.trade.product.indexoption

import java.time.LocalDate
import java.util.UUID

import yokohama.murataku.testutil.MyTestSuite
import yokohama.murataku.trade.lib.date.YearMonth
import yokohama.murataku.trade.product.{IndexName, indexoption}

class IndexOptionTest extends MyTestSuite {
  "intrinsic value" when {
    "call option" when {
      val option = IndexOption(
        id=UUID.randomUUID(),
        indexName= IndexName("dummy"),
        productName= IndexOptionName("dummy"),
        putOrCall= PutOrCall.Call,
        deliveryLimit= YearMonth.of(2019,10),
        deliveryDate= LocalDate.now(),
        strike=100
      )

      "ItM" in {
        option.intrinsicValueAt(120) shouldBe 20
      }

      "OtM" in {
        option.intrinsicValueAt(98) shouldBe 0
      }
    }
    "put option" when {
      val option = indexoption.IndexOption(
        id=UUID.randomUUID(),
        indexName= IndexName("dummy"),
        productName= IndexOptionName("dummy"),
        putOrCall= PutOrCall.Put,
        deliveryLimit= YearMonth.of(2019,10),
        deliveryDate= LocalDate.now(),
        strike=100
      )

      "ItM" in {
        option.intrinsicValueAt(95) shouldBe 5
      }

      "OtM" in {
        option.intrinsicValueAt(110) shouldBe 0
      }
    }
  }
}
