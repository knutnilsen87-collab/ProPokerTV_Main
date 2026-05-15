CREATE TABLE event_outbound_click (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES poker_event(id),
    user_id BIGINT REFERENCES app_user(id),
    session_id VARCHAR(120),
    target_url_type VARCHAR(30) NOT NULL,
    clicked_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    referrer_page VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_event_outbound_click_event_id ON event_outbound_click(event_id);
CREATE INDEX idx_event_outbound_click_clicked_at ON event_outbound_click(clicked_at);
