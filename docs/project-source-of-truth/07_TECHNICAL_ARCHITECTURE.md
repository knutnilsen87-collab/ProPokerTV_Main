# Technical Architecture

## Current repo architecture

The current repo is a fullstack web app:

```text
/
  backend/          Spring Boot 3 / Java 21 modular monolith
  apps/web/         React + Vite + TypeScript frontend
  docs/             existing product/technical docs
  delivery/         existing delivery plans
  machine-readable/ existing blueprint
  reference/        imported reference material, not canonical runtime code
```

## Backend

Architecture:

- Spring Boot 3 / Java 21.
- Modular monolith.
- Package-by-feature.
- PostgreSQL.
- Flyway migrations.
- OpenAPI/springdoc.
- Tests and Testcontainers available.

Current backend modules from repo blueprint:

- auth
- user
- profile
- creator
- clip
- comment
- reaction
- moderation
- contest
- leaderboard
- storage
- common

## Frontend

Architecture:

- React 18.
- Vite.
- TypeScript.
- React Router.
- API base defaults to backend.
- Must be decomposed into owned pages/components as features grow.

Recommended frontend structure:

```text
apps/web/src/
  api/
  components/
  pages/
    HomeContestPage.tsx
    ClipDetailPage.tsx
    AuthPage.tsx
    CreatorProfilePage.tsx
    LeaderboardPage.tsx
    AdminContestPage.tsx
  routes/
  types/
  utils/
```

## Ownership rules

### Backend ownership

- `auth` owns login/signup/token/session behavior.
- `profile` owns general user profile.
- `creator` owns creator profile and creator-facing identity.
- `clip` owns clip lifecycle.
- `comment` owns comments.
- `reaction` owns reactions and reaction summaries.
- `moderation` owns reports and moderation decisions.
- `contest` owns weekly contest lifecycle, nominations, voting, finalization.
- `leaderboard` owns ranking/snapshot/read models.
- `storage` owns media/upload/storage contracts.
- `common` owns shared infrastructure only when genuinely cross-cutting.

### Frontend ownership

- Page components own route-level layout.
- API clients own request/response calls.
- Domain-specific UI components should be colocated by feature when possible.
- Do not put all product logic in `App.tsx`.
- Do not hardcode backend-derived reputation in frontend state.

## Boundary rules

- Do not create duplicate contest or leaderboard models in separate modules.
- Do not treat `reference/` as canonical source.
- Do not introduce `misc`, `helpers`, `utils2`, `new`, `temp`, or broad utility buckets.
- Prefer extending existing well-owned modules.
- New files must clarify ownership.
- New packages/modules require a real boundary reason.

## Deployment assumptions

MVP deployment should support:

- backend service,
- PostgreSQL database,
- frontend static hosting,
- environment variable configuration,
- production-safe CORS,
- secret management,
- health check endpoint,
- CI build gates.

## Required technical hardening before public beta

- frontend CI,
- contest lifecycle integration tests,
- refresh token rotation or explicit MVP-safe auth decision,
- upload/link safety decision,
- moderation permission tests,
- production secret separation,
- CORS/environment hardening,
- backup/restore plan for database,
- basic observability/logging for contest/vote failures.
