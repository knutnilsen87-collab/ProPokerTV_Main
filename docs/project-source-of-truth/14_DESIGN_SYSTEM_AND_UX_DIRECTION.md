# 14 — Design System and UX Direction

## Purpose

This document is binding for frontend work. ProPokerTV must not drift into a generic video feed. The UI must make the first wedge obvious:

> The weekly poker clip league.

The user should understand within 3 seconds:

1. what is happening this week,
2. which clips are competing,
3. how to vote,
4. who is winning,
5. why creators should post again.

## Product personality

ProPokerTV should feel like:

- sports league,
- creator platform,
- community arena,
- prestige board,
- media network.

It should not feel like:

- a casino,
- betting product,
- generic TikTok clone,
- anonymous upload site,
- cluttered admin dashboard,
- crypto/financial speculation app.

## Design principles

### 1. Contest first

The active weekly contest is the hero experience. A feed can exist, but it must support the weekly contest loop.

Homepage priority:

1. active contest hero,
2. nominated clips,
3. vote CTA,
4. leaderboard preview,
5. creator status CTA,
6. recent clips/feed.

### 2. Reputation is visible

Every meaningful creator surface should show status signals:

- wins,
- nominations,
- total votes,
- rank,
- badges,
- best category,
- recent contest performance.

Do not hide reputation in secondary tabs if it is important to creator motivation.

### 3. Voting must feel consequential

Vote buttons should make clear:

- what the user is voting for,
- whether the user has already voted,
- when voting ends,
- how the vote affects the contest.

### 4. Non-gambling clarity

Avoid UI patterns that imply betting, wagering, odds, cash games, deposits, staking, or financial return.

Allowed language:

- vote,
- contest,
- challenge,
- highlight,
- ranking,
- badge,
- winner,
- creator,
- club,
- event.

Avoid language:

- bet,
- wager,
- odds,
- payout,
- casino,
- deposit,
- cash out,
- jackpot.

### 5. Mobile-first, web-first

Most discovery and voting will likely happen on mobile web. Every MVP screen must work on mobile widths before desktop polish.

Minimum responsive requirement:

- 360px mobile width,
- 768px tablet width,
- 1280px desktop width.

### 6. Empty states drive action

Empty states are not placeholders. They must tell the user what to do next.

Examples:

- no active contest → "Next weekly contest opens soon. Submit a clip to be considered."
- no nominations → "No clips nominated yet. Admins can nominate approved clips."
- no creator clips → "This creator has not posted clips yet."
- no leaderboard data → "Leaderboard appears after the first votes are recorded."

### 7. Loading and error states are part of the feature

A feature is incomplete if it only handles the happy path.

Every API-backed screen must define:

- loading state,
- empty state,
- permission-denied state if relevant,
- network/server error state,
- optimistic/pending state for votes where relevant.

## Visual direction

ProPokerTV must feel like a premium poker media league, not a generic dark web app.

The approved visual direction is:

> Midnight Gold Editorial League

It combines:

- black/private-club atmosphere,
- editorial luxury typography,
- restrained gold accents,
- cinematic poker media surfaces,
- league/ranking/status mechanics.

The app should feel premium, calm, and exclusive, while remaining active enough to support voting, contests, and creator reputation.

Base visual tokens:

```css
:root {
  --pptv-bg: #03070c;
  --pptv-bg-soft: #060b12;
  --pptv-surface: #0b1118;
  --pptv-surface-elevated: #101821;

  --pptv-gold: #d6af5f;
  --pptv-gold-soft: #ead08a;
  --pptv-gold-dark: #9c7834;

  --pptv-text: #f4eee2;
  --pptv-text-muted: #9da8b7;
  --pptv-text-subtle: #6f7a87;

  --pptv-border: rgba(214, 175, 95, 0.18);
  --pptv-border-soft: rgba(255, 255, 255, 0.08);
}
```

Typography direction:

```css
--font-display: "Cormorant Garamond", "Playfair Display", Georgia, serif;
--font-body: Inter, ui-sans-serif, system-ui, sans-serif;
```

Hero and major headings use editorial display typography:

```css
font-family: var(--font-display);
font-size: clamp(3.8rem, 7vw, 7.5rem);
font-weight: 500;
letter-spacing: 0.02em;
line-height: 0.95;
text-transform: uppercase;
color: var(--pptv-gold);
```

Nav and body use clean sans-serif. Navigation labels may use `letter-spacing: 0.12em` and uppercase treatment.

Homepage must not become a static luxury landing page. It must show product proof:

- active contest,
- nominee clips,
- current leader,
- votes,
- time left,
- rankings,
- creator status.

Avoid bright casino red/green, overfilled gold decoration, betting-adjacent visual language, and unreleased VIP/livestream/masterclass concepts.

## UX copy rules

Use direct action-oriented copy.

Good:

- "Vote for this week's best poker clip"
- "Submit a clip for next week's contest"
- "You have already voted in this category"
- "Winner announced when voting closes"
- "Creator rank updates after finalized contests"

Bad:

- "Explore content"
- "Engage with media"
- "Interact with platform"
- "Utilize leaderboard functionality"

## Navigation model

MVP navigation should stay small:

- Home/League
- Clips
- Rankings
- Creators
- Upload
- Sign in
- Join the League
- Profile
- Admin or Moderator, only for authorized users

Do not add broad navigation for future ecosystem features until they are real.

## Accessibility baseline

- All interactive elements must be keyboard reachable.
- Vote buttons must have clear text labels.
- Images/video thumbnails must have meaningful alt text or accessible labels.
- Color must not be the only signal for rank, status, pass/fail, or voted state.
- Form fields must have labels and visible validation messages.

## Frontend repo-health rules

- Do not continue growing one giant `App.tsx`.
- Prefer page-level components under `apps/web/src/pages`.
- Prefer feature-owned components under `apps/web/src/features/<feature>`.
- Shared components must be genuinely shared and small.
- Do not create vague folders such as `misc`, `helpers`, `new`, `v2`, `temp`, or `final`.
- Business logic for contest/reputation must not be hardcoded in frontend if it belongs to backend/domain contracts.

## Premium Visual DoD

A frontend feature is not done unless:

- It matches the Midnight Gold Editorial League direction.
- It feels premium before any interaction.
- It avoids generic SaaS visual language.
- It avoids casino/betting visual language.
- It uses gold as restrained accent, not decoration everywhere.
- Major headings use editorial display typography.
- Product surfaces still show real league mechanics: contest, votes, ranking, creator status.
- No debug banners or broken image icons are visible.
- Mobile layout preserves premium spacing and hierarchy.
