-- V2__add_activation_columns.sql
-- Thêm cột token kích hoạt tài khoản vào bảng users.
-- Chạy thủ công một lần — hibernate.hbm2ddl.auto=none nên không tự chạy.

ALTER TABLE users
    ADD COLUMN activation_token VARCHAR(255) NULL AFTER deleted_at;

ALTER TABLE users
    ADD COLUMN activation_expires_at DATETIME NULL AFTER activation_token;

CREATE INDEX idx_users_activation_token ON users (activation_token);

