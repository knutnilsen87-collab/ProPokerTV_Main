INSERT INTO app_user (id, email, password_hash, role, email_verified, account_status, created_at, updated_at)
VALUES
(1, 'admin@propokertv.test', '$2a$10$CwTycUXWue0Thq9StjUM0uJ8w36x6ZzWc2u1Q8VE3d2rYZRk5v0tS', 'ADMIN', TRUE, 'ACTIVE', NOW(), NOW()),
(2, 'creator@propokertv.test', '$2a$10$CwTycUXWue0Thq9StjUM0uJ8w36x6ZzWc2u1Q8VE3d2rYZRk5v0tS', 'CREATOR', TRUE, 'ACTIVE', NOW(), NOW()),
(3, 'fan@propokertv.test', '$2a$10$CwTycUXWue0Thq9StjUM0uJ8w36x6ZzWc2u1Q8VE3d2rYZRk5v0tS', 'USER', TRUE, 'ACTIVE', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO profile (user_id, username, display_name, bio, avatar_url, banner_url, created_at, updated_at)
VALUES
(1, 'admin', 'Admin', 'Platform admin demo user', NULL, NULL, NOW(), NOW()),
(2, 'acecreator', 'Ace Creator', 'Featured poker creator demo profile', NULL, NULL, NOW(), NOW()),
(3, 'grinderfan', 'Grinder Fan', 'Poker fan demo profile', NULL, NULL, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO creator_profile (user_id, creator_slug, headline, is_verified, social_links_json, created_at, updated_at)
VALUES
(2, 'acecreator', 'High-stakes creator and tournament storyteller', TRUE, '{"youtube":"https://youtube.com/@acecreator"}', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO clip (id, owner_user_id, slug, title, description, visibility, moderation_status, category_slug, tags_csv, playback_url, thumbnail_url, duration_seconds, view_count, created_at, updated_at)
VALUES
(1, 2, 'hero-call-at-final-table', 'Hero Call at the Final Table', 'Demo approved clip', 'PUBLIC', 'APPROVED', 'highlights', 'hero-call,final-table', 'https://cdn.example.com/demo1.mp4', 'https://cdn.example.com/demo1.jpg', 74, 1420, NOW(), NOW()),
(2, 2, 'bluff-of-the-week', 'Bluff of the Week', 'Demo pending clip', 'PUBLIC', 'PENDING_REVIEW', 'highlights', 'bluff,featured', 'https://cdn.example.com/demo2.mp4', 'https://cdn.example.com/demo2.jpg', 53, 820, NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO comment (clip_id, author_user_id, body, created_at, updated_at)
VALUES
(1, 3, 'That river call was absurd.', NOW(), NOW()),
(1, 2, 'One of my favorite hands this month.', NOW(), NOW());

INSERT INTO reaction (clip_id, user_id, reaction_type, created_at, updated_at)
VALUES
(1, 3, 'LIKE', NOW(), NOW()),
(1, 2, 'FIRE', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO weekly_contest (id, title, status, starts_at, ends_at, created_at, updated_at)
VALUES
(1, 'Play of the Week #1', 'OPEN', NOW() - INTERVAL '1 day', NOW() + INTERVAL '6 day', NOW(), NOW())
ON CONFLICT DO NOTHING;

INSERT INTO weekly_contest_entry (id, weekly_contest_id, clip_id, created_at, updated_at)
VALUES
(1, 1, 1, NOW(), NOW())
ON CONFLICT DO NOTHING;
