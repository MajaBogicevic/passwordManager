CREATE TABLE audit_logs (
    id SERIAL PRIMARY KEY,
    user_id INT NULL,
    event_type VARCHAR(100) NOT NULL,
    outcome VARCHAR(50) NOT NULL,
    reason_code VARCHAR(255) NULL,
    ip_address VARCHAR(255) NULL,
    session_id VARCHAR(255) NULL,
    details TEXT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_event_type ON audit_logs(event_type);