create table index_option(
    id uuid,
    product_name varchar(255),
    put_or_call char(1),
    delivery_limit varchar(8),
    delivery_date date,
    strike decimal(10,5),
    constraint index_option_unique UNIQUE(put_or_call, delivery_limit, strike)
)