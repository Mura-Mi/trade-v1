package yokohama.murataku.trade.evaluation

import java.time.LocalDate

import wvlet.log.Logger
import yokohama.murataku.testutil.MyTestSuite
import yokohama.murataku.trade.product.PutOrCall

class BlackScholesFormulaTest extends MyTestSuite {
  Logger.scheduleLogLevelScan

  val today = LocalDate.of(2019, 1, 1)

  "result including time value" in {
    BlackScholesFormula
      .price(PutOrCall.Call,
             110,
             100,
             0.17,
             expiry = today.plusDays(30),
             today = today) shouldBe 10.048835748918 +- 1e-10
  }

  "put/call parity" in {
    val call = BlackScholesFormula.price(PutOrCall.Call,
                                         105,
                                         100,
                                         0.15,
                                         today.plusDays(60),
                                         today)
    val put = BlackScholesFormula.price(PutOrCall.Put,
                                        105,
                                        100,
                                        0.15,
                                        today.plusDays(60),
                                        today)

    call - put shouldBe 5.0 +- 1e-10
  }

}
