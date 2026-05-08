import type {
  ApiEnvelope,
  AuthResponse,
  Clip,
  Comment,
  Contest,
  CreatorProfile,
  CurrentUser,
  LeaderboardRow,
  Profile,
  ReactionSummary,
} from "../types";

const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";

export const DEMO_ACCOUNTS = [
  { label: "Admin demo", email: "admin@propokertv.test", password: "password" },
  { label: "Creator demo", email: "creator@propokertv.test", password: "password" },
  { label: "Fan demo", email: "fan@propokertv.test", password: "password" },
];

type Tokens = { accessToken: string; refreshToken: string };

export class ApiError extends Error {
  code?: string;
  status: number;
  fieldViolations?: Array<{ field: string; message: string }>;

  constructor(message: string, status: number, code?: string, fieldViolations?: Array<{ field: string; message: string }>) {
    super(message);
    this.status = status;
    this.code = code;
    this.fieldViolations = fieldViolations;
  }
}

async function request<T>(
  path: string,
  init: RequestInit = {},
  tokens?: Tokens | null,
): Promise<T> {
  const headers = new Headers(init.headers ?? {});
  if (!headers.has("Content-Type") && init.body) {
    headers.set("Content-Type", "application/json");
  }
  if (tokens?.accessToken) {
    headers.set("Authorization", `Bearer ${tokens.accessToken}`);
  }

  const response = await fetch(`${API_BASE}${path}`, {
    ...init,
    headers,
  });

  const text = await response.text();
  const envelope = text ? (JSON.parse(text) as ApiEnvelope<T>) : undefined;

  if (!response.ok || (envelope && envelope.success === false)) {
    throw new ApiError(
      envelope?.error?.message ?? `Request failed with status ${response.status}`,
      response.status,
      envelope?.error?.code,
      envelope?.error?.fieldViolations,
    );
  }

  return envelope?.data as T;
}

export async function withRefresh<T>(
  action: (tokens: Tokens) => Promise<T>,
  tokens: Tokens | null,
  onTokens: (tokens: Tokens | null) => void,
): Promise<T> {
  if (!tokens) {
    throw new ApiError("You must be signed in.", 401, "UNAUTHORIZED");
  }

  try {
    return await action(tokens);
  } catch (error) {
    if (!(error instanceof ApiError) || error.status !== 401) {
      throw error;
    }
  }

  const refreshed = await refresh(tokens.refreshToken).catch(() => null);
  if (!refreshed) {
    onTokens(null);
    throw new ApiError("Session expired. Please sign in again.", 401, "UNAUTHORIZED");
  }

  const nextTokens = {
    accessToken: refreshed.tokens.accessToken,
    refreshToken: refreshed.tokens.refreshToken,
  };
  onTokens(nextTokens);
  return action(nextTokens);
}

export function getApiBase(): string {
  return API_BASE;
}

export async function signup(email: string, password: string): Promise<AuthResponse> {
  return request<AuthResponse>("/api/v1/auth/signup", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

export async function login(email: string, password: string): Promise<AuthResponse> {
  return request<AuthResponse>("/api/v1/auth/login", {
    method: "POST",
    body: JSON.stringify({ email, password }),
  });
}

export async function refresh(refreshToken: string): Promise<AuthResponse> {
  return request<AuthResponse>("/api/v1/auth/refresh", {
    method: "POST",
    body: JSON.stringify({ refreshToken }),
  });
}

export async function forgotPassword(email: string): Promise<string> {
  return request<string>("/api/v1/auth/forgot-password", {
    method: "POST",
    body: JSON.stringify({ email }),
  });
}

export async function resetPassword(token: string, newPassword: string): Promise<string> {
  return request<string>("/api/v1/auth/reset-password", {
    method: "POST",
    body: JSON.stringify({ token, newPassword }),
  });
}

export async function verifyEmail(token: string): Promise<string> {
  return request<string>("/api/v1/auth/verify-email", {
    method: "POST",
    body: JSON.stringify({ token }),
  });
}

export async function getCurrentUser(tokens: Tokens): Promise<CurrentUser> {
  return request<CurrentUser>("/api/v1/me", {}, tokens);
}

export async function getClips(): Promise<Clip[]> {
  return request<Clip[]>("/api/v1/clips");
}

export async function getClip(slug: string): Promise<Clip> {
  return request<Clip>(`/api/v1/clips/${slug}`);
}

export async function createClip(
  tokens: Tokens,
  payload: {
    slug: string;
    title: string;
    description: string;
    visibility: string;
    categorySlug: string;
    tagsCsv: string;
    thumbnailUrl: string;
    playbackUrl: string;
    durationSeconds: number | null;
  },
): Promise<Clip> {
  return request<Clip>(
    "/api/v1/clips",
    {
      method: "POST",
      body: JSON.stringify(payload),
    },
    tokens,
  );
}

export async function getComments(clipSlug: string): Promise<Comment[]> {
  return request<Comment[]>(`/api/v1/comments/clip/${clipSlug}`);
}

export async function postComment(tokens: Tokens, clipSlug: string, body: string): Promise<Comment> {
  return request<Comment>(
    `/api/v1/comments/clip/${clipSlug}`,
    {
      method: "POST",
      body: JSON.stringify({ body }),
    },
    tokens,
  );
}

export async function getReactions(clipSlug: string, tokens?: Tokens | null): Promise<ReactionSummary[]> {
  return request<ReactionSummary[]>(`/api/v1/reactions/clip/${clipSlug}`, {}, tokens ?? undefined);
}

export async function react(tokens: Tokens, clipSlug: string, reactionType: string): Promise<string> {
  return request<string>(
    `/api/v1/reactions/clip/${clipSlug}`,
    {
      method: "POST",
      body: JSON.stringify({ reactionType }),
    },
    tokens,
  );
}

export async function unreact(tokens: Tokens, clipSlug: string, reactionType: string): Promise<string> {
  return request<string>(
    `/api/v1/reactions/clip/${clipSlug}/${reactionType}`,
    {
      method: "DELETE",
    },
    tokens,
  );
}

export async function getContest(): Promise<Contest> {
  return request<Contest>("/api/v1/contests/open");
}

export async function vote(tokens: Tokens, contestId: number, entryId: number): Promise<Contest> {
  return request<Contest>(
    `/api/v1/contests/${contestId}/vote`,
    {
      method: "POST",
      body: JSON.stringify({ entryId }),
    },
    tokens,
  );
}

export async function getLeaderboard(): Promise<LeaderboardRow[]> {
  return request<LeaderboardRow[]>("/api/v1/leaderboards/top-clips");
}

export async function getMyProfile(tokens: Tokens): Promise<Profile> {
  return request<Profile>("/api/v1/profiles/me", {}, tokens);
}

export async function updateMyProfile(
  tokens: Tokens,
  payload: {
    username: string;
    displayName: string;
    bio: string;
    avatarUrl: string;
    bannerUrl: string;
  },
): Promise<Profile> {
  return request<Profile>(
    "/api/v1/profiles/me",
    {
      method: "PUT",
      body: JSON.stringify(payload),
    },
    tokens,
  );
}

export async function getPublicProfile(username: string): Promise<Profile> {
  return request<Profile>(`/api/v1/profiles/${username}`);
}

export async function getCreatorProfile(slug: string): Promise<CreatorProfile> {
  return request<CreatorProfile>(`/api/v1/creators/${slug}`);
}
