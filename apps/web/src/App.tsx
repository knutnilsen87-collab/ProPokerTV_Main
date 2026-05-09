import { FormEvent, useEffect, useState } from "react";
import { Link, NavLink, Route, Routes, useNavigate, useParams } from "react-router-dom";
import { useAuth } from "./state/auth";
import { WeeklyContestPage } from "./pages/WeeklyContestPage";
import { AdminContestPage } from "./pages/AdminContestPage";
import { ModerationQueuePage } from "./pages/ModerationQueuePage";
import { PremiumImage } from "./components/PremiumImage";
import * as api from "./lib/api";
import type {
  Clip,
  Comment,
  Contest,
  CreatorProfile,
  LeaderboardRow,
  CreatorLeaderboardRow,
  Profile,
  ReactionSummary,
  SocialAuthProvider,
} from "./types";

declare global {
  interface Window {
    google?: {
      accounts?: {
        id?: {
          initialize: (options: { client_id: string; callback: (response: { credential?: string }) => void }) => void;
          renderButton: (element: HTMLElement, options: { theme: string; size: string; text: string; width?: number }) => void;
        };
      };
    };
  }
}

function formatDuration(seconds?: number | null) {
  if (!seconds && seconds !== 0) return "Live";
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}:${secs.toString().padStart(2, "0")}`;
}

function formatDate(iso: string) {
  return new Date(iso).toLocaleString("nb-NO", {
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function cx(...parts: Array<string | false | null | undefined>) {
  return parts.filter(Boolean).join(" ");
}

function GoogleIcon() {
  return (
    <svg aria-hidden="true" className="provider-icon" viewBox="0 0 24 24">
      <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09Z" />
      <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23Z" />
      <path fill="#FBBC05" d="M5.84 14.1c-.22-.66-.35-1.36-.35-2.1s.13-1.44.35-2.1V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l3.66-2.84Z" />
      <path fill="#EA4335" d="M12 5.38c1.62 0 3.06 0.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06L5.84 9.9c0.87-2.6 3.3-4.52 6.16-4.52Z" />
    </svg>
  );
}

function AppShell({ children }: { children: React.ReactNode }) {
  const { currentUser, signOut } = useAuth();
  const canManage = currentUser?.role === "ADMIN" || currentUser?.role === "MODERATOR";

  return (
    <div className="app-shell">
      <header className="topbar">
        <Link className="brand-mark" to="/">
          <PremiumImage className="brand-logo" src="/brand/logo-chip.png" alt="ProPokerTV logo" fallbackLabel="PPTV" />
          <span className="brand-copy">
            <strong>ProPokerTV</strong>
            <span>premium poker media</span>
          </span>
        </Link>

        <nav className="main-nav">
          <NavLink to="/">Home/League</NavLink>
          <NavLink to="/clips">Clips</NavLink>
          <NavLink to="/leaderboard">Rankings</NavLink>
          <NavLink to="/creators/acecreator">Creators</NavLink>
          <NavLink to="/upload">Upload</NavLink>
          {canManage ? <NavLink to="/admin/contests">Admin</NavLink> : null}
          {canManage ? <NavLink to="/admin/moderation">Moderation</NavLink> : null}
          {currentUser ? <NavLink to="/settings/profile">Profile</NavLink> : <NavLink to="/login">Sign in</NavLink>}
        </nav>

        <div className="nav-actions">
          {currentUser ? (
            <>
              <div className="signed-in-chip">
                <span>{currentUser.email}</span>
                <small>{currentUser.role}</small>
              </div>
              <button className="button secondary" onClick={signOut}>
                Sign out
              </button>
            </>
          ) : (
            <Link className="button primary" to="/register">
              Join the League
            </Link>
          )}
        </div>
      </header>

      <main className="page-shell">{children}</main>
    </div>
  );
}

function SectionTitle({ eyebrow, title, body }: { eyebrow: string; title: string; body: string }) {
  return (
    <div className="section-title">
      <span>{eyebrow}</span>
      <h2>{title}</h2>
      <p>{body}</p>
    </div>
  );
}

function ClipCard({ clip, featured = false }: { clip: Clip; featured?: boolean }) {
  return (
    <article className={cx("clip-card", featured && "clip-card--featured")}>
      <div className="clip-thumb">
        <PremiumImage src={clip.thumbnailUrl} alt={clip.title} fallbackLabel="PPTV" />
        <span className="duration-badge">{formatDuration(clip.durationSeconds)}</span>
      </div>
      <div className="clip-body">
        <div className="meta-row">
          <span>{clip.categorySlug ?? "featured"}</span>
          <span>{clip.viewCount.toLocaleString("nb-NO")} views</span>
        </div>
        <h3>{clip.title}</h3>
        <p>{clip.description || "No description yet."}</p>
        <div className="card-actions">
          <Link className="button secondary" to={`/clips/${clip.slug}`}>
            Watch clip
          </Link>
          {clip.tagsCsv ? <small>{clip.tagsCsv}</small> : null}
        </div>
      </div>
    </article>
  );
}

function HomePage() {
  const [clips, setClips] = useState<Clip[]>([]);
  const [contest, setContest] = useState<Contest | null>(null);
  const [leaderboard, setLeaderboard] = useState<LeaderboardRow[]>([]);
  const [creatorSpotlight, setCreatorSpotlight] = useState<CreatorProfile | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void Promise.allSettled([
      api.getClips(),
      api.getContest(),
      api.getLeaderboard(),
      api.getCreatorProfile("acecreator"),
    ]).then((results) => {
      const [clipsResult, contestResult, leaderboardResult, creatorResult] = results;

      if (clipsResult.status === "fulfilled") setClips(clipsResult.value);
      if (contestResult.status === "fulfilled") setContest(contestResult.value);
      if (leaderboardResult.status === "fulfilled") setLeaderboard(leaderboardResult.value);
      if (creatorResult.status === "fulfilled") setCreatorSpotlight(creatorResult.value);

      if (clipsResult.status === "rejected") {
        setError(clipsResult.reason instanceof Error ? clipsResult.reason.message : "Failed to load feed.");
      }
    });
  }, []);

  const featured = clips[0];
  const rest = clips.slice(1);

  return (
    <div className="stack-xl">
      <section className="hero-panel">
        <div className="hero-copy">
          <span className="eyebrow">Poker-first premium media</span>
          <h1>Editorial drama up top. Product clarity underneath.</h1>
          <p>Premium poker media with weekly contests, ranked clips, creator status, and a calm league rhythm.</p>
          <div className="hero-actions">
            <Link className="button primary" to="/register">
              Join the table
            </Link>
            <Link className="button secondary" to="/play-of-the-week">
              Open contest
            </Link>
          </div>
        </div>

        {featured ? (
          <div className="hero-feature">
            <ClipCard clip={featured} featured />
          </div>
        ) : (
          <div className="empty-card">
            <h3>No approved clips yet</h3>
            <p>The feed is live, but there are no approved clips to surface right now.</p>
          </div>
        )}
      </section>

      {error ? <div className="error-banner">{error}</div> : null}

      <section className="three-up-grid">
        <div className="panel">
          <SectionTitle
            eyebrow="Contest"
            title="Play of the Week"
            body="Ritual, urgency, and high-stakes voting. Slightly more gold, still productized."
          />
          {contest ? (
            <div className="contest-mini">
              <h3>{contest.title}</h3>
              <p>Status: {contest.status}</p>
              <p>
                {formatDate(contest.startsAt)} → {formatDate(contest.endsAt)}
              </p>
              <p>{contest.entries.length} nominee(s)</p>
              <Link className="button secondary" to="/play-of-the-week">
                See nominees
              </Link>
            </div>
          ) : (
            <div className="empty-card compact">
              <p>No active contest</p>
            </div>
          )}
        </div>

        <div className="panel">
          <SectionTitle
            eyebrow="Recognition"
            title="Top clips this week"
            body="Table-card hybrid presentation with clear rank hierarchy."
          />
          <ol className="leaderboard-list">
            {leaderboard.length ? (
              leaderboard.slice(0, 5).map((row, index) => (
                <li key={row.subjectId}>
                  <strong>#{index + 1}</strong>
                  <span>{row.label}</span>
                  <small>{row.score.toLocaleString("nb-NO")} pts</small>
                </li>
              ))
            ) : (
              <li>No leaderboard data yet</li>
            )}
          </ol>
        </div>

        <div className="panel">
          <SectionTitle
            eyebrow="Spotlight"
            title="Featured creator"
            body="Status and personality, not a resume."
          />
          {creatorSpotlight ? (
            <div className="creator-spotlight">
              <h3>@{creatorSpotlight.creatorSlug}</h3>
              <p>{creatorSpotlight.headline || "No headline yet."}</p>
              <div className="meta-row">
                <span>{creatorSpotlight.verified ? "Verified creator" : "Creator"}</span>
                <Link to={`/creators/${creatorSpotlight.creatorSlug}`}>Open profile</Link>
              </div>
            </div>
          ) : (
            <div className="empty-card compact">
              <p>Creator spotlight unavailable</p>
            </div>
          )}
        </div>
      </section>

      <section className="stack-md">
        <SectionTitle
          eyebrow="Trending lane"
          title="Latest uploads"
          body="Compact media cards for scanning approved clips without losing the league context."
        />
        <div className="clips-grid">
          {rest.length ? rest.map((clip) => <ClipCard clip={clip} key={clip.id} />) : featured ? null : <p>No clips yet.</p>}
        </div>
      </section>
    </div>
  );
}

function ClipDetailPage() {
  const { slug = "" } = useParams();
  const { tokens, setTokens, currentUser } = useAuth();
  const [clip, setClip] = useState<Clip | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [reactions, setReactions] = useState<ReactionSummary[]>([]);
  const [commentBody, setCommentBody] = useState("");
  const [commentError, setCommentError] = useState<string | null>(null);
  const [reportMessage, setReportMessage] = useState<string | null>(null);
  const [pageError, setPageError] = useState<string | null>(null);

  const load = async () => {
    try {
      const [clipData, commentData, reactionData] = await Promise.all([
        api.getClip(slug),
        api.getComments(slug),
        api.getReactions(slug, tokens),
      ]);
      setClip(clipData);
      setComments(commentData);
      setReactions(reactionData);
      setPageError(null);
    } catch (error) {
      setPageError(error instanceof Error ? error.message : "Failed to load clip.");
    }
  };

  useEffect(() => {
    void load();
  }, [slug]);

  const handleReaction = async (reactionType: string, reacted: boolean) => {
    const previous = reactions;
    setReactions((current) =>
      current.map((entry) =>
        entry.reactionType === reactionType
          ? {
              ...entry,
              count: entry.count + (reacted ? -1 : 1),
              reactedByCurrentUser: !reacted,
            }
          : entry,
      ),
    );

    try {
      await api.withRefresh(
        (auth) =>
          reacted
            ? api.unreact(auth, slug, reactionType)
            : api.react(auth, slug, reactionType),
        tokens,
        setTokens,
      );
    } catch (error) {
      setReactions(previous);
      setPageError(error instanceof Error ? error.message : "Failed to update reaction.");
    }
  };

  const submitComment = async (event: FormEvent) => {
    event.preventDefault();
    setCommentError(null);
    try {
      const posted = await api.withRefresh((auth) => api.postComment(auth, slug, commentBody), tokens, setTokens);
      setComments((current) => [...current, posted]);
      setCommentBody("");
    } catch (error) {
      setCommentError(error instanceof Error ? error.message : "Failed to post comment.");
    }
  };

  const reportClip = async () => {
    if (!clip) return;
    try {
      await api.withRefresh(
        (auth) => api.reportTarget(auth, { targetType: "CLIP", targetId: clip.id, reason: "Brand safety review", note: "User requested moderation review." }),
        tokens,
        setTokens,
      );
      setReportMessage("Report sent to moderation.");
    } catch (error) {
      setPageError(error instanceof Error ? error.message : "Failed to report clip.");
    }
  };

  if (pageError) {
    return <div className="error-banner">{pageError}</div>;
  }

  if (!clip) {
    return <div className="loading-panel">Loading clip…</div>;
  }

  return (
    <div className="detail-layout">
      <section className="media-stage panel">
        <div className="media-frame">
          {clip.playbackUrl ? (
            <video controls poster={clip.thumbnailUrl ?? undefined} src={clip.playbackUrl} />
          ) : clip.thumbnailUrl ? (
            <PremiumImage src={clip.thumbnailUrl} alt={clip.title} fallbackLabel="PPTV" />
          ) : (
            <div className="thumb-fallback large">♠</div>
          )}
        </div>
        <div className="stack-md">
          <div className="meta-row">
            <span>{clip.categorySlug ?? "featured"}</span>
            <span>{clip.viewCount.toLocaleString("nb-NO")} views</span>
            <span>{clip.moderationStatus}</span>
          </div>
          <h1>{clip.title}</h1>
          <p>{clip.description || "No description yet."}</p>
          <div className="reaction-row">
            {reactions.map((reaction) => (
              <button
                key={reaction.reactionType}
                className={cx("reaction-pill", reaction.reactedByCurrentUser && "reaction-pill--active")}
                disabled={!currentUser}
                onClick={() => handleReaction(reaction.reactionType, reaction.reactedByCurrentUser)}
              >
                <span>{reaction.reactionType}</span>
                <strong>{reaction.count}</strong>
              </button>
            ))}
          </div>
          <div className="card-actions">
            <button className="button secondary" disabled={!currentUser} onClick={reportClip}>
              Report clip
            </button>
            {reportMessage ? <span className="success-chip">{reportMessage}</span> : null}
          </div>
        </div>
      </section>

      <aside className="side-rail">
        <div className="panel">
          <SectionTitle
            eyebrow="Metadata"
            title="Clip detail"
            body="Player dominant above the fold, actions clear underneath."
          />
          <dl className="meta-list">
            <div>
              <dt>Slug</dt>
              <dd>{clip.slug}</dd>
            </div>
            <div>
              <dt>Duration</dt>
              <dd>{formatDuration(clip.durationSeconds)}</dd>
            </div>
            <div>
              <dt>Tags</dt>
              <dd>{clip.tagsCsv || "No tags"}</dd>
            </div>
          </dl>
        </div>
      </aside>

      <section className="panel">
        <SectionTitle
          eyebrow="Comments"
          title="Thread"
          body="Simpler than the hero player section, with inline post errors."
        />
        <form className="stack-sm" onSubmit={submitComment}>
          <textarea
            className="field"
            placeholder={currentUser ? "Add your take on the hand…" : "Sign in to comment"}
            value={commentBody}
            onChange={(event) => setCommentBody(event.target.value)}
            disabled={!currentUser}
            rows={4}
          />
          {commentError ? <div className="inline-error">{commentError}</div> : null}
          <div>
            <button className="button primary" disabled={!currentUser || !commentBody.trim()}>
              Post comment
            </button>
          </div>
        </form>

        <div className="comment-thread">
          {comments.length ? (
            comments.map((comment) => (
              <article key={comment.id} className="comment-card">
                <header>
                  <strong>User #{comment.authorUserId}</strong>
                  <span>{formatDate(comment.createdAt)}</span>
                </header>
                <p>{comment.body}</p>
              </article>
            ))
          ) : (
            <div className="empty-card compact">
              <p>No comments yet</p>
            </div>
          )}
        </div>
      </section>
    </div>
  );
}

function LeaderboardPage() {
  const [rows, setRows] = useState<LeaderboardRow[]>([]);
  const [creators, setCreators] = useState<CreatorLeaderboardRow[]>([]);
  useEffect(() => {
    void api.getLeaderboard().then(setRows).catch(() => setRows([]));
    void api.getCreatorLeaderboard().then(setCreators).catch(() => setCreators([]));
  }, []);

  return (
    <div className="stack-lg">
      <SectionTitle
        eyebrow="Recognition"
        title="Leaderboard"
        body="Clear rank hierarchy. Gold reserved for #1 and special states."
      />
      <div className="panel">
        <ol className="leaderboard-board">
          {rows.map((row, index) => (
            <li key={row.subjectId} className={cx(index === 0 && "winner-row")}>
              <span className="rank-index">#{index + 1}</span>
              <div>
                <strong>{row.label}</strong>
                <small>Clip #{row.subjectId}</small>
              </div>
              <span>{row.score.toLocaleString("nb-NO")} score</span>
            </li>
          ))}
        </ol>
      </div>
      <SectionTitle
        eyebrow="Creators"
        title="Creator reputation"
        body="Wins, nominations, and votes rolled into durable creator status."
      />
      <div className="panel">
        <ol className="leaderboard-board">
          {creators.map((row, index) => (
            <li key={row.userId} className={cx(index === 0 && "winner-row")}>
              <span className="rank-index">#{index + 1}</span>
              <div>
                <strong>@{row.creatorSlug}</strong>
                <small>{row.nominations} nominations</small>
              </div>
              <span>{row.wins} win(s) · {row.totalContestVotes} votes</span>
            </li>
          ))}
        </ol>
      </div>
    </div>
  );
}

function ContestPage() {
  const { tokens, setTokens, currentUser } = useAuth();
  const [contest, setContest] = useState<Contest | null>(null);
  const [clips, setClips] = useState<Record<number, Clip>>({});
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void api
      .getContest()
      .then(async (contestData) => {
        setContest(contestData);
        const clipPairs = await Promise.all(
          contestData.entries.map(async (entry) => {
            const list = await api.getClips();
            return list.find((clip) => clip.id === entry.clipId);
          }),
        );
        const nextMap: Record<number, Clip> = {};
        clipPairs.forEach((clip) => {
          if (clip) nextMap[clip.id] = clip;
        });
        setClips(nextMap);
      })
      .catch((cause) => setError(cause instanceof Error ? cause.message : "No active contest"));
  }, []);

  const handleVote = async (entryId: number) => {
    if (!contest) return;
    try {
      const updated = await api.withRefresh((auth) => api.vote(auth, contest.id, entryId), tokens, setTokens);
      setContest(updated);
      setError(null);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Failed to vote.");
    }
  };

  return (
    <div className="stack-lg">
      <section className="hero-panel contest-panel">
        <div className="hero-copy">
          <span className="eyebrow">Weekly ritual</span>
          <h1>Play of the Week</h1>
          <p>Weekly voting gives creators a repeatable reason to post and fans a clear reason to return.</p>
        </div>
        <div className="panel">
          {contest ? (
            <>
              <h3>{contest.title}</h3>
              <p>{contest.status}</p>
              <p>
                {formatDate(contest.startsAt)} → {formatDate(contest.endsAt)}
              </p>
            </>
          ) : (
            <p>No active contest</p>
          )}
        </div>
      </section>

      {error ? <div className="error-banner">{error}</div> : null}

      <div className="clips-grid">
        {contest?.entries.length ? (
          contest.entries.map((entry) => {
            const clip = clips[entry.clipId];
            return (
              <article key={entry.entryId} className="nominee-card panel">
                <div className="meta-row">
                  <span>Nominee</span>
                  <span>{entry.votes} votes</span>
                </div>
                <h3>{clip?.title ?? `Clip #${entry.clipId}`}</h3>
                <p>{clip?.description ?? "Contest clip linked from backend."}</p>
                <div className="card-actions">
                  {clip ? (
                    <Link className="button secondary" to={`/clips/${clip.slug}`}>
                      Watch clip
                    </Link>
                  ) : null}
                  <button className="button primary" disabled={!currentUser} onClick={() => handleVote(entry.entryId)}>
                    Vote
                  </button>
                </div>
              </article>
            );
          })
        ) : (
          <div className="empty-card">
            <p>No active contest</p>
          </div>
        )}
      </div>
    </div>
  );
}

