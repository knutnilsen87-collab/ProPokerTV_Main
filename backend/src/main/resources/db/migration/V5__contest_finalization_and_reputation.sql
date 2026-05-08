ALTER TABLE weekly_contest
    ADD COLUMN winner_entry_id BIGINT REFERENCES weekly_contest_entry(id),
    ADD COLUMN finalized_at TIMESTAMPTZ;

ALTER TABLE weekly_contest_entry
    ADD CONSTRAINT uq_weekly_contest_entry_clip UNIQUE (weekly_contest_id, clip_id);

CREATE INDEX idx_weekly_contest_status_finalized_at ON weekly_contest(status, finalized_at);
