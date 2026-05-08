# End Product Acceptance Criteria

The ProPokerTV MVP is done when the Weekly Poker Clip League works end-to-end.

## Core user journeys

### Creator journey

- Creator can sign up/login.
- Creator can create or edit user profile.
- Creator can create or edit creator profile.
- Creator can upload or link a poker clip.
- Creator can see own clips.
- Creator can see votes, nominations, wins, badges, and ranking.
- Creator can understand how to improve status.

### Fan journey

- Fan can view active weekly contest on homepage.
- Fan can watch nominated clips.
- Fan can vote once per contest/category.
- Fan can comment.
- Fan can react.
- Fan can report unsafe or low-quality content.
- Fan can view winners and leaderboards.

### Admin/moderator journey

- Admin/moderator can review reports.
- Admin/moderator can approve/reject clips.
- Admin can create weekly contests.
- Admin can nominate approved clips.
- Admin can finalize contests.
- Admin can verify winner history.
- Admin/moderator actions require correct permissions.

### Competition journey

- Active weekly contest is visible.
- Only approved clips can be nominated.
- User can vote once per contest/category according to product rule.
- Duplicate votes are denied.
- Finalized contests are locked.
- Winner is determined by vote count.
- Winner history is persisted.
- Leaderboard updates after contest finalization.

### Reputation journey

- Completed contests create winner history.
- Creator profile shows wins, nominations, total votes, and badges.
- Leaderboards show weekly/monthly/all-time ranking where supported.
- Reputation is derived from backend/domain data, not hardcoded frontend-only state.
- Finalized contest data cannot be silently mutated.

## Technical gates

- Backend CI passes.
- Frontend CI passes.
- Local fullstack setup is documented and reproducible.
- Database migrations run from empty database.
- Contest lifecycle has integration tests.
- Vote-once invariant is tested.
- Finalized-contest lock is tested.
- Auth-protected actions require correct permissions.
- Moderation/reporting flow has at least basic tests.
- API contracts are discoverable through docs or OpenAPI.
- Known unfinished blueprint items are either completed or explicitly deferred with reason.

## Business validation gates

- 20 creators onboarded.
- 50 clips posted in 30 days.
- 300 votes recorded.
- 30% of creators post more than once.
- 100 comments/reactions recorded.
- At least 1 weekly contest completed end-to-end.
- At least 3 clubs/brands express interest in a sponsored contest pilot.
- Week 2 activity is equal to or higher than Week 1, or cause is documented.

## Not done if

- The product is just a feed.
- Weekly contest is hidden as a side feature.
- Winners do not create persistent reputation.
- Creator profile does not show status.
- Voting can be abused trivially.
- The app cannot be run by a new developer from docs.
- Moderation cannot protect public content.
- No metrics exist to prove or disprove the creator/fan loop.