function CreatorPage() {
  const { slug = "" } = useParams();
  const [creator, setCreator] = useState<CreatorProfile | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void api.getCreatorProfile(slug).then(setCreator).catch((cause) => setError(cause instanceof Error ? cause.message : "Creator not found"));
  }, [slug]);

  return (
    <div className="stack-lg">
      {error ? <div className="error-banner">{error}</div> : null}
      {creator ? (
        <>
          <section className="creator-header panel">
            <div className="creator-banner" />
            <div className="creator-profile-row">
              <div className="avatar-orb">{creator.creatorSlug.slice(0, 2).toUpperCase()}</div>
              <div>
                <h1>@{creator.creatorSlug}</h1>
                <p>{creator.headline || "No headline yet."}</p>
                <div className="meta-row">
                  <span>{creator.verified ? "Verified creator" : "Creator"}</span>
                  <span>User #{creator.userId}</span>
                </div>
                <div className="reputation-row">
                  <span>{creator.reputation.wins} win(s)</span>
                  <span>{creator.reputation.nominations} nomination(s)</span>
                  <span>{creator.reputation.totalContestVotes} vote(s)</span>
                  {creator.reputation.rankingPosition ? <span>Rank #{creator.reputation.rankingPosition}</span> : null}
                </div>
              </div>
            </div>
          </section>
          <section className="panel">
            <SectionTitle eyebrow="Reputation" title="Badges" body="Contest history creates visible status for creators." />
            <div className="badge-row">
              {creator.reputation.badges.length ? (
                creator.reputation.badges.map((badge) => <span className="success-chip" key={badge}>{badge}</span>)
              ) : (
                <span className="signed-in-chip">No badges yet</span>
              )}
            </div>
          </section>
          <section className="panel">
            <SectionTitle eyebrow="Links" title="Creator data" body="Public creator profile backed by `/api/v1/creators/{slug}`." />
            <pre className="json-box">{creator.socialLinksJson || "{ }"}</pre>
          </section>
        </>
      ) : (
        <div className="loading-panel">Loading creator…</div>
      )}
    </div>
  );
}

