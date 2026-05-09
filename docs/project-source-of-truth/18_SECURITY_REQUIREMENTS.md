# ProPokerTV — Security Requirements

**Purpose:** Define the security baseline for ProPokerTV so developers and Codex do not ship insecure league, profile, upload, calendar, affiliate or admin features.

## 1. Security baseline

Target:

```text
OWASP ASVS Level 1 for MVP
Selected ASVS Level 2 controls for auth, admin, upload, moderation, voting, calendar affiliate links and partner boundaries
```

The goal is not “use AES-256 and call it secure.” The goal is verified security across auth, authorization, voting integrity, uploads, moderation, calendar/affiliate disclosures, secrets and auditability.

## 2. Cryptography

### Required

- HTTPS/TLS in all non-local environments.
- Managed encryption at rest for database and object storage where available.
- AES-256-GCM only for application-level field/object encryption when truly needed.
- Never implement custom cryptography.
- Never commit keys, tokens, secrets, credentials or private certificates.
- Store secrets in a secret manager or environment-specific secure config.

### Do not

- encrypt passwords
- store raw passwords
- use AES as a substitute for access control
- log secrets, tokens or signed upload URLs

Passwords must be hashed, not encrypted.

## 3. Authentication

Required:

- password hashing with Argon2id or bcrypt
- short-lived access tokens
- refresh token rotation
- refresh tokens stored hashed
- logout/revoke invalidates refresh token
- rate limiting on login/register/password reset
- email verification before sensitive actions
- admin/moderator MFA before public launch
- no demo accounts in production

## 4. Authorization

Roles:

```text
anonymous
user
creator
moderator
admin
partner
```

Rules:

- default deny
- backend enforces authorization, never frontend only
- creator can mutate only own profile/clips
- user can mutate only own votes/reactions/comments
- moderator/admin APIs require explicit role checks
- partner APIs must enforce tenant/partner ownership
- all sensitive object actions check ownership

## 5. Contest and voting integrity

Required invariants:

```text
One user can vote only according to the defined contest/category voting rule.
Voting is allowed only while contest is open.
Finalized contests cannot receive new votes.
Finalized contest results are immutable except through audited admin correction.
```

Implementation requirements:

- database unique constraint for vote rule
- backend validation of contest state
- audit event on vote cast
- audit event on contest finalization
- audit event on result correction
- tests for vote-once and finalized-contest lock

## 6. Media upload security

Before broad public upload:

- signed upload URLs
- content type allowlist
- file size limits
- extension validation plus MIME sniffing
- random object keys
- storage outside app server
- uploaded file state: pending_scan, approved, rejected
- malware scanning or quarantine/manual approval before public visibility
- safe image/video fallback in UI
- upload audit events

## 7. Moderation and community safety

Required:

- report clip/comment
- moderation queue
- approve/reject/remove actions
- reason codes
- audit log for moderation decisions
- admin/moderator-only enforcement
- no silent destructive moderation without audit

## 8. Calendar / affiliate safety

Calendar/event links can generate affiliate or partner revenue, but must be safe and transparent.

Required:

- affiliate or sponsored links clearly disclosed
- ProPokerTV does not present itself as a gambling operator
- ProPokerTV does not accept wagers or deposits
- age/jurisdiction disclaimers shown when relevant
- sponsored placements distinguishable from organic listings
- unsafe or expired events can be removed by admin
- outbound clicks eventually tracked through backend endpoint
- do not hardcode affiliate links only in frontend long term

## 9. API security

Required:

- validation on all DTOs
- pagination limits
- request size limits
- CORS restricted to allowed origins in prod
- CSRF strategy based on token/cookie architecture
- standard error contract
- no stack traces to clients
- OpenAPI should not expose unsafe admin operations without auth
- security headers:
  - Content-Security-Policy
  - X-Content-Type-Options
  - Referrer-Policy
  - Permissions-Policy
  - HSTS in prod

## 10. Database and secrets

Required:

- no secrets in repo
- separate local/staging/prod config
- Flyway migrations deterministic
- least-privilege DB user in prod
- backups before public use
- audit tables/events for admin, moderation, contest-finalize, event publish/remove
- PII minimization

## 11. Audit events

Minimum audit events:

```text
user.registered
user.login_failed
user.login_succeeded
token.refreshed
token.revoked
clip.created
clip.approved
clip.rejected
clip.reported
contest.created
contest.nominated_clip
contest.vote_cast
contest.finalized
contest.result_corrected
event.created
event.published
event.removed
event.outbound_click
role.changed
admin.action_performed
```

Never log:

```text
passwords
tokens
auth headers
private secrets
full signed URLs
payment credentials
```

## 12. Security Definition of Done

A security-sensitive feature is not done unless:

- authorization is enforced in backend
- ownership checks are tested
- invalid input is tested
- abuse case is considered
- audit event is emitted where required
- no secrets are introduced
- backend tests pass
- frontend build passes if UI changed
- docs are updated if behavior or security posture changed

## 13. Priority backlog

### P0 before real users

1. remove demo/debug from production paths
2. lock CORS to allowed origins
3. role/ownership checks for creator, clip, contest, moderation
4. vote-once DB constraint
5. finalized contest lock
6. basic auth rate limiting
7. secrets hygiene
8. production security headers

### P1 before broad creator upload

1. signed upload URLs
2. file size/type validation
3. upload scan/quarantine flow
4. refresh token rotation
5. admin/moderator MFA
6. moderation queue hardening

### P2 before partner/commercial scale

1. partner tenant boundary tests
2. event click tracking backend
3. sponsored placement disclosure
4. affiliate reporting
5. audit dashboard
6. lightweight security review/pentest
