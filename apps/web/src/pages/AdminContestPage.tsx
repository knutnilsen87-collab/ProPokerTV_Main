import { FormEvent, useEffect, useState } from "react";
import * as api from "../lib/api";
import { useAuth } from "../state/auth";
import type { Clip, Contest } from "../types";

function toLocalInputValue(date: Date) {
  return new Date(date.getTime() - date.getTimezoneOffset() * 60_000).toISOString().slice(0, 16);
}

export function AdminContestPage() {
  const { currentUser, tokens, setTokens } = useAuth();
  const [contest, setContest] = useState<Contest | null>(null);
  const [clips, setClips] = useState<Clip[]>([]);
  const [form, setForm] = useState({
    title: "Play of the Week",
    startsAt: toLocalInputValue(new Date()),
    endsAt: toLocalInputValue(new Date(Date.now() + 7 * 24 * 60 * 60 * 1000)),
  });
  const [clipId, setClipId] = useState("");
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const canManage = currentUser?.role === "ADMIN" || currentUser?.role === "MODERATOR";

  useEffect(() => {
    void api.getContest().then(setContest).catch(() => setContest(null));
    void api.getClips().then(setClips).catch(() => setClips([]));
  }, []);

  const run = async (action: (auth: NonNullable<typeof tokens>) => Promise<Contest>, success: string) => {
    try {
      const updated = await api.withRefresh(action, tokens, setTokens);
      setContest(updated);
      setMessage(success);
      setError(null);
    } catch (cause) {
      setMessage(null);
      setError(cause instanceof Error ? cause.message : "Admin action failed.");
    }
  };

  const create = async (event: FormEvent) => {
    event.preventDefault();
    await run(
      (auth) =>
        api.createContest(auth, {
          title: form.title,
          startsAt: new Date(form.startsAt).toISOString(),
          endsAt: new Date(form.endsAt).toISOString(),
        }),
      "Contest created.",
    );
  };

  if (!currentUser) return <div className="error-banner">Sign in as admin or moderator.</div>;
  if (!canManage) return <div className="error-banner">Admin or moderator access required.</div>;

  return (
    <div className="stack-lg">
      <div className="section-title">
        <span>Admin</span>
        <h2>Contest management</h2>
        <p>Create, open, nominate, and finalize the Weekly Poker Clip League.</p>
      </div>

      {message ? <div className="success-chip">{message}</div> : null}
      {error ? <div className="error-banner">{error}</div> : null}

      <div className="settings-grid">
        <form className="panel stack-sm" onSubmit={create}>
          <h3>Create contest</h3>
          <label>
            <span>Title</span>
            <input className="field" value={form.title} onChange={(event) => setForm((current) => ({ ...current, title: event.target.value }))} />
          </label>
          <label>
            <span>Voting opens</span>
            <input className="field" type="datetime-local" value={form.startsAt} onChange={(event) => setForm((current) => ({ ...current, startsAt: event.target.value }))} />
          </label>
          <label>
            <span>Voting closes</span>
            <input className="field" type="datetime-local" value={form.endsAt} onChange={(event) => setForm((current) => ({ ...current, endsAt: event.target.value }))} />
          </label>
          <button className="button primary">Create contest</button>
        </form>

        <section className="panel stack-sm">
          <h3>Active contest operation</h3>
          {contest ? (
            <>
              <div className="meta-row">
                <span>#{contest.id}</span>
                <span>{contest.status}</span>
                <span>{contest.entries.length} nominee(s)</span>
              </div>
              <p>{contest.title}</p>
              <div className="card-actions">
                <button className="button secondary" disabled={contest.status === "OPEN"} onClick={() => run((auth) => api.openContest(auth, contest.id), "Contest opened.")}>
                  Open
                </button>
                <button className="button primary" disabled={contest.status === "FINALIZED"} onClick={() => run((auth) => api.finalizeContest(auth, contest.id), "Contest finalized.")}>
                  Finalize
                </button>
              </div>
              {contest.winnerClipId ? <div className="success-chip">Winner clip #{contest.winnerClipId}</div> : null}
            </>
          ) : (
            <div className="empty-card compact">
              <p>No active contest loaded. Create one to begin.</p>
            </div>
          )}
        </section>
      </div>

      <section className="panel stack-sm">
        <h3>Nominate approved clip</h3>
        <div className="card-actions">
          <select className="field" value={clipId} onChange={(event) => setClipId(event.target.value)}>
            <option value="">Choose approved clip</option>
            {clips.map((clip) => (
              <option value={clip.id} key={clip.id}>
                {clip.title}
              </option>
            ))}
          </select>
          <button
            className="button primary"
            disabled={!contest || !clipId || contest?.status === "FINALIZED"}
            onClick={() => contest && run((auth) => api.nominateClip(auth, contest.id, Number(clipId)), "Clip nominated.")}
          >
            Nominate
          </button>
        </div>
      </section>
    </div>
  );
}
