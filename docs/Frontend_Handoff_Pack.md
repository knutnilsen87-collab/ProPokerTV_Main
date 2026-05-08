# Frontend Handoff Pack

## Endpoint to screen map
- Landing / feed -> `GET /api/v1/clips`
- Clip detail -> `GET /api/v1/clips/{slug}`
- Clip comments -> `GET /api/v1/comments/clip/{clipSlug}`
- Clip reactions -> `GET /api/v1/reactions/clip/{clipSlug}`
- Signup -> `POST /api/v1/auth/signup`
- Login -> `POST /api/v1/auth/login`
- Current user bootstrap -> `GET /api/v1/me`
- My profile -> `GET/PUT /api/v1/profiles/me`
- Public profile -> `GET /api/v1/profiles/{username}`
- Creator page -> `GET /api/v1/creators/{slug}`
- Play of the Week -> `GET /api/v1/contests/open`
- Leaderboard -> `GET /api/v1/leaderboards/top-clips`

## State handling hints
- Boot app by reading access token from secure storage, then call `/api/v1/me`
- If `/api/v1/me` fails with 401, attempt refresh, else logout
- Use optimistic UI only for reactions, not for moderation or contest vote
- Treat clip visibility + moderation as server source of truth

## Loading / empty / error
- Feed empty: show “No approved clips yet”
- Contest empty: show “No active contest”
- Profile empty: redirect creator to onboarding
- Comment post error: inline error under form
