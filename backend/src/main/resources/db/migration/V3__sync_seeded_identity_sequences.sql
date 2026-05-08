-- Keep BIGSERIAL/IDENTITY-backed sequences aligned after explicit-ID seed data.
SELECT setval('app_user_id_seq', COALESCE((SELECT MAX(id) FROM app_user), 0) + 1, false);
SELECT setval('clip_id_seq', COALESCE((SELECT MAX(id) FROM clip), 0) + 1, false);
SELECT setval('weekly_contest_id_seq', COALESCE((SELECT MAX(id) FROM weekly_contest), 0) + 1, false);
SELECT setval('weekly_contest_entry_id_seq', COALESCE((SELECT MAX(id) FROM weekly_contest_entry), 0) + 1, false);
