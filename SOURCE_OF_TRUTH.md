# ProPokerTV Source of Truth

This folder is the consolidated main repo for ProPokerTV.

## Canonical Code

- `backend/` is the canonical Spring Boot backend, taken from `PPTV2026`.
- `apps/web/` is the canonical React/Vite frontend, taken from `PPTV2026` because it already talks to the Spring Boot API.

## Imported Reference Material

- `docs/`, `delivery/`, and `machine-readable/` come from the strongest `PPTV2026` architecture and handoff package.
- `apps/web/public/` includes selected brand and thumbnail assets from `PPTVny-slim-git/frontend`.
- `reference/master-pack-docs/` preserves the master-pack README/status from `ProPokerTV_April`.
- `reference/frontend-next/` preserves useful QA material from the richer Next.js frontend branch.

## Archived Inputs

The old workspace folders remain outside this repo as history/reference only:

- `PPTV2026`
- `PPTVny-slim-git`
- `PPTVny`
- `ProPokerTV_April`
- `ProPokerTVOld`
- `ProPokerTV_WorldClass`

Future work should start here unless a specific file is intentionally imported from an archive.
