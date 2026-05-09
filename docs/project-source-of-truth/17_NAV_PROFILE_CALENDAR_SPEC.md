# ProPokerTV — Navigation, Profile, Calendar & Affiliate Spec

**Purpose:** Give Codex/developers a clear, repo-ready source of truth for the next product iteration.

**Status:** Binding direction for frontend/product work. Backend schema changes must be done in later, explicit PRs unless already supported.

**Primary goal:** ProPokerTV must evolve from a clip app into a premium non-gambling poker media, league, identity and event-discovery platform.

## 1. Product decision

The current profile page is too thin for the product ambition. It behaves like basic account settings, but ProPokerTV needs profile pages to become a **poker identity and reputation surface**.

The main navigation also needs a first-class **Calendar** surface for upcoming poker tournaments, live events, club nights and partner registrations.

### Locked product direction

ProPokerTV is:

> A non-gambling poker media and reputation platform where creators, players, fans, clubs, events and brands turn poker moments into status, rankings, discovery and commercial opportunity.

The MVP wedge remains:

> The Weekly Poker Clip League.

Calendar is added as a commercial/discovery surface, not as a gambling product.

## 2. Main navigation

### Approved MVP nav

Use this order:

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

### Logged-out right side

```text
Sign in
Join the League
```

### Logged-in right side

Do **not** show a large raw email pill as the premium default. Use a compact account/avatar menu when feasible.

Preferred:

```text
[Avatar] Ace Creator
```

Dropdown items:

```text
Profile
My clips
Calendar preferences
Settings
Sign out
```

If a dropdown is too much for the current PR, keep sign out accessible but make it visually quieter and premium.

## 3. What each nav item owns

| Nav item | Purpose | Must show |
|---|---|---|
| Home | Premium landing and active weekly league preview | hero, active contest, current leader, CTA, rankings/calendar preview |
| League | Core weekly contest and voting experience | active contest, categories, nominees, vote CTA, rules, time left |
| Clips | Clip discovery | newest, trending, filters, clip cards |
| Rankings | Reputation/status | top clips, top creators, winners, category leaders |
| Calendar | Tournament/event discovery and partner registrations | upcoming events, filters, registration CTAs, affiliate disclosure |
| Creators | Creator/player discovery | creator cards, badges, wins, region, categories |
| Upload | Creator submission | submit clip, category, contest nomination, moderation status |
| Profile | Identity/reputation settings | public profile, poker identity, creator tools, league stats, calendar preferences, account |

## 4. Calendar product spec

Calendar should be introduced as a first-class nav item and initially can be a premium placeholder/landing page.

### Calendar positioning

```text
Discover upcoming poker tournaments, club nights, online series, creator events and partner challenges.
```

### Allowed event types

```text
Live tournament
Online tournament
Club night
Creator event
Watch party
Training/masterclass
Sponsored challenge
Partner contest
```

### Event card fields

Each event card should eventually contain:

```text
Event name
Organizer
Organizer type
Date/time
Timezone
Location or online status
Country/city/venue
Event type
Tags
Registration CTA
Official event URL
Affiliate/partner URL when applicable
Sponsor/featured label when applicable
Disclosure text when applicable
```

### Calendar MVP page

For the first frontend PR, backend is not required. The page may show curated/static placeholder cards or empty/coming-soon state, but it must feel premium and intentional.

Required on first Calendar page:

- page title: `Poker Calendar`
- short explanation of upcoming tournaments/events
- premium empty/placeholder state
- affiliate/partner disclosure
- CTA: `Submit an event` or `Partner with ProPokerTV`
- no gambling/deposit language

### Affiliate disclosure

Use this disclosure where partner links may appear:

```text
Some event links may be partner or affiliate links. ProPokerTV is a non-gambling media platform. We do not operate gambling services, accept wagers, or process tournament entries directly.
```

### CTA language

Allowed:

```text
View event
Register with partner
Open official event page
Learn more
```

Avoid:

```text
Bet now
Deposit now
Play for cash
Gamble now
```

## 5. Profile product spec

Profile must become a **poker identity and reputation management area**, not just account settings.

### Profile sections/tabs

```text
Public Profile
Poker Identity
Creator Tools
League Stats
Calendar Preferences
Account
```

### Public Profile

Fields:

```text
Display name
Username
Avatar
Banner image
Bio
Country/region
City optional
Languages
Profile type
```

Profile type values:

```text
Fan
Player
Creator
Club/Event
Brand/Partner
```

### Poker Identity

Fields:

```text
Poker roles
Preferred games
Preferred formats
Content focus
Skill/content level optional
Region
```

Poker role values:

```text
Recreational player
Live player
Online player
Creator
Coach
Commentator
Club representative
Tournament organizer
```

Preferred games:

```text
Texas Hold’em
Omaha
Short Deck
Mixed games
```

Preferred formats:

```text
Cash game
Tournament
Sit & Go
Home games
Live events
Online events
```

