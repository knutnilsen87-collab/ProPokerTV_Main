# Founder Season Runbook

Purpose: run the first real Weekly Poker Clip League test with a small creator cohort.

## Preconditions

- Frontend CI is active.
- Backend compiles and frontend builds.
- One active weekly contest exists.
- Admin can nominate approved public clips.
- Voting rejects duplicate votes.
- Finalization creates winner history and creator reputation.

## Weekly Operating Loop

1. Recruit creators.
2. Confirm creator profiles are complete.
3. Collect clip links or uploads.
4. Approve brand-safe public clips.
5. Nominate clips into the active weekly contest.
6. Promote voting.
7. Monitor comments, reactions, reports, and vote errors.
8. Finalize the contest.
9. Share the winner and updated creator ranking.
10. Record what drove repeat creator and fan activity.

## Admin Smoke Checklist

- Admin signs in.
- Fan signs in.
- Active contest is visible on homepage.
- Nominee clips render with vote buttons.
- Fan can vote once.
- Duplicate vote is denied.
- Admin can finalize contest.
- Finalized contest appears in winner history.
- Creator profile shows win/reputation state.
- Top creators leaderboard includes the winner.

## Metrics To Review

- `creator_signed_up`
- `creator_profile_completed`
- `clip_created`
- `clip_moderated`
- `clip_nominated`
- `contest_vote_cast`
- `contest_finalized`
- `comment_created`
- `reaction_created`
- `report_created`

## Manual Fallbacks

- If signed uploads are not ready, run link-first clips with HTTPS-only URLs.
- If scheduled finalization is not enabled, use admin-triggered finalization.
- If analytics aggregation is not connected, count `PPTV_EVENT` log lines.
- If creator outreach is weak, reduce scope to 5-10 high-fit creators and finish the loop anyway.
