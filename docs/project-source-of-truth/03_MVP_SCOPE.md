# MVP Scope

## MVP hypothesis

Poker creators will post clips weekly if ProPokerTV gives them visibility, competition, status, and a chance to be ranked.

## MVP product

A web app where:

- creators sign up,
- creators create profiles,
- creators upload or link poker clips,
- admin/moderators approve and nominate clips,
- fans vote on active weekly contests,
- winners are finalized,
- creators receive visible status,
- leaderboards show top clips and creators.

## Must have

| Area | Requirement |
|---|---|
| Auth | Signup/login, current user, role-aware protected actions |
| Profiles | User profile and creator profile |
| Clips | Create/list/detail/update/delete or equivalent current repo behavior |
| Feed/Discovery | Active contest-first homepage |
| Comments/Reactions | Basic engagement |
| Reports/Moderation | Report queue and clip moderation |
| Weekly contest | Create/open contest, nominate approved clips, vote once, finalize |
| Leaderboard | Top clips, top creators, winner history |
| Reputation | Wins, nominations, votes, badges v0 on creator profile |
| CI | Backend CI and frontend CI |
| Tests | Integration tests for contest lifecycle and vote invariants |
| Docs | Source-of-truth docs and status bundle |

## Should have

- basic notification hooks or placeholder events,
- simple admin UI for nominations/finalization,
- creator profile public sharing,
- contest category pages,
- analytics event tracking plan,
- seed data for demo contest.

## Could have later

- signed upload pipeline,
- email verification,
- password reset,
- refresh token rotation,
- creator analytics v0,
- partner contest mock package.

## Explicit non-goals before MVP proof

Do not build before core loop proof:

- native mobile app,
- full creator marketplace,
- livestreaming,
- advanced AI tagging,
- complex recommendation engine,
- full partner dashboard,
- gambling/betting functionality,
- broad global multi-language launch,
- tournament operating system,
- advanced video editor.

## MVP success metrics

| Metric | Early target |
|---|---:|
| Creators activated | 20 |
| Clips posted | 50 in 30 days |
| Repeat creators | 30% post more than once |
| Contest votes | 300 in first month |
| Comments/reactions | 100 in first month |
| Weekly contests completed | 1 minimum, 4 target |
| Partner interest | 3 clubs/brands say yes to sponsor/contest test |
| Week 2 vs Week 1 | Week 2 activity equal or higher, or cause documented |

## MVP failure signals

- Creators only post when manually chased.
- Fans watch but do not vote.
- Weekly contest is not visible on homepage.
- Creator profile does not make status desirable.
- No one understands why they should return next week.
- Admin cannot complete contest lifecycle reliably.
