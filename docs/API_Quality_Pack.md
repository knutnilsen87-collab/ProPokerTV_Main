# API Quality Pack

## Envelope
All endpoints return:
- `success`
- `data`
- `error`
- `meta`
- `timestamp`

## Error codes
- VALIDATION_ERROR
- UNAUTHORIZED
- FORBIDDEN
- NOT_FOUND
- CONFLICT
- INTERNAL_ERROR
- INVALID_TOKEN
- ALREADY_VOTED
- OWNERSHIP_REQUIRED

## Pagination standard
Prefer query params:
- `page`
- `size`
- `sort`

## Versioning rule
Expose version in URI until platform stabilizes:
- `/api/v1/...`

## Naming
- collections plural
- actions as sub-resources or POST verbs only when side-effectful
