CREATE TABLE IF NOT EXISTS projects (
    id_project TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    local_path TEXT NOT NULL UNIQUE,
    remote_url TEXT,
    added_at TEXT NOT NULL
);