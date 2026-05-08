# API and Data Contracts

This is a product-level API contract guide. The actual backend contract should be verified against OpenAPI/controllers before implementation.

## Contract rules

- Public API changes must be reflected in OpenAPI or docs.
- Frontend must use backend-derived domain state.
- Do not create frontend-only reputation logic.
- Do not create duplicate DTOs with incompatible field names.
- All protected actions require auth.
- Admin/moderator actions require role checks.
- Error responses should be predictable enough for frontend handling.

## Core frontend data needs

### Active contest

Needed by homepage.

Expected data:

- contest id
- title
- description
- status
- starts at
- ends at
- categories
- nominations
- current viewer vote state if authenticated
- leaderboard preview

### Contest nomination

Needed by contest cards.

Expected data:

- nomination id
- contest id
- clip id
- clip title
- clip media URL/thumbnail
- creator id
- creator display name/handle
- category
- vote count if public
- viewer has voted

### Vote result

Needed after fan votes.

Expected data:

- vote accepted/denied
- reason if denied
- updated vote count or summary
- viewer vote state

### Creator profile

Needed by public creator page.

Expected data:

- creator id
- handle/display name
- bio/avatar/banner
- clips
- wins
- nominations
- total votes
- badges
- ranking position

### Leaderboard

Needed by leaderboard page and homepage preview.

Expected data:

- scope
- period
- entries
- generated at

### Admin contest management

Needed by admin/moderator UI.

Expected actions:

- create contest
- open/close contest
- nominate approved clip
- remove nomination
- finalize contest
- view result/winner history

## Critical invariants

### Vote-once invariant

A user must not be able to vote more than allowed by the contest rule.

Minimum test:

- same authenticated user votes twice in same contest/category,
- second vote is denied.

### Approved-clip nomination invariant

Rejected, removed, pending, or unsafe clips must not be nominated.

Minimum test:

- attempt to nominate non-approved clip,
- request is denied.

### Finalized contest lock invariant

Finalized contests must not accept new votes or silent result mutations.

Minimum test:

- finalize contest,
- attempt vote,
- vote denied.

### Reputation derivation invariant

Winner history must update creator reputation/leaderboard consistently.

Minimum test:

- finalize contest,
- winning creator profile/stat endpoint reflects win.

### Permission invariant

Admin/moderator actions require correct role.

Minimum test:

- fan attempts admin contest action,
- request denied.

## Database migration rules

- Any schema change must use Flyway.
- Migration must run on empty database.
- Migration must be backward-aware during active development.
- Seed/demo data must be clearly marked as local/demo.
- Production secrets or credentials must not appear in migrations.

## Error handling expectations

Recommended common error shape:

```json
{
  "error": "string_code",
  "message": "Human readable message",
  "details": {},
  "timestamp": "ISO-8601"
}
```

Frontend should handle:

- 400 validation error,
- 401 unauthenticated,
- 403 forbidden,
- 404 not found,
- 409 conflict/duplicate vote,
- 422 business rule violation,
- 500 unexpected server error.
