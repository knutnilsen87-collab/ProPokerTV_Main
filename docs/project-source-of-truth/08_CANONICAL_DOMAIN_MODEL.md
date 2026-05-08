# Canonical Domain Model v1

This file defines the canonical product/domain model. Do not create private incompatible variants.

## User

Represents an account.

Required concepts:

- id
- email
- display name
- role(s)
- created at
- status

Roles:

- fan
- creator
- moderator
- admin

## UserProfile

General public profile.

Required concepts:

- user id
- display name
- avatar
- bio
- country/location optional
- social links optional

## CreatorProfile

Poker creator identity.

Required concepts:

- creator id
- user id
- creator handle
- bio
- avatar/banner
- primary poker category
- links
- status stats
- public/private visibility

Creator reputation fields may be derived:

- total clips
- total nominations
- total contest votes
- wins
- badges
- ranking position

## Clip

Poker media item.

Required concepts:

- clip id
- creator/user id
- title
- description
- media URL or upload ref
- thumbnail optional
- category/tag(s)
- status: draft/pending/approved/rejected/removed
- visibility
- created at
- updated at

Rules:

- only approved clips can be nominated for contests,
- rejected/removed clips cannot be publicly promoted,
- unsafe content can be reported/moderated.

## Comment

User comment on clip.

Required concepts:

- comment id
- clip id
- user id
- body
- status
- created at

## Reaction

User reaction on clip.

Required concepts:

- reaction id or composite key
- clip id
- user id
- reaction type
- created at

## Report

User report against clip/comment/user content.

Required concepts:

- report id
- target type
- target id
- reporter user id
- reason
- status
- created at
- reviewed by optional
- reviewed at optional

## ModerationDecision

Decision taken by moderator/admin.

Required concepts:

- decision id
- target type
- target id
- decision: approve/reject/remove/restore/escalate
- reason
- actor id
- created at

## WeeklyContest

Main competition object.

Required concepts:

- contest id
- title
- description
- category or categories
- status: draft/open/voting/finalized/cancelled
- starts at
- ends at
- finalized at optional
- created by admin id

Rules:

- contests must have a defined voting window,
- finalized contests are locked,
- cancelled contests do not create winner history.

## ContestNomination

Approved clip nominated into a contest.

Required concepts:

- nomination id
- contest id
- clip id
- category
- nominated by admin id
- status
- created at

Rules:

- nominated clip must be approved,
- same clip/category duplication should be prevented according to product rule.

## ContestVote

Fan vote.

Required concepts:

- vote id
- contest id
- nomination id or clip id
- voter user id
- category if category voting is enabled
- created at

Rules:

- user can vote once per contest/category according to configured product rule,
- vote after finalization is denied,
- duplicate vote is denied.

## ContestResult / WinnerHistory

Durable result after finalization.

Required concepts:

- result id
- contest id
- winning clip id
- winning creator id
- category
- vote count
- finalized at
- finalized by or job id

Rules:

- result must be reproducible from votes,
- manual correction must be admin-governed and auditable,
- result creates reputation signal.

## LeaderboardEntry

Read model for rankings.

Possible scopes:

- weekly clips,
- monthly creators,
- all-time creators,
- category rankings,
- club/event rankings later.

Required concepts:

- scope
- subject type: clip/creator
- subject id
- rank
- score
- period start/end optional
- generated at

## Badge

Visible reputation marker.

Initial badges:

- Clip of the Week Winner
- Best Bluff Winner
- Sickest River Winner
- Funniest Poker Moment Winner
- Best Hand Breakdown Winner
- Rising Creator
- Top 10 Creator

Rules:

- badges should be derived from verified events where possible,
- manual badges require admin authority.
