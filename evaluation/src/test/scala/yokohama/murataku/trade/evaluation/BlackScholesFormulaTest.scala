package yokohama.murataku.trade.evaluation

import java.time.LocalDate

import wvlet.log.{LogLevel, Logger}
import yokohama.murataku.testutil.MyTestSuite
import yokohama.murataku.trade.product.PutOrCall

class BlackScholesFormulaTest extends MyTestSuite {
  Logger.scheduleLogLevelScan

  val today = LocalDate.of(2019, 1, 1)

  "result including time value" in {
    Logger.setDefaultLogLevel(LogLevel.DEBUG)
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

  "test" in {
    println(
      BlackScholesFormula.impliedVol(
        PutOrCall.Put,
        underlying = 21410.20,
        strike = 21875,
        expiry = LocalDate.of(2019, 10, 10),
        today = LocalDate.of(2019, 10, 4),
        price = 565
      )
      /*
      BlackScholesFormula.price(
//        PutOrCall.Put,
        PutOrCall.Call,
        underlying = 21410.20,
        strike = 21875,
//        vol = 0.227249,
        vol = 0.139447,
        expiry = LocalDate.of(2019, 10, 10),
        today = LocalDate.of(2019, 10, 4)
      )
     */
    )
  }

}
