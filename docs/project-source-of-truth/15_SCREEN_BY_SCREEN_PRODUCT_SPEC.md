# 15 — Screen-by-Screen Product Spec

This document defines the MVP screens needed to turn ProPokerTV into the Weekly Poker Clip League.

## Global App Shell

Primary nav:

- Weekly Contest
- Clips
- Leaderboard
- Creators
- Profile

Conditional nav:

- Admin / Moderator, only for authorized users.

## Priority Screens

1. Weekly Contest Home
2. Clip Detail
3. Creator Profile reputation block
4. Leaderboard
5. Admin Contest Management
6. Moderation Queue
7. Submit Clip improvements
8. Partner Pilot Page, deferred until loop proof

## Required States

All API-backed screens must handle loading, empty, error, unauthenticated, and unauthorized where relevant.

## Weekly Contest Home

Goal: make the active weekly contest the main product experience.

Required content:

- active contest title,
- voting close date/time,
- contest status,
- nominated clips,
- vote CTA,
- user voted state where available,
- top leaderboard preview,
- submit clip CTA,
- winner module if finalized.

DoD:

- user understands the weekly contest within 3 seconds,
- logged-in user can vote once,
- closed/finalized contests do not show active voting CTA,
- mobile layout is usable at 360px.

## Admin Contest Management

Goal: allow staff to operate weekly contests.

Required content:

- create contest,
- open contest,
- nominate approved clips,
- view vote counts,
- finalize contest,
- view winner snapshot.

Required permission: admin/moderator.

DoD:

- admin can run one contest end-to-end,
- finalized contest is locked from normal vote changes,
- winner history is created on finalize.

## Moderation Queue

Goal: keep the platform safe and non-gambling/brand-safe.

Required content:

- reported clips/comments,
- approve/reject/remove actions for clips,
- reason visibility.

DoD:

- moderators can review reports,
- unsafe content can be removed or blocked.