Content focus:

```text
Bluff spots
Hand breakdowns
Tournament runs
Beginner content
High-stakes clips
Funny moments
River moments
Table drama
```

### Creator Tools

Fields:

```text
Creator tagline
Featured clip
Pinned clips
Content categories
Posting frequency
Collaboration open
Sponsor contact enabled
Business email optional/private
Social links
```

Social links:

```text
YouTube
TikTok
Instagram
Twitch
X/Twitter
Website
Discord
Coaching/profile link
```

### League Stats

Read-only, derived from contests/leaderboards:

```text
Total clips
Total votes received
Weekly wins
Nominations
Best category
Current rank
All-time rank
Badges
Winner history
```

Do not let users manually edit these.

### Calendar Preferences

Fields:

```text
Interested event types
Preferred region
Online events allowed
Max travel distance
Event alerts opt-in
Partner offers opt-in
```

### Account

Fields/features:

```text
Email
Password
Security settings
Sessions later
Privacy
Notification preferences
Delete account
```

## 6. Profile UI requirements

The profile settings screen should use a premium tabbed layout:

```text
Profile Settings

[Public Profile] [Poker Identity] [Creator Tools] [League Stats] [Calendar Preferences] [Account]
```

### Layout

- Left/main: editable form or section content.
- Right/side: live premium creator/player preview where useful.
- Existing supported fields must remain functional.
- Planned unsupported fields may be displayed as disabled/planned, but must not fake persistence.

### Existing backend-supported fields

Keep functional:

```text
username
display name
bio
avatar URL
banner URL
```

### Product copy

Use copy that makes the purpose clear:

```text
Build your poker identity. Your profile powers creator discovery, league rankings, calendar recommendations and partner opportunities.
```

### Profile preview

The preview should look like a premium creator card and eventually show:

```text
avatar
banner
display name
username
profile type
bio
region
badges
weekly wins
total votes
featured clip
social links
```

## 7. Visual direction binding

Use the approved visual direction:

```text
Midnight Gold Editorial League
```

The UI should feel like a premium poker media league:

- deep midnight/black atmosphere
- restrained gold accents
- editorial serif display typography for major headings
- clean sans-serif body/nav
- cinematic surfaces
- calm premium spacing
- league/status mechanics visible
- not generic SaaS
- not cheap casino
- not betting-site UI

### Avoid

- bright casino red/green as primary UI
- overusing gold fills
- debug banners in user-facing UI
- raw unstyled email pills as primary nav identity
- broken image icons
- large empty cards without purpose
- static VIP-club landing page with no contest mechanics

## 8. Legal/commercial guardrails for Calendar

Calendar is allowed because it can support event discovery and partner registrations, but ProPokerTV must remain non-gambling.

### Rules

- ProPokerTV must not present itself as a gambling operator.
- ProPokerTV must not accept wagers.
- ProPokerTV must not process deposits.
- ProPokerTV must not hide affiliate relationships.
- Sponsored/partner placements must be distinguishable from organic listings.
- Age/jurisdiction disclaimers must be shown when relevant.
- Admin must be able to remove unsafe/expired events.
- Event CTAs should point to official/partner pages with clear disclosure.

## 9. Backend target models

Do not implement all of this in the first frontend PR. These are target contracts for planned backend work.

### UserProfile additions

```text
userId
displayName
username
bio
avatarUrl
bannerUrl
country
city
languages[]
profileType
```

### PokerIdentity

```text
userId
roles[]
games[]
formats[]
contentFocus[]
skillLevel optional
region
```

### CreatorProfile additions

```text
userId
tagline
featuredClipId
pinnedClipIds[]
socialLinks
sponsorContactEnabled
collaborationOpen
businessEmail optional/private
```

### UserEventPreferences

```text
userId
interestedEventTypes[]
preferredRegions[]
onlineEventsAllowed
maxTravelDistanceKm
eventAlertsOptIn
partnerOffersOptIn
```

### Event

```text
eventId
title
organizerName
organizerType
eventType
startsAt
endsAt
timezone
locationType
country
city
venueName
onlineUrl optional
registrationUrl
affiliateUrl optional
affiliateDisclosureRequired
imageUrl
description
tags[]
status
featured
sponsored
```

### EventOutboundClick

```text
clickId
eventId
userId nullable
sessionId
targetUrlType: official | affiliate | partner
clickedAt
referrerPage
```

## 10. PR sequence

### PR 1 — Navigation + Profile UX

Scope:

- update main nav to include Calendar
- keep League first-class
- improve logged-in account area
- redesign profile settings into tabbed premium editor
- keep existing profile save fields working
- no backend-breaking change

Validation:

```bash
cd apps/web
npm run build
```

Manual smoke:

```text
nav links work
profile page opens
existing profile fields save
profile preview renders
desktop layout usable
mobile layout usable
```

