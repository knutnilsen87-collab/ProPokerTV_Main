# ProPokerTV Main

Consolidated main repo for ProPokerTV: a non-gambling poker media, creator, clips, community, contest, and leaderboard platform.

## Project Source of Truth

The canonical product and delivery source of truth lives in:

`docs/project-source-of-truth/00_START_HERE.md`

Current priority:

> Build ProPokerTV as the Weekly Poker Clip League MVP.

Do not build broad ecosystem features until the weekly contest loop is verified end-to-end.

## What This Repo Contains

- `backend/` - Spring Boot 3 / Java 21 modular monolith with auth, profiles, creators, clips, comments, reactions, moderation, weekly contests, leaderboards, Flyway, OpenAPI, and tests.
- `apps/web/` - React/Vite frontend wired to the backend API.
- `docs/` - backend, API, security, observability, admin, storage, frontend handoff, and legal starter docs.
- `delivery/` - implementation phases, first 90 days, and first PR sequence.
- `machine-readable/` - project blueprint.
- `reference/` - imported material from older packs that is useful but not canonical runtime code.

## Local Development

### Backend

```powershell
cd backend
docker compose up -d
mvn test
mvn spring-boot:run
```

The backend expects PostgreSQL on host port `55436` and serves:

- Health: `http://localhost:8080/actuator/health`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### Frontend

```powershell
cd apps/web
npm install
npm run build
npm run dev
```

Default API base is `http://localhost:8080`. Override with:

```powershell
$env:VITE_API_BASE="http://localhost:8080"
```

## Demo Accounts

The seed data includes:

- `admin@propokertv.test` / `password`
- `creator@propokertv.test` / `password`
- `fan@propokertv.test` / `password`

## Source of Truth

Start with `docs/project-source-of-truth/00_START_HERE.md` for current product and delivery direction. `SOURCE_OF_TRUTH.md` remains the repo consolidation note. Older folders in `F:\prosjekter\POKER` are now archive/reference material unless intentionally imported here.
