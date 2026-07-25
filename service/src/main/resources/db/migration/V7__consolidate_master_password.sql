ALTER TABLE users RENAME COLUMN login_password_hash TO password_hash;
ALTER TABLE users DROP COLUMN master_password_hash;

ALTER TABLE users ADD COLUMN key_salt VARCHAR(255);
ALTER TABLE users ADD COLUMN wrapped_data_key TEXT;

