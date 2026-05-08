# Codex / Agent Execution Guide

This guide tells Codex or an AI developer how to work in this repo.

## Operating mode

Use bounded execution:

1. Read source-of-truth docs.
2. Inspect current repo files before editing.
3. Make the smallest safe change.
4. Run targeted verification.
5. Update docs/status bundle when facts change.

## Required first checks

Before changing code:

- read `README.md`,
- read `SOURCE_OF_TRUTH.md`,
- read `machine-readable/blueprint.json`,
- read this folder,
- inspect current file/module ownership,
- identify tests/build commands.

## Do not

- Do not create duplicate domain models.
- Do not bypass existing backend modules.
- Do not dump broad generic templates into repo.
- Do not turn reference/archive material into runtime code without explicit decision.
- Do not add marketplace/AI/mobile/livestreaming before weekly contest loop is verified.
- Do not claim success without running relevant verification.
- Do not leave dead code or ambiguous TODOs.
- Do not use vague names like `misc`, `helpers`, `new`, `temp`, `v2` unless justified.

## Default validation commands

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd apps/web
npm install
npm run build
```

If CI exists and lockfile exists:

```bash
cd apps/web
npm ci
npm run build
```

Fullstack smoke target:

1. Backend starts.
2. Health endpoint returns UP.
3. Frontend builds.
4. User can login.
5. Active contest is visible.
6. User can vote.
7. Duplicate vote is denied.
8. Leaderboard/result updates where implemented.

## Next action contract

For each task, write:

```md
## Plan
- Target files:
- Reason:
- Risk:
- Validation:

## Repo-health check
- Correct module/layer:
- Duplicate/parallel abstraction risk:
- Boundary impact:
- Cleanup needed:

## Result
- Changed:
- Verified:
- Not verified:
- Follow-up:
```

## Current recommended next action

PR 1:

> Add frontend CI for `apps/web`.

Success condition:

- workflow runs on push/PR affecting `apps/web/**`,
- installs dependencies,
- runs frontend build,
- backend CI remains unchanged.

Fallback:

- if dependency lockfile issue blocks `npm ci`, use `npm install` temporarily and create follow-up to commit lockfile.

## Current fallback next action

PR 2:

> Implement `HomeContestPage` as a contest-first homepage using existing available APIs.

Success condition:

- contest is the primary page experience,
- build passes,
- API gaps are documented.

## Status-bundle requirement

After meaningful work, update:

- `10_STATUS_BUNDLE.md`
- `machine-readable/status_bundle.json`
- `machine-readable/next_prs.json` if PR order changes
