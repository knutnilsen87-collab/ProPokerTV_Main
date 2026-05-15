import type {
  ApiEnvelope,
  AuthResponse,
  Clip,
  Comment,
  Contest,
  CreateContestPayload,
  CreatorLeaderboardRow,
  CreatorProfile,
  CurrentUser,
  LeaderboardRow,
  PokerEvent,
  Profile,
  ReactionSummary,
  Report,
  SocialAuthProvider,
} from "../types";
import {
  previewClips,
  previewComments,
  previewContest,
  previewContestHistory,
  previewCreatorLeaderboard,
  previewCreatorProfile,
  previewEvents,
  previewLeaderboard,
  previewProfile,
  previewReactions,
} from "./previewData";

const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";
const PREVIEW_FALLBACK = import.meta.env.VITE_PREVIEW_FALLBACK !== "false";
const PREVIEW_MODE = import.meta.env.VITE_PREVIEW_MODE === "true" || (typeof window !== "undefined" && window.location.hostname.endsWith(".vercel.app"));

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

async function withPreviewFallback<T>(action: () => Promise<T>, fallback: () => T): Promise<T> {
  if (PREVIEW_MODE) {
    return fallback();
  }
  try {
    return await action();
  } catch (error) {
    if (PREVIEW_FALLBACK) {
      return fallback();
    }
    throw error;
  }
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

export async function socialLogin(provider: SocialAuthProvider, idToken: string): Promise<AuthResponse> {
  return request<AuthResponse>("/api/v1/auth/social", {
    method: "POST",
    body: JSON.stringify({ provider, idToken }),
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
  return withPreviewFallback(
    () => request<Clip[]>("/api/v1/clips"),
    () => previewClips,
  );
}

export async function getClip(slug: string): Promise<Clip> {
  return withPreviewFallback(
    () => request<Clip>(`/api/v1/clips/${slug}`),
    () => previewClips.find((clip) => clip.slug === slug) ?? previewClips[0],
  );
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
  return withPreviewFallback(
    () => request<Comment[]>(`/api/v1/comments/clip/${clipSlug}`),
    () => previewComments,
  );
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
  return withPreviewFallback(
    () => request<ReactionSummary[]>(`/api/v1/reactions/clip/${clipSlug}`, {}, tokens ?? undefined),
    () => previewReactions,
  );
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
  return withPreviewFallback(
    () => request<Contest>("/api/v1/contests/open"),
    () => previewContest,
  );
}

export async function createContest(tokens: Tokens, payload: CreateContestPayload): Promise<Contest> {
  return request<Contest>(
    "/api/v1/contests",
    {
      method: "POST",
      body: JSON.stringify(payload),
    },
    tokens,
  );
}

export async function openContest(tokens: Tokens, contestId: number): Promise<Contest> {
  return request<Contest>(
    `/api/v1/contests/${contestId}/open`,
    {
      method: "POST",
    },
    tokens,
  );
}

export async function nominateClip(tokens: Tokens, contestId: number, clipId: number): Promise<Contest> {
  return request<Contest>(
    `/api/v1/contests/${contestId}/entries`,
    {
      method: "POST",
      body: JSON.stringify({ clipId }),
    },
    tokens,
  );
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

export async function getContestHistory(): Promise<Contest[]> {
  return withPreviewFallback(
    () => request<Contest[]>("/api/v1/contests/history"),
    () => previewContestHistory,
  );
}

export async function finalizeContest(tokens: Tokens, contestId: number): Promise<Contest> {
  return request<Contest>(
    `/api/v1/contests/${contestId}/finalize`,
    {
      method: "POST",
    },
    tokens,
  );
}

export async function getModerationQueue(tokens: Tokens): Promise<Report[]> {
  return request<Report[]>("/api/v1/moderation/queue", {}, tokens);
}

export async function moderateClip(tokens: Tokens, clipId: number, decision: string, reason = ""): Promise<string> {
  return request<string>(
    `/api/v1/moderation/clips/${clipId}/decision`,
    {
      method: "POST",
      body: JSON.stringify({ decision, reason }),
    },
    tokens,
  );
}

export async function reportTarget(
  tokens: Tokens,
  payload: { targetType: "CLIP" | "COMMENT"; targetId: number; reason: string; note?: string },
): Promise<Report> {
  return request<Report>(
    "/api/v1/moderation/reports",
    {
      method: "POST",
      body: JSON.stringify(payload),
    },
    tokens,
  );
}

export async function getLeaderboard(): Promise<LeaderboardRow[]> {
  return withPreviewFallback(
    () => request<LeaderboardRow[]>("/api/v1/leaderboards/top-clips"),
    () => previewLeaderboard,
  );
}

export async function getCreatorLeaderboard(): Promise<CreatorLeaderboardRow[]> {
  return withPreviewFallback(
    () => request<CreatorLeaderboardRow[]>("/api/v1/leaderboards/top-creators"),
    () => previewCreatorLeaderboard,
  );
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
    country?: string;
    city?: string;
    languages?: string[];
    profileType?: string;
    pokerRoles?: string[];
    preferredGames?: string[];
    preferredFormats?: string[];
    contentFocus?: string[];
    preferredRegion?: string;
    interestedEventTypes?: string[];
    onlineEventsAllowed?: boolean;
    maxTravelDistanceKm?: number | null;
    eventAlertsOptIn?: boolean;
    partnerOffersOptIn?: boolean;
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
  return withPreviewFallback(
    () => request<Profile>(`/api/v1/profiles/${username}`),
    () => ({ ...previewProfile, username }),
  );
}

export async function getCreatorProfile(slug: string): Promise<CreatorProfile> {
  return withPreviewFallback(
    () => request<CreatorProfile>(`/api/v1/creators/${slug}`),
    () => ({ ...previewCreatorProfile, creatorSlug: slug }),
  );
}

export async function getCalendarEvents(): Promise<PokerEvent[]> {
  return withPreviewFallback(
    () => request<PokerEvent[]>("/api/v1/calendar/events"),
    () => previewEvents,
  );
}

export async function trackCalendarClick(eventId: number, targetUrlType: "official" | "affiliate" | "partner", referrerPage: string): Promise<string> {
  return request<string>(`/api/v1/calendar/events/${eventId}/outbound-click`, {
    method: "POST",
    body: JSON.stringify({
      sessionId: window.sessionStorage.getItem("pptv-session-id") ?? "anonymous-preview",
      targetUrlType,
      referrerPage,
    }),
  });
}

export type CalendarEventPayload = {
  title: string;
  organizerName: string;
  organizerType: string;
  eventType: string;
  startsAt: string;
  endsAt?: string | null;
  timezone: string;
  locationType: string;
  country?: string;
  city?: string;
  venueName?: string;
  onlineUrl?: string;
  registrationUrl?: string;
  affiliateUrl?: string;
  affiliateDisclosureRequired?: boolean;
  imageUrl?: string;
  description?: string;
  tags?: string[];
  status?: string;
  featured?: boolean;
  sponsored?: boolean;
};

export async function createCalendarEvent(tokens: Tokens, payload: CalendarEventPayload): Promise<PokerEvent> {
  return request<PokerEvent>(
    "/api/v1/calendar/admin/events",
    {
      method: "POST",
      body: JSON.stringify(payload),
    },
    tokens,
  );
}

export async function publishCalendarEvent(tokens: Tokens, eventId: number): Promise<PokerEvent> {
  return request<PokerEvent>(`/api/v1/calendar/admin/events/${eventId}/publish`, { method: "POST" }, tokens);
}

export async function removeCalendarEvent(tokens: Tokens, eventId: number): Promise<PokerEvent> {
  return request<PokerEvent>(`/api/v1/calendar/admin/events/${eventId}/remove`, { method: "POST" }, tokens);
}
