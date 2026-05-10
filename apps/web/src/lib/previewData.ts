import type { Clip, Comment, Contest, CreatorLeaderboardRow, CreatorProfile, LeaderboardRow, PokerEvent, Profile, ReactionSummary } from "../types";

const now = Date.now();
const startsAt = new Date(now - 36e5).toISOString();
const endsAt = new Date(now + 6 * 24 * 36e5).toISOString();

export const previewClips: Clip[] = [
  {
    id: 101,
    ownerUserId: 201,
    slug: "hero-call-final-table",
    title: "Hero Call at the Final Table",
    description: "A creator breaks down a river decision that swings the weekly league lead.",
    visibility: "PUBLIC",
    moderationStatus: "APPROVED",
    categorySlug: "final-table",
    tagsCsv: "hero-call,river,final-table",
    thumbnailUrl: "/images/thumbnails/wsop-main.jpeg",
    playbackUrl: null,
    durationSeconds: 74,
    viewCount: 18420,
  },
  {
    id: 102,
    ownerUserId: 202,
    slug: "three-barrel-bluff",
    title: "Three-Barrel Bluff Under Pressure",
    description: "A tense bluff line from cutoff to river, nominated for execution and timing.",
    visibility: "PUBLIC",
    moderationStatus: "APPROVED",
    categorySlug: "bluff",
    tagsCsv: "bluff,pressure,cash-game",
    thumbnailUrl: "/images/thumbnails/amazing-bluff.jpeg",
    playbackUrl: null,
    durationSeconds: 58,
    viewCount: 14270,
  },
  {
    id: 103,
    ownerUserId: 203,
    slug: "icm-fold-of-the-week",
    title: "ICM Fold of the Week",
    description: "The disciplined laydown that kept a tournament life alive.",
    visibility: "PUBLIC",
    moderationStatus: "APPROVED",
    categorySlug: "tournament",
    tagsCsv: "icm,tournament,discipline",
    thumbnailUrl: "/images/thumbnails/strategy.jpeg",
    playbackUrl: null,
    durationSeconds: 91,
    viewCount: 9780,
  },
];

export const previewContest: Contest = {
  id: 77,
  title: "Play of the Week #1",
  status: "OPEN",
  startsAt,
  endsAt,
  finalizedAt: null,
  winnerEntryId: null,
  winnerClipId: null,
  winnerCreatorUserId: null,
  entries: [
    { entryId: 9001, clipId: 101, votes: 184 },
    { entryId: 9002, clipId: 102, votes: 139 },
    { entryId: 9003, clipId: 103, votes: 96 },
  ],
};

export const previewContestHistory: Contest[] = [
  {
    ...previewContest,
    id: 71,
    title: "Week 0 Winner: River Pressure",
    status: "FINALIZED",
    finalizedAt: new Date(now - 3 * 24 * 36e5).toISOString(),
    winnerEntryId: 8801,
    winnerClipId: 102,
    winnerCreatorUserId: 202,
    entries: [{ entryId: 8801, clipId: 102, votes: 221 }],
  },
];

export const previewLeaderboard: LeaderboardRow[] = [
  { subjectId: 101, label: "Hero Call at the Final Table", score: 184 },
  { subjectId: 102, label: "Three-Barrel Bluff Under Pressure", score: 139 },
  { subjectId: 103, label: "ICM Fold of the Week", score: 96 },
];

export const previewCreatorLeaderboard: CreatorLeaderboardRow[] = [
  { userId: 201, creatorSlug: "acecreator", wins: 2, nominations: 8, totalContestVotes: 841, score: 1041 },
  { userId: 202, creatorSlug: "riverpressure", wins: 1, nominations: 6, totalContestVotes: 622, score: 752 },
  { userId: 203, creatorSlug: "icmstudio", wins: 0, nominations: 5, totalContestVotes: 413, score: 463 },
];

