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
  finalizedAt: string | null;
  winnerEntryId: number | null;
  winnerClipId: number | null;
  winnerCreatorUserId: number | null;
  entries: ContestEntry[];
};

export type CreateContestPayload = {
  title: string;
  startsAt: string;
  endsAt: string;
};

export type Report = {
  id: number;
  targetType: string;
  targetId: number;
  reporterUserId: number;
  reason: string;
  note: string | null;
  status: string;
  createdAt: string;
};

export type LeaderboardRow = {
  subjectId: number;
  label: string;
  score: number;
};

export type CreatorLeaderboardRow = {
  userId: number;
  creatorSlug: string;
  wins: number;
  nominations: number;
  totalContestVotes: number;
  score: number;
};

export type CreatorReputation = {
  wins: number;
  nominations: number;
  totalContestVotes: number;
  rankingPosition: number | null;
  topCategory: string | null;
  badges: string[];
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
  reputation: CreatorReputation;
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
