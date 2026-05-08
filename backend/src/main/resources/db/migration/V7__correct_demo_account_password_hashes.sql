UPDATE app_user
SET password_hash = '$2a$10$Mi7zTF81dcEQVhuaPuEeEORzvI.gF8JLUd50FMZB8Bx4v.Vqaj0NG',
    updated_at = NOW()
WHERE email IN ('admin@propokertv.test', 'creator@propokertv.test', 'fan@propokertv.test');
