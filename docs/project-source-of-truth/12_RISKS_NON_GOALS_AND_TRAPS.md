# Risks, Non-Goals, and Traps

## Strategic traps

### Trap 1 — Building a generic feed

Risk:

A clip feed is easy to copy and gives weak reason to return.

Countermeasure:

Make weekly contest and creator status the primary experience.

### Trap 2 — Building the whole ecosystem too early

Risk:

Too many half-finished features and no validated loop.

Countermeasure:

Build one loop first: creator post → fan vote → winner → status → repeat.

### Trap 3 — Weak creator motivation

Risk:

Creators do not post repeatedly.

Countermeasure:

Make profile status, badges, rankings, and winner history visible early.

### Trap 4 — Fan passivity

Risk:

Fans watch but do not vote/comment/react.

Countermeasure:

Homepage CTA must be voting-driven. Categories should be emotionally clear.

### Trap 5 — No commercial path

Risk:

B2C engagement exists but no payer.

Countermeasure:

After loop proof, test club/event sponsored contests.

## Engineering traps

### Trap 1 — Parallel domain models

Do not create separate contest/reputation models outside existing modules unless explicitly required.

### Trap 2 — Frontend hardcoded truth

Do not hardcode reputation or winner history in frontend-only state.

### Trap 3 — Unbounded App.tsx growth

Decompose pages before adding more flows.

### Trap 4 — Missing finalization

Contest without finalization does not create reputation.

### Trap 5 — False green

Build passing is not enough if vote invariants, permissions, or finalized lock are untested.

### Trap 6 — Reference material confusion

`reference/` is not canonical runtime code.

## Product non-goals before MVP proof

- native mobile app,
- livestreaming,
- marketplace,
- advanced AI,
- full partner dashboard,
- complex recommendations,
- tournament OS,
- gambling/betting,
- broad international launch.

## Legal/safety risk

ProPokerTV must stay non-gambling.

Avoid:

- betting flows,
- odds,
- wagers,
- casino deposits,
- gambling affiliate core positioning,
- claims that imply gambling participation.

Allow:

- poker media,
- poker education,
- creator clips,
- non-gambling contests,
- reputation/rankings,
- club/event media pages,
- brand-safe sponsorships.

## Moderation risk

Poker clips and comments are user-generated content. Public launch requires:

- report path,
- moderation queue,
- admin/moderator permissions,
- rejection/removal state,
- abuse handling,
- basic content policy.

## Security risk

Before public beta, verify:

- no production secrets committed,
- local demo credentials are marked local-only,
- auth-protected endpoints require auth,
- admin endpoints require admin/moderator roles,
- CORS is production-safe,
- upload/link inputs are validated.
