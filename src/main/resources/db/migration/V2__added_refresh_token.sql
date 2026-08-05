-- 1. Створення таблиці refresh_tokens
CREATE TABLE refresh_tokens (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    revoked BOOLEAN NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    PRIMARY KEY (id)
);

-- 2. Додавання зовнішнього ключа (Foreign Key) на таблицю users
ALTER TABLE refresh_tokens
    ADD CONSTRAINT FK_refresh_tokens_on_users
    FOREIGN KEY (user_id) REFERENCES users (id);