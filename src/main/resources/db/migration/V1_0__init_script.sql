CREATE TABLE stock (
    id SERIAL NOT NULL PRIMARY KEY,
    quarter NUMERIC(1,0),
    ticker VARCHAR(10),
    stock_date DATE,
    open NUMERIC(10,2),
    high NUMERIC(10,2),
    low NUMERIC(10,2),
    close NUMERIC(10,2),
    volume NUMERIC(20,0),
    percent_change_price DECIMAL,
    percent_change_volume_over_last_week DECIMAL,
    previous_weeks_volume NUMERIC(20,0),
    next_weeks_open NUMERIC(10,2),
    next_weeks_close NUMERIC(10,2),
    percent_change_next_weeks_price DECIMAL,
    days_to_next_dividend NUMERIC(5),
    percent_return_next_dividend DECIMAL
);

CREATE UNIQUE INDEX index_name on stock (ticker,stock_date);