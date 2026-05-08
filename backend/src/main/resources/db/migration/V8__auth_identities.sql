CREATE TABLE auth_identity (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    provider VARCHAR(40) NOT NULL,
    provider_subject VARCHAR(190) NOT NULL,
    email VARCHAR(190),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_auth_identity_provider_subject UNIQUE (provider, provider_subject)
);

CREATE INDEX idx_auth_identity_user_id ON auth_identity(user_id);
