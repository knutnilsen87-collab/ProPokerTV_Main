# Definition of Done

A feature is done only when all required conditions are met.

## Product DoD

- The feature strengthens at least one core loop:
  - creator posts more,
  - fan returns to vote,
  - reputation/status increases,
  - platform becomes safer,
  - partner has reason to pay,
  - ProPokerTV becomes harder to copy.
- Acceptance criteria are satisfied.
- User-facing copy is clear.
- Empty, loading, and error states exist where relevant.
- The feature does not make the product feel like a generic feed.
- The feature does not introduce gambling/betting functionality.

## Engineering DoD

- Code is in the correct owned module.
- Existing backend modular monolith boundaries are respected.
- No duplicate parallel model/service is introduced.
- Backend contracts are documented or reflected in OpenAPI when public.
- Database migration exists if schema changes.
- No hardcoded production secrets.
- No demo-only behavior leaks into production path.
- Role/permission checks exist for protected actions.
- Data ownership is clear.

## Verification DoD

- Backend tests pass.
- Frontend build passes.
- Relevant targeted tests are added or updated.
- Critical invariants are verified.
- Manual smoke path is documented if automation is not yet available.
- Verification result is stated in PR description.

## Repo-health DoD

- No broad utility bucket is introduced.
- No obsolete code is left behind without explicit follow-up.
- File/module ownership remains clear.
- Docs are updated when behavior, setup, API, data model, or workflow changes.
- Reference/archive files are not treated as canonical runtime code without explicit import decision.
- New files have clear responsibility and ownership.

## Security/legal DoD

- The change preserves non-gambling positioning.
- Authenticated actions require auth.
- Admin/moderator actions require correct role.
- User-generated content has moderation/report path.
- Secrets are not committed.
- Demo credentials are clearly local-only.
- Public content assumptions are documented.

## Closure rule

A task is not done if:

- verification is missing,
- ambiguity is unresolved,
- critical tests are skipped without reason,
- repo structure is degraded,
- the feature does not support the product wedge,
- there is known false-green risk without explicit follow-up.

## PR template checklist

```md
## What changed

## Why

## Product loop strengthened
- [ ] Creator loop
- [ ] Fan loop
- [ ] Competition loop
- [ ] Reputation loop
- [ ] Partner loop
- [ ] Safety/moderation

## Verification
- [ ] Backend tests
- [ ] Frontend build
- [ ] Targeted tests added/updated
- [ ] Manual smoke documented

## Repo health
- [ ] Correct module/layer
- [ ] No duplicate model/service
- [ ] Docs updated
- [ ] No dead code left behind

## Known gaps / follow-up
```
