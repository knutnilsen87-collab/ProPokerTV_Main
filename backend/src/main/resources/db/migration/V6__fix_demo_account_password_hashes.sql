UPDATE app_user
SET password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    updated_at = NOW()
WHERE email IN ('admin@propokertv.test', 'creator@propokertv.test', 'fan@propokertv.test');
