# Storage Abstraction Plan

## Goal
Keep media storage decoupled from clip metadata and public playback resolution.

## Interfaces
- `createUploadUrl(objectKey, contentType)`
- `publicUrl(objectKey)`

## Providers
- local dev
- S3-compatible production

## Future
- signed upload endpoints
- media_asset table
- transcoding state machine
- thumbnail variants
