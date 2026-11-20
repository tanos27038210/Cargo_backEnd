CREATE TABLE messages
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id   BIGINT        NOT NULL,
    author_id  BIGINT        NOT NULL REFERENCES users (id),
    text       VARCHAR(1000) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP     NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_message_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
);