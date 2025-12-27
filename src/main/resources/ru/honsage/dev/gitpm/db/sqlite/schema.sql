CREATE TABLE IF NOT EXISTS project (
    id_project TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    local_path TEXT NOT NULL UNIQUE,
    remote_url TEXT,
    added_at TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS task (
    id_task TEXT PRIMARY KEY,
    id_project TEXT NOT NULL,
    title TEXT NOT NULL,
    content TEXT,
    created_at TEXT NOT NULL,
    is_completed INTEGER NOT NULL,
    deadline_at TEXT,
    priority TEXT NOT NULL,

    FOREIGN KEY (id_project) REFERENCES project(id_project) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS script (
    id_script TEXT PRIMARY KEY,
    id_project TEXT NOT NULL,
    title TEXT NOT NULL,
    description TEXT,
    working_dir TEXT NOT NULL,
    command TEXT NOT NULL

    FOREIGN KEY (id_project) REFERENCES project(id_project) ON DELETE CASCADE
);