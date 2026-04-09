CREATE TABLE password_entries (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    folder_id INT NULL,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(1000) NULL,
    username VARCHAR(255) NOT NULL,
    encrypted_password TEXT NOT NULL,
    notes TEXT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_password_entries_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_password_entries_folder
        FOREIGN KEY (folder_id) REFERENCES folders(id) ON DELETE SET NULL
);

CREATE INDEX idx_password_entries_user_id ON password_entries(user_id);
CREATE INDEX idx_password_entries_folder_id ON password_entries(folder_id);
CREATE INDEX idx_password_entries_user_title ON password_entries(user_id, title);