create table jpx_option_price
(
    date                DATE,
    put_or_call         VARCHAR(64),
    option_product_code VARCHAR(255),
    product_code        VARCHAR(255),
    product_type        VARCHAR(255),
    delivery_limit      VARCHAR(255),
    strike              DECIMAL(20, 10),
    note1               VARCHAR(255),
    close_price         DECIMAL(20, 10),
    spare               VARCHAR(255),
    theoretical_price   DECIMAL(20, 10),
    volatility          DECIMAL(20, 10),
    CONSTRAINT jpx_option_price_date_product_code_unique UNIQUE (product_code, date)
);