function AuthPage({ mode }: { mode: "login" | "register" }) {
  const navigate = useNavigate();
  const { signIn, signUp, signInWithProvider } = useAuth();
  const [email, setEmail] = useState(mode === "login" ? "creator@propokertv.test" : "");
  const [password, setPassword] = useState(mode === "login" ? "password" : "");
  const [error, setError] = useState<string | null>(null);
  const [forgotEmail, setForgotEmail] = useState("");
  const [forgotMessage, setForgotMessage] = useState<string | null>(null);
  const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID as string | undefined;
  const microsoftClientId = import.meta.env.VITE_MICROSOFT_CLIENT_ID as string | undefined;
  const microsoftTenantId = (import.meta.env.VITE_MICROSOFT_TENANT_ID as string | undefined) || "common";

  const handleSocialToken = async (provider: SocialAuthProvider, idToken: string) => {
    setError(null);
    try {
      await signInWithProvider(provider, idToken);
      navigate("/");
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Social sign-in failed.");
    }
  };

  const startMicrosoftLogin = () => {
    if (!microsoftClientId) {
      return;
    }
    const state = crypto.randomUUID();
    const nonce = crypto.randomUUID();
    window.sessionStorage.setItem("pptv-ms-oauth-state", state);
    const params = new URLSearchParams({
      client_id: microsoftClientId,
      response_type: "id_token",
      redirect_uri: window.location.origin + window.location.pathname,
      response_mode: "fragment",
      scope: "openid profile email",
      state,
      nonce,
      prompt: "select_account",
    });
    window.location.href = `https://login.microsoftonline.com/${microsoftTenantId}/oauth2/v2.0/authorize?${params.toString()}`;
  };

  useEffect(() => {
    if (!googleClientId) {
      return;
    }

    let cancelled = false;
    const scriptId = "google-identity-services";
    const mountButton = () => {
      if (cancelled || !window.google?.accounts?.id) {
        return;
      }
      const buttonRoot = document.getElementById("google-signin-button");
      if (!buttonRoot || buttonRoot.childElementCount > 0) {
        return;
      }
      window.google.accounts.id.initialize({
        client_id: googleClientId,
        callback: async (response) => {
          if (!response.credential) {
            setError("Google did not return an identity token.");
            return;
          }
          await handleSocialToken("google", response.credential);
        },
      });
      window.google.accounts.id.renderButton(buttonRoot, {
        theme: "outline",
        size: "large",
        text: mode === "login" ? "signin_with" : "signup_with",
        width: 320,
      });
    };

    const existing = document.getElementById(scriptId) as HTMLScriptElement | null;
    if (existing) {
      mountButton();
      return () => {
        cancelled = true;
      };
    }

    const script = document.createElement("script");
    script.id = scriptId;
    script.src = "https://accounts.google.com/gsi/client";
    script.async = true;
    script.defer = true;
    script.onload = mountButton;
    document.head.appendChild(script);

    return () => {
      cancelled = true;
    };
  }, [googleClientId, mode]);

  useEffect(() => {
    const hash = new URLSearchParams(window.location.hash.replace(/^#/, ""));
    const idToken = hash.get("id_token");
    const state = hash.get("state");
    if (!idToken || !state) {
      return;
    }
    const expectedState = window.sessionStorage.getItem("pptv-ms-oauth-state");
    window.sessionStorage.removeItem("pptv-ms-oauth-state");
    window.history.replaceState(null, "", window.location.pathname + window.location.search);
    if (state !== expectedState) {
      setError("Microsoft sign-in state did not match. Please try again.");
      return;
    }
    void handleSocialToken("microsoft", idToken);
  }, []);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    try {
      if (mode === "login") {
        await signIn(email, password);
      } else {
        await signUp(email, password);
      }
      navigate("/");
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Authentication failed.");
    }
  };

  const handleForgot = async (event: FormEvent) => {
    event.preventDefault();
    const message = await api.forgotPassword(forgotEmail);
    setForgotMessage(message);
  };

  return (
    <div className="auth-layout">
      <section className="panel auth-copy">
        <span className="eyebrow">{mode === "login" ? "Return to the table" : "Registration flow"}</span>
        <h1>{mode === "login" ? "Sign in" : "Create account"}</h1>
        <p>Create an account to vote, upload clips, build creator reputation, and return to the weekly league without friction.</p>
        <div className="demo-accounts">
          {api.DEMO_ACCOUNTS.map((account) => (
            <button
              className="demo-chip"
              key={account.email}
              onClick={() => {
                setEmail(account.email);
                setPassword(account.password);
              }}
            >
              {account.label}
            </button>
          ))}
        </div>
      </section>

      <section className="panel auth-form">
        <div className="social-auth stack-sm">
          {googleClientId ? (
            <div id="google-signin-button" className="social-provider-button" />
          ) : (
            <button className="social-login-button" type="button" disabled title="Set VITE_GOOGLE_CLIENT_ID and GOOGLE_OAUTH_CLIENT_ID to enable Google sign-in.">
              <GoogleIcon />
              <span>{mode === "login" ? "Sign in with Google" : "Sign up with Google"}</span>
            </button>
          )}
          {!googleClientId ? <div className="social-provider-note">Set VITE_GOOGLE_CLIENT_ID and GOOGLE_OAUTH_CLIENT_ID to enable Google sign-in.</div> : null}
          {microsoftClientId ? (
            <button className="button secondary" type="button" onClick={startMicrosoftLogin}>
              Continue with Microsoft
            </button>
          ) : null}
        </div>
        <div className="auth-divider"><span>or</span></div>
        <form className="stack-sm" onSubmit={submit}>
          <label>
            <span>Email</span>
            <input className="field" value={email} onChange={(event) => setEmail(event.target.value)} />
          </label>
          <label>
            <span>Password</span>
            <input className="field" type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
          </label>
          {error ? <div className="inline-error">{error}</div> : null}
          <button className="button primary">{mode === "login" ? "Sign in" : "Create account"}</button>
        </form>

        {mode === "login" ? (
          <form className="stack-sm divider-top" onSubmit={handleForgot}>
            <label>
              <span>Forgot password</span>
              <input className="field" value={forgotEmail} onChange={(event) => setForgotEmail(event.target.value)} />
            </label>
            <button className="button secondary">Request reset</button>
            {forgotMessage ? <div className="success-chip">{forgotMessage}</div> : null}
          </form>
        ) : null}
      </section>
    </div>
  );
}

function ProfileSettingsPage() {
  const { tokens, setTokens, currentUser } = useAuth();
  const [form, setForm] = useState({
    username: "",
    displayName: "",
    bio: "",
    avatarUrl: "",
    bannerUrl: "",
  });
  const [profile, setProfile] = useState<Profile | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    void api
      .withRefresh(api.getMyProfile, tokens, setTokens)
      .then((data) => {
        setProfile(data);
        setForm({
          username: data.username,
          displayName: data.displayName,
          bio: data.bio ?? "",
          avatarUrl: data.avatarUrl ?? "",
          bannerUrl: data.bannerUrl ?? "",
        });
      })
      .catch((cause) => setError(cause instanceof Error ? cause.message : "Failed to load profile."));
  }, []);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    try {
      const updated = await api.withRefresh((auth) => api.updateMyProfile(auth, form), tokens, setTokens);
      setProfile(updated);
      setMessage("Profile updated.");
      setError(null);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Failed to update profile.");
    }
  };

  if (!currentUser) {
    return <div className="error-banner">Sign in to manage your profile.</div>;
  }

  return (
    <div className="stack-lg">
      <SectionTitle eyebrow="Settings" title="My profile" body="Keep identity, profile copy, and media surfaces ready for the league." />
      <div className="settings-grid">
        <form className="panel stack-sm" onSubmit={submit}>
          <label>
            <span>Username</span>
            <input className="field" value={form.username} onChange={(event) => setForm((current) => ({ ...current, username: event.target.value }))} />
          </label>
          <label>
            <span>Display name</span>
            <input className="field" value={form.displayName} onChange={(event) => setForm((current) => ({ ...current, displayName: event.target.value }))} />
          </label>
          <label>
            <span>Bio</span>
            <textarea className="field" rows={4} value={form.bio} onChange={(event) => setForm((current) => ({ ...current, bio: event.target.value }))} />
          </label>
          <label>
            <span>Avatar URL</span>
            <input className="field" value={form.avatarUrl} onChange={(event) => setForm((current) => ({ ...current, avatarUrl: event.target.value }))} />
          </label>
          <label>
            <span>Banner URL</span>
            <input className="field" value={form.bannerUrl} onChange={(event) => setForm((current) => ({ ...current, bannerUrl: event.target.value }))} />
          </label>
          {message ? <div className="success-chip">{message}</div> : null}
          {error ? <div className="inline-error">{error}</div> : null}
          <button className="button primary">Save profile</button>
        </form>

        <div className="panel profile-preview">
          <div className="creator-banner" style={{ backgroundImage: form.bannerUrl ? `url(${form.bannerUrl})` : undefined }} />
          <div className="creator-profile-row">
            <div className="avatar-orb">
              {form.avatarUrl ? <PremiumImage src={form.avatarUrl} alt={form.displayName} fallbackLabel={form.displayName.slice(0, 2).toUpperCase()} /> : form.displayName.slice(0, 2).toUpperCase()}
            </div>
            <div>
              <h3>{form.displayName || "Display name"}</h3>
              <p>@{form.username || "username"}</p>
              <small>{currentUser.email}</small>
            </div>
          </div>
          <p>{form.bio || "Profile preview updates live while you edit."}</p>
          {profile ? <small>User #{profile.userId}</small> : null}
        </div>
      </div>
    </div>
  );
}

