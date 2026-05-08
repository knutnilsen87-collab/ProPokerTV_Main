# Status Bundle

The status bundle is the operational truth for what is known, what is assumed, what is blocked, and what should happen next.

Update this file or `machine-readable/status_bundle.json` after meaningful transitions.

## Current status

```json
{
  "project": "ProPokerTV",
  "bundle_version": 1,
  "updated_at": "2026-05-08",
  "phase": "MVP wedge alignment",
  "current_goal": "Turn existing ProPokerTV foundation into the Weekly Poker Clip League MVP.",
  "current_priority": "Add frontend CI, then make weekly contest the homepage experience.",
  "facts": [
    "Repo contains backend Spring Boot 3 / Java 21 modular monolith.",
    "Repo contains React/Vite frontend.",
    "Blueprint lists implemented slices for auth, profile, creator profile, clips, comments, reactions, reports, moderation, weekly contest and leaderboard.",
    "Blueprint lists unfinished items including integration_tests, signed_uploads, refresh_token_rotation and contest_finalize_job.",
    "Weekly contest foundation defines admin-created contest, approved clip nominations, one vote per user and winner by vote count."
  ],
  "assumptions": [
    "The current repo runs locally as documented.",
    "The immediate business goal is to validate creator/fan contest loop, not broad ecosystem buildout.",
    "Web MVP is the correct first surface."
  ],
  "ambiguity_flags": [
    "Frontend API coverage for contest/admin flow must be verified in code.",
    "Exact current App.tsx decomposition state must be checked before refactor.",
    "Auth/session hardening level must be verified before public beta."
  ],
  "verification_state": {
    "status": "source_of_truth_ready_static_only",
    "must_check": [
      "Run backend tests",
      "Run frontend build",
      "Verify existing contest endpoints",
      "Verify vote-once behavior",
      "Verify admin nomination/finalization support"
    ],
    "maybe_check": [
      "OpenAPI endpoint review",
      "Manual fullstack smoke",
      "Security review for local demo credentials"
    ]
  },
  "scope_lock": {
    "build_now": [
      "frontend CI",
      "contest-first homepage",
      "contest finalization",
      "winner history",
      "creator reputation v0",
      "contest lifecycle tests",
      "moderation/permission hardening"
    ],
    "do_not_build_now": [
      "native mobile app",
      "marketplace",
      "livestreaming",
      "advanced AI tagging",
      "complex recommendation engine",
      "full partner dashboard",
      "gambling/betting features"
    ]
  },
  "recommended_next_action": "PR 1: add frontend CI for apps/web.",
  "fallback_action": "If CI setup blocks, create ContestHomePage using existing API data and document missing endpoint gaps.",
  "stop_path_signal": "If weekly contest lifecycle cannot be verified end-to-end, stop broad feature work and fix contest lifecycle first.",
  "repo_health": {
    "status": "preserve_then_improve",
    "structure_fit": "Use existing backend modules and frontend app; do not create parallel domain layers.",
    "ownership_fit": "Contest owns contest lifecycle; leaderboard owns ranking/read models; creator owns creator profile; frontend pages own route-level UI.",
    "duplication_risk": "Medium if reference material is copied in as runtime code.",
    "boundary_violation_risk": "Medium if reputation is hardcoded in frontend instead of backend-derived.",
    "temporary_mess_introduced": false,
    "cleanup_required": [
      "Decompose large frontend files as feature work grows.",
      "Keep project-source-of-truth docs compact and canonical."
    ]
  },
  "closure_readiness": {
    "can_claim_mvp_success": false,
    "blocked_by": [
      "No independently verified full contest lifecycle yet.",
      "contest_finalize_job not complete according to blueprint.",
      "integration_tests not complete according to blueprint."
    ]
  }
}
```

## Update rules

Update status bundle when:

- a PR changes product scope,
- a PR changes domain model/API,
- a major verification gate passes/fails,
- a current assumption becomes fact,
- a blocker appears or is resolved,
- MVP scope changes.

## Closure rule

Do not claim MVP completion unless `06_ACCEPTANCE_CRITERIA.md` is satisfied or unresolved gaps are explicitly accepted as non-MVP.

## Implementation Update — 2026-05-08

Status moved from static source-of-truth setup to MVP loop implementation.

Implemented:

- Frontend CI workflow for `apps/web`.
- Contest-first homepage for the Weekly Poker Clip League.
- Backend contest finalization with winner entry, finalized timestamp, and winner history.
- Vote hardening: contest must be open/in-window, duplicate votes are denied, entries must belong to the contest.
- Nomination hardening: only approved public clips can be nominated, duplicate nominations are denied.
- Creator reputation v0: wins, nominations, total contest votes, ranking position, top category, and badges.
- Top creators leaderboard.
- Basic upload/link safety: clip media URLs must be HTTPS or local dev URLs.
- Moderation/report hardening: reports must target existing clips or comments.
- Analytics v0 via `PPTV_EVENT` structured log lines for core MVP events.
- Founder Season runbook.

Verified locally:

- `apps/web` production build passed.
- Backend compile passed with Maven.

Completed verification:

- Backend integration tests passed with PostgreSQL on `localhost:55436`.
- Fullstack smoke passed against live backend and frontend services.
- Demo admin login works with `admin@propokertv.test` / `password`.

Next action:

1. Keep Docker/PostgreSQL available for future backend integration tests.
2. Continue shrinking legacy page implementations out of `App.tsx`.
3. Decide signed upload vs link-first launch.
4. Decide whether admin-triggered finalization is enough for MVP or whether scheduled finalization is required before pilot.

## Design Spec Update — 2026-05-08

Implemented from the design-spec package:

- Added binding design direction docs: `14_DESIGN_SYSTEM_AND_UX_DIRECTION.md`, `15_SCREEN_BY_SCREEN_PRODUCT_SPEC.md`, and `16_FRONTEND_COMPONENT_OWNERSHIP.md`.
- Added machine-readable `design_spec.json` and `screen_map.json`.
- Renamed the homepage route implementation to `WeeklyContestPage` via `apps/web/src/pages/WeeklyContestPage.tsx`.
- Added role-gated `AdminContestPage` at `/admin/contests`.
- Added role-gated `ModerationQueuePage` at `/admin/moderation`.
- Updated primary navigation to match MVP scope: Weekly Contest, Clips, Leaderboard, Creators, Upload/Profile, with Admin/Moderation shown only for staff roles.
- Added clip report action on clip detail.

Verified:

- Frontend production build passed after these changes.
- Browser smoke passed for Weekly Contest, Admin Contest, Moderation Queue, and Leaderboard routes.
