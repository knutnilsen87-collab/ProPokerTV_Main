CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(190) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    account_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE profile (
    user_id BIGINT PRIMARY KEY REFERENCES app_user(id),
    username VARCHAR(40) NOT NULL UNIQUE,
    display_name VARCHAR(80) NOT NULL,
    bio VARCHAR(400),
    avatar_url VARCHAR(500),
    banner_url VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE creator_profile (
    user_id BIGINT PRIMARY KEY REFERENCES app_user(id),
    creator_slug VARCHAR(80) NOT NULL UNIQUE,
    headline VARCHAR(120),
    is_verified BOOLEAN NOT NULL DEFAULT FALSE,
    social_links_json TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    replaced_by_token_hash VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE clip (
    id BIGSERIAL PRIMARY KEY,
    owner_user_id BIGINT NOT NULL REFERENCES app_user(id),
    slug VARCHAR(160) NOT NULL UNIQUE,
    title VARCHAR(140) NOT NULL,
    description VARCHAR(1000),
    visibility VARCHAR(30) NOT NULL DEFAULT 'PUBLIC',
    moderation_status VARCHAR(30) NOT NULL DEFAULT 'PENDING_REVIEW',
    category_slug VARCHAR(60),
    tags_csv VARCHAR(500),
    thumbnail_url VARCHAR(500),
    playback_url VARCHAR(500),
    duration_seconds INT,
    view_count BIGINT NOT NULL DEFAULT 0,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_clip_moderation_status ON clip(moderation_status);
CREATE INDEX idx_clip_owner_user_id ON clip(owner_user_id);

CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY,
    clip_id BIGINT NOT NULL REFERENCES clip(id),
    author_user_id BIGINT NOT NULL REFERENCES app_user(id),
    parent_comment_id BIGINT REFERENCES comment(id),
    body VARCHAR(1000) NOT NULL,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comment_clip_id ON comment(clip_id);

CREATE TABLE reaction (
    id BIGSERIAL PRIMARY KEY,
    clip_id BIGINT NOT NULL REFERENCES clip(id),
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    reaction_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_reaction_clip_user_type UNIQUE (clip_id, user_id, reaction_type)
);

CREATE TABLE report (
    id BIGSERIAL PRIMARY KEY,
    target_type VARCHAR(30) NOT NULL,
    target_id BIGINT NOT NULL,
    reporter_user_id BIGINT NOT NULL REFERENCES app_user(id),
    reason VARCHAR(80) NOT NULL,
    note VARCHAR(1000),
    status VARCHAR(30) NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE weekly_contest (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(140) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
    starts_at TIMESTAMPTZ NOT NULL,
    ends_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE weekly_contest_entry (
    id BIGSERIAL PRIMARY KEY,
    weekly_contest_id BIGINT NOT NULL REFERENCES weekly_contest(id),
    clip_id BIGINT NOT NULL REFERENCES clip(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE vote (
    id BIGSERIAL PRIMARY KEY,
    weekly_contest_id BIGINT NOT NULL REFERENCES weekly_contest(id),
    entry_id BIGINT NOT NULL REFERENCES weekly_contest_entry(id),
    voter_user_id BIGINT NOT NULL REFERENCES app_user(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_vote_contest_voter UNIQUE (weekly_contest_id, voter_user_id)
);
