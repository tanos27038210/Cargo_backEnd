CREATE TABLE status_history
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    entity_type        VARCHAR(30) NOT NULL CHECK (entity_type IN ('ORDER', 'PARCEL')),
    entity_id          BIGINT      NOT NULL,
    old_status         VARCHAR(30) NOT NULL,
    new_status         VARCHAR(30) NOT NULL,
    changed_by_user_id BIGINT      NOT NULL REFERENCES users (id),
    comment            VARCHAR(250),
    created_at         TIMESTAMP   NOT NULL DEFAULT NOW()
);