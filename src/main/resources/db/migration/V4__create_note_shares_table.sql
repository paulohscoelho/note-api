-- V4: Cria a tabela de compartilhamento de notas (Muitos-para-Muitos)

CREATE TABLE IF NOT EXISTS tb_note_shares (
    note_id BIGINT NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY (note_id, user_id),
    CONSTRAINT fk_shares_note FOREIGN KEY (note_id) REFERENCES tb_notes(id) ON DELETE CASCADE,
    CONSTRAINT fk_shares_user FOREIGN KEY (user_id) REFERENCES tb_users(id) ON DELETE CASCADE
);