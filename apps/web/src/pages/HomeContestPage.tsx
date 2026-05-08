import { Link } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import * as api from "../lib/api";
import { useAuth } from "../state/auth";
import type { Clip, Contest, CreatorLeaderboardRow, LeaderboardRow } from "../types";

function formatDate(iso: string) {
  return new Date(iso).toLocaleString("nb-NO", {
    day: "2-digit",
    month: "short",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function formatDuration(seconds?: number | null) {
  if (!seconds && seconds !== 0) return "Live";
  const mins = Math.floor(seconds / 60);
  const secs = seconds % 60;
  return `${mins}:${secs.toString().padStart(2, "0")}`;
}

function scoreLabel(votes: number) {
  return votes === 1 ? "1 vote" : `${votes.toLocaleString("nb-NO")} votes`;
}

export function HomeContestPage() {
  const { currentUser, tokens, setTokens } = useAuth();
  const [contest, setContest] = useState<Contest | null>(null);
  const [clips, setClips] = useState<Record<number, Clip>>({});
  const [topClips, setTopClips] = useState<LeaderboardRow[]>([]);
  const [topCreators, setTopCreators] = useState<CreatorLeaderboardRow[]>([]);
  const [history, setHistory] = useState<Contest[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [voteMessage, setVoteMessage] = useState<string | null>(null);

  const load = async () => {
    const [contestResult, clipsResult, topClipsResult, topCreatorsResult, historyResult] = await Promise.allSettled([
      api.getContest(),
      api.getClips(),
      api.getLeaderboard(),
      api.getCreatorLeaderboard(),
      api.getContestHistory(),
    ]);

    if (contestResult.status === "fulfilled") {
      setContest(contestResult.value);
    } else {
      setError(contestResult.reason instanceof Error ? contestResult.reason.message : "No active contest.");
    }

    if (clipsResult.status === "fulfilled") {
      setClips(Object.fromEntries(clipsResult.value.map((clip) => [clip.id, clip])));
    }
    if (topClipsResult.status === "fulfilled") setTopClips(topClipsResult.value);
    if (topCreatorsResult.status === "fulfilled") setTopCreators(topCreatorsResult.value);
    if (historyResult.status === "fulfilled") setHistory(historyResult.value);
  };

  useEffect(() => {
    void load();
  }, []);

  const leadingEntry = useMemo(() => {
    return contest?.entries.slice().sort((a, b) => b.votes - a.votes)[0] ?? null;
  }, [contest]);

  const handleVote = async (entryId: number) => {
    if (!contest) return;
    try {
      const updated = await api.withRefresh((auth) => api.vote(auth, contest.id, entryId), tokens, setTokens);
      setContest(updated);
      setVoteMessage("Vote registered.");
      setError(null);
    } catch (cause) {
      setVoteMessage(null);
      setError(cause instanceof Error ? cause.message : "Failed to vote.");
    }
  };

  return (
    <div className="stack-xl">
      <section className="contest-hero">
        <div className="contest-hero__copy">
          <span className="eyebrow">Weekly Poker Clip League</span>
          <h1>Vote for this week's best poker clip.</h1>
          <p>
            Creators post the hands. Fans decide the winner. Wins, nominations, and votes become creator status.
          </p>
          <div className="hero-actions">
            <Link className="button primary" to={currentUser ? "/upload" : "/register"}>
              {currentUser ? "Submit a clip" : "Join and vote"}
            </Link>
            <Link className="button secondary" to="/leaderboard">
              View rankings
            </Link>
          </div>
        </div>

        <div className="contest-hero__panel">
          {contest ? (
            <>
              <div className="meta-row">
                <span>{contest.status}</span>
                <span>{formatDate(contest.startsAt)} - {formatDate(contest.endsAt)}</span>
              </div>
              <h2>{contest.title}</h2>
              <p>{contest.entries.length} nominated clip(s)</p>
              {leadingEntry ? (
                <div className="leader-callout">
                  <span>Current leader</span>
                  <strong>{clips[leadingEntry.clipId]?.title ?? `Clip #${leadingEntry.clipId}`}</strong>
                  <small>{scoreLabel(leadingEntry.votes)}</small>
                </div>
              ) : null}
            </>
          ) : (
            <div className="empty-card compact">
              <p>{error ?? "No active contest yet."}</p>
            </div>
          )}
        </div>
      </section>

      {error ? <div className="error-banner">{error}</div> : null}
      {voteMessage ? <div className="success-chip">{voteMessage}</div> : null}

      <section className="stack-md">
        <div className="section-title">
          <span>Vote now</span>
          <h2>This week's nominees</h2>
          <p>One vote per user. Finalized contests lock the result into winner history.</p>
        </div>
        <div className="clips-grid">
          {contest?.entries.length ? (
            contest.entries.map((entry) => {
              const clip = clips[entry.clipId];
              return (
                <article key={entry.entryId} className="nominee-card panel">
                  <div className="clip-thumb">
                    {clip?.thumbnailUrl ? <img src={clip.thumbnailUrl} alt={clip.title} /> : <div className="thumb-fallback">PPTV</div>}
                    <span className="duration-badge">{formatDuration(clip?.durationSeconds)}</span>
                  </div>
                  <div className="meta-row">
                    <span>{clip?.categorySlug ?? "clip league"}</span>
                    <span>{scoreLabel(entry.votes)}</span>
                  </div>
                  <h3>{clip?.title ?? `Clip #${entry.clipId}`}</h3>
                  <p>{clip?.description ?? "Contest nominee linked from the backend."}</p>
                  <div className="card-actions">
                    {clip ? <Link className="button secondary" to={`/clips/${clip.slug}`}>Watch</Link> : null}
                    <button className="button primary" disabled={!currentUser || contest.status !== "OPEN"} onClick={() => handleVote(entry.entryId)}>
                      Vote
                    </button>
                  </div>
                </article>
              );
            })
          ) : (
            <div className="empty-card">
              <p>No nominees are available yet.</p>
            </div>
          )}
        </div>
      </section>

      <section className="three-up-grid">
        <div className="panel">
          <div className="section-title">
            <span>Clips</span>
            <h2>Top clips</h2>
            <p>Approved clips ranked by current score.</p>
          </div>
          <ol className="leaderboard-list">
            {topClips.slice(0, 5).map((row, index) => (
              <li key={row.subjectId}>
                <strong>#{index + 1}</strong>
                <span>{row.label}</span>
                <small>{row.score.toLocaleString("nb-NO")} pts</small>
              </li>
            ))}
          </ol>
        </div>

        <div className="panel">
          <div className="section-title">
            <span>Creators</span>
            <h2>Top creators</h2>
            <p>Wins, votes, and nominations become reputation.</p>
          </div>
          <ol className="leaderboard-list">
            {topCreators.slice(0, 5).map((row, index) => (
              <li key={row.userId}>
                <strong>#{index + 1}</strong>
                <span>@{row.creatorSlug}</span>
                <small>{row.wins} win(s)</small>
              </li>
            ))}
          </ol>
        </div>

        <div className="panel">
          <div className="section-title">
            <span>History</span>
            <h2>Recent winners</h2>
            <p>Finalized contests create durable status.</p>
          </div>
          <ol className="leaderboard-list">
            {history.slice(0, 5).map((row) => (
              <li key={row.id}>
                <strong>#{row.id}</strong>
                <span>{row.title}</span>
                <small>{row.finalizedAt ? formatDate(row.finalizedAt) : row.status}</small>
              </li>
            ))}
            {!history.length ? <li>No finalized contests yet</li> : null}
          </ol>
        </div>
      </section>
    </div>
  );
}
