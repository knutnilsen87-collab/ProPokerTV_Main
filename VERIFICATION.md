# Verification

Run date: 2026-05-15

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
- Vercel root deploy config was added so Git-integrated deployments install/build from `apps/web` and publish `apps/web/dist`.

## Backend

Commands:

```powershell
cd backend
docker compose up -d
..\tools\apache-maven-3.9.12\bin\mvn.cmd "-Dmaven.repo.local=..\tools\m2" test
..\tools\apache-maven-3.9.12\bin\mvn.cmd "-Dmaven.repo.local=..\tools\m2" spring-boot:run
```

Result:

- Maven compile passed locally with:

```powershell
$env:MAVEN_OPTS='-Djavax.net.ssl.trustStoreType=Windows-ROOT'
..\tools\apache-maven-3.9.12\bin\mvn.cmd "-Dmaven.repo.local=..\tools\m2" -DskipTests compile
```

- Full backend tests require PostgreSQL on `localhost:55436`. Local test execution was attempted, but Docker Desktop returned a 500 response from the Linux engine API and no PostgreSQL service was reachable, so integration tests failed at database connection setup rather than application assertions.
- GitHub Actions backend CI is configured with a PostgreSQL 16 service on `55436` and remains the backend test gate when local Docker is unavailable.

Latest local backend test blocker:

```text
Unable to obtain connection from database: Connection to localhost:55436 refused.
```

Notes:

- Maven was downloaded locally into `tools/` for verification because Maven was not installed globally.
- Maven on this Windows machine needs `-Djavax.net.ssl.trustStoreType=Windows-ROOT` to trust Maven Central through the local certificate chain.
- `tools/`, `target/`, `dist/`, and `node_modules/` are ignored by git.
