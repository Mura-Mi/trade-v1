package yokohama.murataku.trade.product

trait Product {}

case class IndexFuture(productName: String, deliveryLimit: String)
    extends Product
case class IndexOption(productName: String,
                       underlyingProductName: String,
                       putOrCall: PutOrCall,
                       deliveryLimit: String,
                       strike: BigDecimal)
    extends Product
