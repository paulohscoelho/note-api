CREATE TABLE IF NOT EXISTS tb_users (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS tb_notes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    user_id UUID NOT NULL,
    CONSTRAINT fk_notes_user FOREIGN KEY (user_id) REFERENCES tb_users(id) ON DELETE CASCADE
);