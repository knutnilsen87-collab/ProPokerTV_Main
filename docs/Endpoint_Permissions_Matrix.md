# Endpoint Permissions Matrix

| Endpoint | Public | Authenticated | Creator | Moderator/Admin |
|---|---:|---:|---:|---:|
| POST /api/v1/auth/signup | ✅ |  |  |  |
| POST /api/v1/auth/login | ✅ |  |  |  |
| GET /api/v1/me |  | ✅ | ✅ | ✅ |
| GET /api/v1/profiles/{username} | ✅ | ✅ | ✅ | ✅ |
| PUT /api/v1/profiles/me |  | ✅ | ✅ | ✅ |
| GET /api/v1/creators/{slug} | ✅ | ✅ | ✅ | ✅ |
| PUT /api/v1/creators/me |  |  | ✅ | ✅ |
| GET /api/v1/clips | ✅ | ✅ | ✅ | ✅ |
| POST /api/v1/clips |  | ✅ | ✅ | ✅ |
| POST /api/v1/comments/clip/{clipSlug} |  | ✅ | ✅ | ✅ |
| POST /api/v1/reactions/clip/{clipSlug} |  | ✅ | ✅ | ✅ |
| POST /api/v1/moderation/reports |  | ✅ | ✅ | ✅ |
| GET /api/v1/moderation/queue |  |  |  | ✅ |
| POST /api/v1/contests |  |  |  | ✅ |
| POST /api/v1/contests/{contestId}/vote |  | ✅ | ✅ | ✅ |