### PR 2 — Calendar placeholder page

Scope:

- add `/calendar` route
- premium calendar landing/empty state
- affiliate disclosure
- mock/placeholder event cards if useful
- no backend required yet

Validation:

```bash
cd apps/web
npm run build
```

Manual smoke:

```text
calendar route opens
disclosure visible
no gambling/operator language
mobile layout usable
```

### PR 3 — Profile model expansion

Scope:

- backend migrations
- API DTOs
- frontend save support
- tests

Validation:

```bash
cd backend
mvn test

cd apps/web
npm run build
```

### PR 4 — Calendar backend v0

Scope:

- event model
- list upcoming events
- admin create/publish/remove events
- outbound click tracking endpoint
- affiliate disclosure flags
- tests

### PR 5 — Calendar monetization v0

Scope:

- featured/sponsored events
- partner attribution
- basic event click reporting
- admin safety controls

## 11. Definition of Done additions

### Navigation DoD

A nav change is not done unless:

- the approved nav items are present or explicitly justified
- active state is clear
- desktop and mobile work
- logged-in account controls are accessible
- raw email pills are avoided where a premium account menu is feasible
- no nav item points to a broken route

### Profile DoD

A profile change is not done unless:

- existing supported fields still save correctly
- unsupported planned fields are clearly non-submitting/disabled or are fully implemented
- public profile purpose is clear
- preview feels premium and not like a debug card
- league/reputation stats are read-only if shown
- no user can edit derived rank/vote/win stats manually

### Calendar / Affiliate Event DoD

Calendar or event features are not done unless:

- each event has organizer, date, location/online status and registration CTA
- affiliate/sponsored links are clearly disclosed
- ProPokerTV does not present itself as the gambling operator
- age/jurisdiction disclaimers are shown when relevant
- outbound clicks are eventually tracked through backend, not only frontend
- sponsored placement is distinguishable from organic listings
- admin can disable/remove unsafe or expired events
- event status supports at least draft, published, expired, removed

## 12. Non-goals now

Do not build these before the core league/profile/calendar direction is verified:

```text
full marketplace
native mobile app
livestreaming platform
betting/gambling functionality
payment/deposit handling
complex AI recommendation system
full partner dashboard
global multi-language launch
```

## 13. Codex task prompt

Use this as the next implementation prompt:

```text
Update ProPokerTV navigation and profile direction.

Scope:
- apps/web frontend first
- preserve existing backend contracts unless fields already exist
- no broad backend migration in this PR

Navigation:
- Use main nav: Home, League, Clips, Rankings, Calendar, Creators, Upload, Profile
- Add Calendar nav item as future event/tournament discovery surface
- Keep League as a first-class nav item
- Replace visible raw email pill with a premium account/avatar menu if feasible
- Keep Sign out accessible

Profile:
- Redesign profile settings as a premium multi-section profile editor
- Sections/tabs:
  1. Public Profile
  2. Poker Identity
  3. Creator Tools
  4. League Stats
  5. Calendar Preferences
  6. Account
- If backend does not support all fields yet, render disabled/planned fields clearly or keep local UI sections non-submitting for now
- Keep currently supported fields functional: username/display name/bio/avatar URL/banner URL
- Add product copy explaining that profile builds creator/player reputation
- Profile preview must feel like a premium creator card

Calendar:
- Add placeholder Calendar route/page if not already implemented
- Calendar page should explain upcoming tournaments/events and partner registrations
- Include affiliate disclosure:
  “Some event links may be partner or affiliate links. ProPokerTV is a non-gambling media platform. We do not operate gambling services.”

Validation:
- npm run build passes
- profile save still works for existing supported fields
- nav works on desktop and mobile
- Calendar route does not break app

Repo health:
- use owned profile/settings and calendar components
- do not create helpers/misc/temp files
- do not duplicate API logic
- do not hardcode backend-inconsistent models as if persisted
```

## 14. status_bundle

```json
{
  "facts": [
    "Profile must evolve from basic settings into creator/player identity and reputation management.",
    "Calendar should be first-class navigation for tournament/event discovery and affiliate/partner opportunities.",
    "Calendar must preserve ProPokerTV's non-gambling positioning.",
    "Weekly Poker Clip League remains the primary product wedge."
  ],
  "locked_decisions": [
    "Approved MVP nav: Home, League, Clips, Rankings, Calendar, Creators, Upload, Profile.",
    "Profile sections: Public Profile, Poker Identity, Creator Tools, League Stats, Calendar Preferences, Account.",
    "Calendar links must include affiliate/partner disclosure where relevant.",
    "Do frontend-only nav/profile/calendar placeholder first; backend expansion follows in explicit PRs."
  ],
  "recommended_next_action": "PR 1: Navigation + Profile UX frontend-only, preserving existing backend contracts.",
  "fallback_action": "PR 1a: Add Calendar nav/route and improve account nav first, then profile tabs next."
}
```
