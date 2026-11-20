CREATE TABLE customers
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(150) NOT NULL,
    company_name VARCHAR(150),
    email        VARCHAR(100),
    phone        VARCHAR(50) NOT NULL,
    address      VARCHAR(100),
    notes        VARCHAR(250),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP NOT NULL DEFAULT NOW()
);