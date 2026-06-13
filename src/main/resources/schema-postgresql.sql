CREATE TABLE IF NOT EXISTS usuario (
    id     SERIAL PRIMARY KEY,
    nome   VARCHAR(100),
    email  VARCHAR(150) UNIQUE NOT NULL,
    senha  VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS minha_lista (
    id          SERIAL PRIMARY KEY,
    usuario_id  INTEGER      NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    tmdb_id     BIGINT       NOT NULL,
    titulo      VARCHAR(255),
    tipo        VARCHAR(10),
    poster_path VARCHAR(255),
    status      VARCHAR(50)  DEFAULT 'quero assistir',
    nota        TEXT,
    avaliacao   INTEGER      DEFAULT 0
);

ALTER TABLE minha_lista ADD COLUMN IF NOT EXISTS avaliacao INTEGER DEFAULT 0;
