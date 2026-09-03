-- =====================================================
-- V4__fix_checkpoint_and_transfer_schema_drift.sql
-- Приведення схеми `checkpoint` та `transfers` у відповідність
-- до поточних JPA-entity (Checkpoint, Transfer).
--
-- Контекст: через spring.jpa.hibernate.ddl-auto=update Hibernate міг уже
-- самостійно додати колонки за замовчуванням для полів, яких бракувало
-- в оригінальній V1-схемі (наприклад transfer_type, location). Тому кожен
-- крок нижче перевіряє наявність колонки перед дією, щоб міграція була
-- ідемпотентною і безпечною незалежно від поточного фактичного стану БД.
-- =====================================================

-- ---------- transfers: transport_type -> transfer_type ----------

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'transfers' AND column_name = 'transport_type'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'transfers' AND column_name = 'transfer_type'
        ) THEN
            -- Обидві колонки існують (Hibernate вже додав transfer_type) —
            -- переносимо дані зі старої в нову там, де нова порожня, і прибираємо стару.
            UPDATE transfers SET transfer_type = transport_type WHERE transfer_type IS NULL;
            ALTER TABLE transfers DROP COLUMN transport_type;
        ELSE
            ALTER TABLE transfers RENAME COLUMN transport_type TO transfer_type;
        END IF;
    END IF;
END $$;

ALTER TABLE transfers ALTER COLUMN transfer_type SET NOT NULL;

ALTER TABLE transfers DROP CONSTRAINT IF EXISTS transfers_transport_type_check;
ALTER TABLE transfers DROP CONSTRAINT IF EXISTS transfers_transfer_type_check;
ALTER TABLE transfers ADD CONSTRAINT transfers_transfer_type_check
    CHECK (transfer_type IN ('PLANE', 'CAR', 'SHIP', 'TRAIN', 'BUS', 'FOOT', 'OTHER'));

-- ---------- transfers: ticket -> ticket_url ----------

DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'transfers' AND column_name = 'ticket'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'transfers' AND column_name = 'ticket_url'
        ) THEN
            UPDATE transfers SET ticket_url = ticket WHERE ticket_url IS NULL;
            ALTER TABLE transfers DROP COLUMN ticket;
        ELSE
            ALTER TABLE transfers RENAME COLUMN ticket TO ticket_url;
        END IF;
    END IF;
END $$;

-- ---------- checkpoint: departure_location/arrival_location -> location ----------

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'checkpoint' AND column_name = 'location'
    ) THEN
        ALTER TABLE checkpoint ADD COLUMN location VARCHAR(200);
    END IF;
END $$;

-- Переносимо існуючі дані (пріоритет departure_location, потім arrival_location)
-- в нове уніфіковане поле location, щоб не втратити наявні записи.
UPDATE checkpoint
SET location = COALESCE(location, departure_location, arrival_location, 'Unknown')
WHERE location IS NULL;

ALTER TABLE checkpoint ALTER COLUMN location SET NOT NULL;

ALTER TABLE checkpoint DROP COLUMN IF EXISTS departure_location;
ALTER TABLE checkpoint DROP COLUMN IF EXISTS arrival_location;

-- ---------- checkpoint: is_completed не використовується в entity ----------
-- Checkpoint не має статусу виконання (це властивість PreparationPoint, не Checkpoint).

ALTER TABLE checkpoint DROP COLUMN IF EXISTS is_completed;