-- Видаляємо старе обмеження, яке не пропускає EDITOR
ALTER TABLE trip_members DROP CONSTRAINT trip_members_role_check;

-- Створюємо нове обмеження з актуальним списком ролей
ALTER TABLE trip_members ADD CONSTRAINT trip_members_role_check
CHECK (role IN ('OWNER', 'MEMBER', 'EDITOR'));