function UploadPage() {
  const { tokens, setTokens, currentUser } = useAuth();
  const [form, setForm] = useState({
    slug: "",
    title: "",
    description: "",
    visibility: "PUBLIC",
    categorySlug: "highlights",
    tagsCsv: "",
    thumbnailUrl: "",
    playbackUrl: "",
    durationSeconds: "",
  });
  const [created, setCreated] = useState<Clip | null>(null);
  const [error, setError] = useState<string | null>(null);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    try {
      const clip = await api.withRefresh(
        (auth) =>
          api.createClip(auth, {
            ...form,
            durationSeconds: form.durationSeconds ? Number(form.durationSeconds) : null,
          }),
        tokens,
        setTokens,
      );
      setCreated(clip);
      setError(null);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Upload failed.");
    }
  };

  if (!currentUser) {
    return <div className="error-banner">Sign in to upload clips.</div>;
  }

  return (
    <div className="stack-lg">
      <SectionTitle
        eyebrow="Upload flow"
        title="Create a clip"
        body="Submit clip metadata first. Moderation reviews the clip before it can enter public league surfaces."
      />
      <div className="settings-grid">
        <form className="panel stack-sm" onSubmit={submit}>
          {(
            [
              ["slug", "Slug"],
              ["title", "Title"],
              ["description", "Description"],
              ["categorySlug", "Category"],
              ["tagsCsv", "Tags csv"],
              ["thumbnailUrl", "Thumbnail URL"],
              ["playbackUrl", "Playback URL"],
              ["durationSeconds", "Duration seconds"],
            ] as const
          ).map(([field, label]) => (
            <label key={field}>
              <span>{label}</span>
              {field === "description" ? (
                <textarea
                  className="field"
                  rows={4}
                  value={form[field]}
                  onChange={(event) => setForm((current) => ({ ...current, [field]: event.target.value }))}
                />
              ) : (
                <input
                  className="field"
                  value={form[field]}
                  onChange={(event) => setForm((current) => ({ ...current, [field]: event.target.value }))}
                />
              )}
            </label>
          ))}

          <label>
            <span>Visibility</span>
            <select className="field" value={form.visibility} onChange={(event) => setForm((current) => ({ ...current, visibility: event.target.value }))}>
              <option value="PUBLIC">PUBLIC</option>
              <option value="UNLISTED">UNLISTED</option>
              <option value="PRIVATE">PRIVATE</option>
            </select>
          </label>
          {error ? <div className="inline-error">{error}</div> : null}
          <button className="button primary">Submit clip</button>
        </form>

        <div className="panel">
          <h3>Upload state</h3>
          <p>After submit, backend sets moderation to `PENDING_REVIEW`. Creators can return later after review.</p>
          {created ? (
            <div className="success-stack">
              <div className="success-chip">Clip created: {created.title}</div>
              <Link className="button secondary" to={`/clips/${created.slug}`}>
                Open clip page
              </Link>
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
}

function NotFoundPage() {
  return (
    <div className="empty-card">
      <h2>Route not found</h2>
      <p>This route is not implemented yet.</p>
      <Link className="button secondary" to="/">
        Back home
      </Link>
    </div>
  );
}

export function App() {
  const { loading, authError } = useAuth();

  if (loading) {
    return <div className="boot-screen">Booting ProPokerTV…</div>;
  }

  return (
    <AppShell>
      <div className="stack-md">
        {authError ? <div className="error-banner">{authError}</div> : null}

        <Routes>
          <Route path="/" element={<WeeklyContestPage />} />
          <Route path="/clips" element={<HomePage />} />
          <Route path="/legacy-home" element={<HomePage />} />
          <Route path="/clips/:slug" element={<ClipDetailPage />} />
          <Route path="/creators/:slug" element={<CreatorPage />} />
          <Route path="/leaderboard" element={<LeaderboardPage />} />
          <Route path="/play-of-the-week" element={<ContestPage />} />
          <Route path="/login" element={<AuthPage mode="login" />} />
          <Route path="/register" element={<AuthPage mode="register" />} />
          <Route path="/settings/profile" element={<ProfileSettingsPage />} />
          <Route path="/upload" element={<UploadPage />} />
          <Route path="/admin/contests" element={<AdminContestPage />} />
          <Route path="/admin/moderation" element={<ModerationQueuePage />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </div>
    </AppShell>
  );
}
