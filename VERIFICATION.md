# Verification

Run date: 2026-05-08

## Frontend

Command:

```powershell
cd apps/web
npm install
npm run build
```

Result:

- TypeScript check passed.
- Vite production build passed.
- Output generated under `apps/web/dist/`.

## Backend

Commands:

```powershell
cd backend
docker compose up -d
..\tools\apache-maven-3.9.12\bin\mvn.cmd "-Dmaven.repo.local=..\tools\m2" test
..\tools\apache-maven-3.9.12\bin\mvn.cmd "-Dmaven.repo.local=..\tools\m2" spring-boot:run
```

Result:

- Maven compile passed.
- Backend tests passed.
- Flyway validated and applied 4 migrations.
- Runtime health check returned HTTP 200 with:

```json
{"status":"UP","groups":["liveness","readiness"]}
```

Cleanup:

- Runtime process was stopped after health check.
- Docker Compose was stopped with `docker compose down`.

Notes:

- Maven was downloaded locally into `tools/` for verification because Maven was not installed globally.
- `tools/`, `target/`, `dist/`, and `node_modules/` are ignored by git.
