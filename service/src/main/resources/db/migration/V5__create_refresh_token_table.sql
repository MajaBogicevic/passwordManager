CREATE TABLE refresh_tokens (
    token_id VARCHAR(255) PRIMARY KEY,
    user_id INT NOT NULL,
    token_family_id VARCHAR(255) NOT NULL,
    expires_at_millis BIGINT NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    consumed BOOLEAN NOT NULL DEFAULT FALSE,
    family_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_refresh_tokens_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_family_id ON refresh_tokens(token_family_id);