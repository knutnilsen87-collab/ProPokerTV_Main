export type ApiEnvelope<T> = {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    fieldViolations?: Array<{ field: string; message: string }>;
  };
};

export type Clip = {
  id: number;
  ownerUserId: number;
  slug: string;
  title: string;
  description: string | null;
  visibility: string;
  moderationStatus: string;
  categorySlug: string | null;
  tagsCsv: string | null;
  thumbnailUrl: string | null;
  playbackUrl: string | null;
  durationSeconds: number | null;
  viewCount: number;
};

export type Comment = {
  id: number;
  clipId: number;
  authorUserId: number;
  parentCommentId: number | null;
  body: string;
  createdAt: string;
};

export type ReactionSummary = {
  reactionType: string;
  count: number;
  reactedByCurrentUser: boolean;
};

export type ContestEntry = {
  entryId: number;
  clipId: number;
  votes: number;
};

export type Contest = {
  id: number;
  title: string;
  status: string;
  startsAt: string;
  endsAt: string;
  entries: ContestEntry[];
};

export type LeaderboardRow = {
  subjectId: number;
  label: string;
  score: number;
};

export type Profile = {
  userId: number;
  username: string;
  displayName: string;
  bio: string | null;
  avatarUrl: string | null;
  bannerUrl: string | null;
};

export type CreatorProfile = {
  userId: number;
  creatorSlug: string;
  headline: string | null;
  verified: boolean;
  socialLinksJson: string | null;
};

export type CurrentUser = {
  userId: number;
  email: string;
  role: string;
};

export type AuthTokens = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInSeconds: number;
};

export type AuthResponse = {
  userId: number;
  email: string;
  role: string;
  emailVerified: boolean;
  tokens: AuthTokens;
};
