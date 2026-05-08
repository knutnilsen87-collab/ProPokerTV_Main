# Start Here — ProPokerTV Project Source of Truth

This folder is the operational source of truth for turning the current ProPokerTV repo into the finished MVP.

## Read order for Codex, agents, and developers

1. `01_PRODUCT_NORTH_STAR.md`
2. `02_END_PRODUCT_DEFINITION.md`
3. `03_MVP_SCOPE.md`
4. `04_BUILD_ORDER.md`
5. `05_DEFINITION_OF_DONE.md`
6. `06_ACCEPTANCE_CRITERIA.md`
7. `07_TECHNICAL_ARCHITECTURE.md`
8. `08_CANONICAL_DOMAIN_MODEL.md`
9. `09_API_AND_DATA_CONTRACTS.md`
10. `10_STATUS_BUNDLE.md`
11. `11_CODEX_EXECUTION_GUIDE.md`
12. `12_RISKS_NON_GOALS_AND_TRAPS.md`
13. `13_GO_TO_MARKET_AND_METRICS.md`
14. `14_DESIGN_SYSTEM_AND_UX_DIRECTION.md`
15. `15_SCREEN_BY_SCREEN_PRODUCT_SPEC.md`
16. `16_FRONTEND_COMPONENT_OWNERSHIP.md`
17. `17_FOUNDER_SEASON_RUNBOOK.md`

Machine-readable files live in `machine-readable/`.

## Current product priority

Build the first wedge:

> The weekly poker clip league.

Creators post poker clips. Fans vote. Winners earn status. Leaderboards and badges turn clips into reputation.

## Current repo reality

The repo already contains a working foundation:

- `backend/` — Spring Boot 3 / Java 21 modular monolith.
- `apps/web/` — React/Vite frontend wired to backend API.
- `docs/` — backend/API/security/observability/admin/storage/frontend/legal starter docs.
- `delivery/` — implementation phases, first 90 days, first PR sequence.
- `machine-readable/blueprint.json` — current project blueprint.
- `reference/` — useful imported material, not canonical runtime code unless intentionally imported.

Known current unfinished items from the repo blueprint:

- `integration_tests`
- `signed_uploads`
- `scheduled_contest_finalize_job`
- fullstack smoke with running backend/frontend

## Immediate recommended PR sequence

1. Run backend integration tests with PostgreSQL available.
2. Smoke Weekly Contest, Admin Contest, and Moderation Queue in browser.
3. Continue decomposing legacy `App.tsx` pages into owned page/feature files.
4. Decide signed upload vs link-first launch.
5. Add scheduled contest finalization if admin-triggered finalization is not enough.

## Agent rule

Do not build broad ecosystem features until the weekly contest loop is verified.

A task is not complete unless:

- it supports the weekly clip league wedge,
- tests/builds pass,
- repo structure remains clear,
- no duplicate domain model is introduced,
- the status bundle is updated.