export const previewCreatorProfile: CreatorProfile = {
  userId: 201,
  creatorSlug: "acecreator",
  headline: "Final-table strategy, river calls, and elite hand breakdowns.",
  verified: true,
  socialLinksJson: "{\"x\":\"@acecreator\",\"youtube\":\"AceCreator Poker\"}",
  reputation: {
    wins: 2,
    nominations: 8,
    totalContestVotes: 841,
    rankingPosition: 1,
    topCategory: "final-table",
    badges: ["Weekly Winner", "Top Ranked", "Fan Favorite"],
  },
};

export const previewProfile: Profile = {
  userId: 201,
  username: "acecreator",
  displayName: "Ace Creator",
  bio: "Premium poker creator focused on decisive tournament moments.",
  avatarUrl: "/images/creator-avatar.jpeg",
  bannerUrl: "/images/thumbnails/high-stakes.jpeg",
  country: "Norway",
  city: "Oslo",
  languages: ["English", "Norwegian"],
  profileType: "Creator",
  pokerRoles: ["Live player", "Creator"],
  preferredGames: ["Texas Hold'em", "Omaha"],
  preferredFormats: ["Tournament", "Live events"],
  contentFocus: ["Hand breakdowns", "River moments"],
  preferredRegion: "Nordics and online",
  interestedEventTypes: ["Live tournament", "Creator event", "Watch party"],
  onlineEventsAllowed: true,
  maxTravelDistanceKm: 500,
  eventAlertsOptIn: true,
  partnerOffersOptIn: false,
};

export const previewComments: Comment[] = [
  {
    id: 501,
    clipId: 101,
    authorUserId: 301,
    parentCommentId: null,
    body: "That river pause makes the whole clip. Clean nomination.",
    createdAt: new Date(now - 2 * 36e5).toISOString(),
  },
];

export const previewReactions: ReactionSummary[] = [
  { reactionType: "RESPECT", count: 42, reactedByCurrentUser: false },
  { reactionType: "CLUTCH", count: 31, reactedByCurrentUser: false },
];

export const previewEvents: PokerEvent[] = [
  {
    id: 1,
    title: "Nordic Poker Weekend",
    organizerName: "Partner card room",
    organizerType: "Partner",
    eventType: "Live tournament",
    startsAt: "2026-05-24T18:00:00Z",
    endsAt: "2026-05-26T22:00:00Z",
    timezone: "Europe/Oslo",
    locationType: "LIVE",
    country: "Norway",
    city: "Oslo",
    venueName: "Oslo poker club",
    onlineUrl: null,
    registrationUrl: "https://example.com/nordic-poker-weekend",
    affiliateUrl: "https://example.com/nordic-poker-weekend?partner=propokertv",
    affiliateDisclosureRequired: true,
    imageUrl: "/images/thumbnails/high-stakes.jpeg",
    description: "A curated live tournament weekend surfaced as a partner discovery card for the ProPokerTV audience.",
    tags: ["live tournament", "nordic", "partner"],
    status: "PUBLISHED",
    featured: true,
    sponsored: true,
  },
  {
    id: 2,
    title: "Creator Hand Review Night",
    organizerName: "ProPokerTV creators",
    organizerType: "Creator",
    eventType: "Creator event",
    startsAt: "2026-06-02T19:00:00Z",
    endsAt: null,
    timezone: "Europe/Oslo",
    locationType: "ONLINE",
    country: null,
    city: null,
    venueName: null,
    onlineUrl: "https://example.com/creator-review-night",
    registrationUrl: "https://example.com/creator-review-night",
    affiliateUrl: null,
    affiliateDisclosureRequired: false,
    imageUrl: "/images/thumbnails/strategy.jpeg",
    description: "An online creator event focused on hand breakdowns, reputation, and community discovery.",
    tags: ["online", "creator", "hand review"],
    status: "PUBLISHED",
    featured: false,
    sponsored: false,
  },
];
