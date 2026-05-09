# ProPokerTV — Codex Next Implementation Prompt

Copy this into Codex for the next safe PR.

## Task

Update ProPokerTV navigation, profile direction and Calendar foundation.

## Scope

- `apps/web` frontend first
- preserve existing backend contracts unless fields already exist
- no broad backend migration in this PR
- do not change auth/token behavior
- do not introduce a new design framework
- do not create `helpers`, `misc`, `temp`, `new`, or `v2` dumping grounds

## Product intent

ProPokerTV is a premium non-gambling poker media and reputation platform.

The MVP wedge is the Weekly Poker Clip League.

The current profile page is too thin. It must become a premium creator/player identity editor. Calendar must become a first-class nav item for upcoming tournaments/events and future affiliate/partner revenue.

## Navigation

Use main nav:

```text
Home
League
Clips
Rankings
Calendar
Creators
Upload
Profile
```

Rules:

- Keep League first-class.
- Add Calendar nav item.
- Prefer premium account/avatar menu over raw email pill if feasible.
- Keep Sign out accessible.
- No broken nav routes.
- Active state must be clear.

## Profile

Redesign profile settings as a premium multi-section editor.

Sections/tabs:

```text
Public Profile
Poker Identity
Creator Tools
League Stats
Calendar Preferences
Account
```

Keep currently supported fields functional:

```text
username
display name
bio
avatar URL
banner URL
```

If backend does not support planned fields yet:

- render as disabled/planned, or
- make them UI-only placeholders with clear copy,
- but do not fake persistence.

Profile copy:

```text
Build your poker identity. Your profile powers creator discovery, league rankings, calendar recommendations and partner opportunities.
```

Profile preview must feel like a premium creator card.

## Calendar

Add `/calendar` route/page if not already present.

Calendar page must:

- explain upcoming tournaments/events and partner registrations
- use premium Midnight Gold Editorial League direction
- include affiliate disclosure:
  “Some event links may be partner or affiliate links. ProPokerTV is a non-gambling media platform. We do not operate gambling services.”
- avoid gambling/deposit language
- show empty/coming-soon state or placeholder event cards

## Validation

Run:

```bash
cd apps/web
npm run build
```

Manual smoke:

```text
homepage loads
nav links work
Calendar route works
profile page opens
existing supported profile fields still save
profile preview renders
mobile layout usable
desktop layout usable
no debug banners
no broken image icons
```

## Repo health constraints

- use owned profile/settings components
- use owned calendar route/components
- do not duplicate API logic
- do not hardcode backend-inconsistent models as if persisted
- avoid growing `App.tsx` further if possible
- keep changes small and reviewable

## Success condition

The PR is successful only when:

- build passes
- approved nav is visible
- Calendar route does not break
- profile settings are visibly upgraded into a premium identity/reputation editor
- existing profile save behavior remains intact
- no backend-breaking assumptions are introduced
