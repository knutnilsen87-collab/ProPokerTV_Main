import { Link } from "react-router-dom";
import { useEffect, useMemo, useState } from "react";
import * as api from "../lib/api";
import { PremiumImage } from "../components/PremiumImage";
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

function timeLeftLabel(iso: string) {
  const ms = new Date(iso).getTime() - Date.now();
  if (ms <= 0) return "Closing soon";
  const hours = Math.ceil(ms / 36e5);
  if (hours < 48) return `${hours}h left`;
  return `${Math.ceil(hours / 24)} days left`;
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

  const totalVotes = useMemo(() => {
    return contest?.entries.reduce((sum, entry) => sum + entry.votes, 0) ?? 0;
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
      <section className="contest-hero editorial-league-hero">
        <div className="contest-hero__copy">
          <span className="eyebrow">ProPokerTV Weekly League</span>
          <h1>
            <span>This week's</span>
            <span>best poker</span>
            <span>moment.</span>
          </h1>
          <p>
            Creators post the highlights. Fans vote for the winners. Rankings, badges, and weekly wins build poker status.
          </p>
          <div className="hero-actions">
            <Link className="button primary" to={currentUser ? "/upload" : "/register"}>
              {currentUser ? "Submit a clip" : "Vote now"}
            </Link>
            <Link className="button secondary" to="/leaderboard">
              See rankings
            </Link>
          </div>
        </div>

        <div className="contest-hero__panel">
          {contest ? (
            <>
              <div className="meta-row">
                <span>Live now</span>
                <span>{contest.status}</span>
              </div>
              <h2>{contest.title || "Play of the Week #1"}</h2>
              <div className="league-stat-grid">
                <span><strong>{contest.entries.length}</strong> nominated clips</span>
                <span><strong>{totalVotes}</strong> votes</span>
                <span><strong>{timeLeftLabel(contest.endsAt)}</strong> closes {formatDate(contest.endsAt)}</span>
              </div>
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
              <p>{error ?? "Next weekly contest opens soon. Submit a clip to be considered."}</p>
            </div>
          )}
        </div>
      </section>

      {error ? <div className="error-banner">{error}</div> : null}
      {voteMessage ? <div className="success-chip">{voteMessage}</div> : null}

      <section className="stack-md">
        <div className="section-title">
          <span>Nominated clips</span>
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
                    <PremiumImage src={clip?.thumbnailUrl} alt={clip?.title ?? `Clip #${entry.clipId}`} fallbackLabel="PPTV" />
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
              <p>No clips nominated yet. Admins can nominate approved clips.</p>
            </div>
          )}
        </div>
      </section>

      <section className="three-up-grid">
        <div className="panel">
          <div className="section-title">
            <span>Weekly ranking</span>
            <h2>Leaderboard preview</h2>
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
            <span>Hall of Fame</span>
            <h2>Past winners</h2>
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
