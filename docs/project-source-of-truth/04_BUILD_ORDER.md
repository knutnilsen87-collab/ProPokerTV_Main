# Build Order and First PRs

## Execution rule

Build the ecosystem as a sequence of loops, not as one large app.

Each PR must be small enough to verify and must preserve repo health.

## Recommended PR sequence

### PR 1 — Frontend CI

Goal: prevent false green.

Scope:

- Add `.github/workflows/frontend-ci.yml`.
- Run from `apps/web`.
- Use `npm ci` if lockfile exists, otherwise `npm install` until lockfile is committed.
- Run `npm run build`.

Success condition:

- Backend CI still passes.
- Frontend build runs in GitHub Actions.
- No product behavior changes.

Fallback:

- If lockfile/setup blocks CI, add documented local `npm run build` gate and create follow-up issue for lockfile/CI.

### PR 2 — Contest-first homepage

Goal: make “Weekly Poker Clip League” the main product experience.

Scope:

- Add or refactor a homepage component/page focused on active weekly contest.
- Show active contest, nominated clips, vote CTA, and leaderboard preview.
- Keep general feed secondary.
- Do not change backend schema unless required.

Success condition:

- New visitor understands “vote for this week’s best poker clip.”
- Existing build passes.
- No duplicate contest models created.

Fallback:

- If API gaps exist, render available contest/leaderboard data and document exact missing endpoint.

### PR 3 — Frontend decomposition

Goal: preserve repo health before adding more flows.

Scope:

- Split large `App.tsx` into owned pages/components.
- Suggested ownership:
  - `pages/HomeContestPage.tsx`
  - `pages/ClipDetailPage.tsx`
  - `pages/AuthPage.tsx`
  - `pages/CreatorProfilePage.tsx`
  - `pages/LeaderboardPage.tsx`
  - `pages/AdminContestPage.tsx`
- Keep route wiring in `App.tsx`.

Success condition:

- No behavior change.
- Frontend build passes.
- Routes remain equivalent.

Fallback:

- Split only the contest/homepage path first if full decomposition becomes too broad.

### PR 4 — Contest finalize job

Goal: contests must produce durable winners and status.

Scope:

- Implement contest finalization in the existing contest module.
- Determine winner by vote count.
- Lock finalized contests.
- Prevent new votes after finalization.
- Produce winner history/snapshot.

Success condition:

- Integration test: create contest → nominate clips → vote → finalize → winner history exists.
- Test: user cannot vote twice.
- Test: finalized contest cannot accept votes.

Fallback:

- Implement admin-triggered finalization before scheduled job.

### PR 5 — Creator reputation v0

Goal: make creators care about winning.

Scope:

- Creator profile shows:
  - wins,
  - nominations,
  - total contest votes,
  - top category,
  - ranking position,
  - badges v0.
- Badges v0:
  - Clip of the Week Winner,
  - Best Bluff Winner,
  - Sickest River Winner,
  - Funniest Poker Moment Winner,
  - Best Hand Breakdown Winner,
  - Rising Creator.

Success condition:

- A finalized contest updates creator reputation.
- Creator profile displays status without hardcoded frontend-only logic.
- Tests or clear smoke path verify reputation update.

Fallback:

- Start with derived read-only stats before persistent badge table.

### PR 6 — Contest lifecycle integration tests

Goal: verify the core loop.

Scope:

- Add integration tests for:
  - admin creates contest,
  - admin nominates approved clip,
  - user votes once,
  - duplicate vote denied,
  - finalize selects winner,
  - finalized contest denies new vote,
  - leaderboard reflects winner/top clips.

Success condition:

- Backend tests pass locally and in CI.
- Tests use canonical domain APIs/services.

Fallback:

- Add service-level tests first if full controller integration is blocked.

### PR 7 — Moderation hardening

Goal: protect quality and brand safety.

Scope:

- Ensure reported clips can enter moderation queue.
- Ensure rejected clips cannot be nominated.
- Ensure role permissions for moderation/admin actions.
- Add basic tests.

Success condition:

- Report/moderation flow is verifiable.
- Admin-only actions are protected.

Fallback:

- Document manual moderation flow if UI is incomplete.

### PR 8 — Upload/storage readiness

Goal: remove friction from creator posting without overbuilding.

Scope:

- Decide link-first vs signed upload for MVP.
- If link-first: document supported URLs and validation.
- If upload: implement signed upload path only if storage contract is clear.
- Do not introduce broad media pipeline unless required.

Success condition:

- Creator can add playable clip reliably.
- Invalid/unsafe media inputs are rejected or contained.

Fallback:

- Launch MVP with link-based clips and defer signed upload.

### PR 9 — Analytics events v0

Goal: measure the loop.

Scope:

Track at minimum:

- creator_signed_up,
- creator_profile_completed,
- clip_created,
- clip_approved,
- clip_nominated,
- contest_vote_cast,
- contest_finalized,
- creator_repeat_posted,
- comment_created,
- reaction_created,
- report_created.

Success condition:

- Events can be emitted/logged and counted.
- Metrics map to MVP success metrics.

Fallback:

- Use backend logs/database queries initially.

### PR 10 — Founder Season readiness

Goal: run the first manual market test.

Scope:

- Seed/demo active contest.
- Admin/moderator runbook.
- Creator outreach copy.
- Partner pitch copy.
- Manual smoke checklist.

Success condition:

- Team can run a real weekly contest with 20 creators.
- Known risks and manual steps are documented.

Fallback:

- Run with manual nominations and admin-triggered finalization.
