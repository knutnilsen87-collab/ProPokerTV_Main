# 16 — Frontend Component Ownership

## Purpose

This document prevents frontend entropy. ProPokerTV must not keep growing as one large `App.tsx` or a pile of loosely owned components.

## Ownership Rules

Pages own route-level composition only.

Examples:

- `pages/WeeklyContestPage.tsx`
- `pages/ClipDetailPage.tsx`
- `pages/CreatorProfilePage.tsx`
- `pages/LeaderboardPage.tsx`
- `pages/AdminContestPage.tsx`
- `pages/ModerationQueuePage.tsx`

Feature folders own product-domain UI and feature-specific hooks.

Shared UI should be small and generic. Shared UI must not own contest-specific vote logic, creator ranking rules, leaderboard calculations, moderation policies, or API contract assumptions.

## App.tsx Rule

`App.tsx` should eventually contain:

- providers,
- router,
- app shell,
- route declarations.

It should not contain complete page implementations, large forms, leaderboard calculations, contest business logic, or moderation UI internals.

## Required Verification Per Frontend PR

- `npm run build`
- manual smoke path in PR notes
- no TypeScript errors
- no known broken route
