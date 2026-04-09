CREATE TABLE vault_sessions (
    user_id INT NOT NULL,
    jwt_token_id VARCHAR(255) NOT NULL,
    unlocked_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity_at TIMESTAMP NOT NULL,
    PRIMARY KEY (user_id, jwt_token_id),
    CONSTRAINT fk_vault_sessions_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_vault_sessions_user_id ON vault_sessions(user_id);
CREATE INDEX idx_vault_sessions_last_activity_at ON vault_sessions(last_activity_at);