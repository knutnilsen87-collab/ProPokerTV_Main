import { useEffect, useState } from "react";
import * as api from "../lib/api";
import { useAuth } from "../state/auth";
import type { Report } from "../types";

export function ModerationQueuePage() {
  const { currentUser, tokens, setTokens } = useAuth();
  const [reports, setReports] = useState<Report[]>([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const canModerate = currentUser?.role === "ADMIN" || currentUser?.role === "MODERATOR";

  const load = async () => {
    setLoading(true);
    try {
      const rows = await api.withRefresh(api.getModerationQueue, tokens, setTokens);
      setReports(rows);
      setError(null);
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Failed to load moderation queue.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (canModerate) void load();
  }, [canModerate]);

  const decide = async (clipId: number, decision: string) => {
    try {
      const status = await api.withRefresh((auth) => api.moderateClip(auth, clipId, decision), tokens, setTokens);
      setMessage(`Clip #${clipId}: ${status}`);
      await load();
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : "Moderation action failed.");
    }
  };

  if (!currentUser) return <div className="error-banner">Sign in as admin or moderator.</div>;
  if (!canModerate) return <div className="error-banner">Admin or moderator access required.</div>;

  return (
    <div className="stack-lg">
      <div className="section-title">
        <span>Moderation</span>
        <h2>Queue</h2>
        <p>Review reports and keep the weekly contest brand-safe.</p>
      </div>
      {message ? <div className="success-chip">{message}</div> : null}
      {error ? <div className="error-banner">{error}</div> : null}
      {loading ? <div className="loading-panel">Loading moderation queue...</div> : null}

      {!loading && !reports.length ? (
        <div className="empty-card">
          <h3>Queue is clear</h3>
          <p>Reports appear here when users flag clips or comments.</p>
        </div>
      ) : null}

      <div className="stack-md">
        {reports.map((report) => (
          <article className="panel report-row" key={report.id}>
            <div>
              <div className="meta-row">
                <span>{report.targetType}</span>
                <span>Target #{report.targetId}</span>
                <span>{report.status}</span>
              </div>
              <h3>{report.reason}</h3>
              <p>{report.note || "No additional note."}</p>
            </div>
            {report.targetType === "CLIP" ? (
              <div className="card-actions">
                <button className="button secondary" onClick={() => decide(report.targetId, "APPROVE")}>Approve</button>
                <button className="button secondary" onClick={() => decide(report.targetId, "REJECT")}>Reject</button>
                <button className="button primary" onClick={() => decide(report.targetId, "REMOVE")}>Remove</button>
              </div>
            ) : null}
          </article>
        ))}
      </div>
    </div>
  );
}
