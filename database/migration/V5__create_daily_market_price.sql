create table daily_market_price
(
    id           uuid primary key,
    product_type varchar(255) not null,
    product_name varchar(255) not null,
    date         date         not null,
    open         decimal(15, 5),
    high         decimal(15, 5),
    low          decimal(15, 5),
    close        decimal(15, 5),
    created_at   timestamp default current_timestamp,
    constraint daily_market_price_unique UNIQUE (product_type, product_name, date)
);
