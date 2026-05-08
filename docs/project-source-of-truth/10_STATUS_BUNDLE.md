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
