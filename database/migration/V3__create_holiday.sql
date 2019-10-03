create table holiday
(
    date   DATE,
    market VARCHAR(16),
    note   VARCHAR(255),
    CONSTRAINT holiday_uqique UNIQUE (date, market)
);