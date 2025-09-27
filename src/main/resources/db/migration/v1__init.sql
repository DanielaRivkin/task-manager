CREATE TABLE tasks (
                       id UUID PRIMARY KEY,
                       title VARCHAR(200) NOT NULL,
                       description TEXT,
                       status VARCHAR(20) NOT NULL,
                       priority VARCHAR(20) NOT NULL,
                       assignee VARCHAR(120),
                       category VARCHAR(120),
                       due_date TIMESTAMPTZ,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                       updated_at TIMESTAMPTZ,
                       completed_at TIMESTAMPTZ,
                       version BIGINT
);


-- Useful indexes for filtering and queries
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
