CREATE TABLE orders
(
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_number VARCHAR(30) NOT NULL,
    customer_id          BIGINT                                             NOT NULL  REFERENCES customers (id),
    manager_id           BIGINT                                             NOT NULL  REFERENCES users (id),
    origin_address       VARCHAR(100)                                       NOT NULL,
    destination_address  VARCHAR(100)                                       NOT NULL,
    delivery_type        VARCHAR(30) CHECK ( delivery_type  IN ('STANDARD', 'EXPRESS')) NOT NULL,
    price                NUMERIC(10, 2)                                     NOT NULL,
    status               VARCHAR(30) CHECK ( status IN ('DRAFT', 'CONFIRMED', 'PICKUP_SCHEDULED',
                                                    'IN_TRANSIT', 'AT_WAREHOUSE', 'OUT_FOR_DELIVERY',
                                                    'DELIVERED', 'CANCELLED', 'RETURNED'))  DEFAULT 'DRAFT'      NOT NULL,
    expected_delivery_date DATE                                          NOT NULL,
    notes                VARCHAR(250),
    created_at           TIMESTAMP                                          NOT NULL DEFAULT NOW(),
    updated_at           TIMESTAMP                                          NOT NULL DEFAULT NOW(),
    UNIQUE (order_number)
);