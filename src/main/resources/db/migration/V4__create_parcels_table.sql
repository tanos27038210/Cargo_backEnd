CREATE TABLE parcels
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id        BIGINT         NOT NULL,
    tracking_number VARCHAR(30)    NOT NULL UNIQUE,
    description     VARCHAR(100)   NOT NULL,
    weight_kg       NUMERIC(8, 2)  NOT NULL CHECK ( weight_kg > 0 ),
    length_cm       NUMERIC(8, 2) CHECK (length_cm > 0),
    width_cm        NUMERIC(8, 2) CHECK (width_cm > 0),
    height_cm       NUMERIC(8, 2) CHECK (height_cm > 0),
    declared_value  NUMERIC(10, 2) NOT NULL                                                   DEFAULT 0 CHECK ( declared_value >= 0 ),
    fragile         BOOLEAN        NOT NULL                                                   DEFAULT FALSE,
    status          VARCHAR(30)    NOT NULL CHECK (status IN ('REGISTERED', 'PICKED_UP',
                                                              'IN_TRANSIT', 'SORTING_CENTER',
                                                              'OUT_FOR_DELIVERY', 'DELIVERED',
                                                              'DAMAGED', 'LOST', 'RETURNED')) DEFAULT 'REGISTERED',
    created_at      TIMESTAMP      NOT NULL                                                   DEFAULT NOW(),
    updated_at      TIMESTAMP      NOT NULL                                                   DEFAULT NOW(),
    CONSTRAINT fk_parcel_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);