-- =====================================================
-- V1__init_schema.sql
-- Початкова схема WayPoint
-- =====================================================

-- ---------- users ----------
CREATE TABLE users (
    id            UUID PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    full_name     VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    avatar_url    VARCHAR(255),
    created_at    TIMESTAMP NOT NULL
);

-- ---------- trips ----------
CREATE TABLE trips (
    id           UUID PRIMARY KEY,
    title        VARCHAR(150) NOT NULL,
    description  TEXT,
    cover_url    VARCHAR(255),
    status       VARCHAR(20) NOT NULL
        CHECK (status IN ('PLANNING', 'IN_PROGRESS', 'COMPLETED', 'DELETED')),
    invite_code  UUID NOT NULL UNIQUE,
    start_date   DATE,
    end_date     DATE,
    created_at   TIMESTAMP NOT NULL,
    owner_id     UUID NOT NULL REFERENCES users(id)
);

CREATE INDEX idx_trips_owner_id ON trips(owner_id);

-- ---------- trip_members ----------
CREATE TABLE trip_members (
    id         UUID PRIMARY KEY,
    trip_id    UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    user_id    UUID NOT NULL REFERENCES users(id),
    role       VARCHAR(20) NOT NULL
        CHECK (role IN ('OWNER', 'MEMBER', 'INVITED')),
    joined_at  TIMESTAMP NOT NULL,
    CONSTRAINT uq_trip_members_trip_user UNIQUE (trip_id, user_id)
);

CREATE INDEX idx_trip_members_trip_id ON trip_members(trip_id);
CREATE INDEX idx_trip_members_user_id ON trip_members(user_id);

-- ---------- checkpoint ----------
CREATE TABLE checkpoint (
    id                  UUID PRIMARY KEY,
    title               VARCHAR(150) NOT NULL,
    note                TEXT,
    checkpoint_type     VARCHAR(20) NOT NULL
        CHECK (checkpoint_type IN ('FOOD', 'ENTERTAINMENT', 'LANDMARK', 'ACCOMMODATION', 'SHOPPING', 'OTHER')),
    start_time          TIMESTAMP,
    end_time            TIMESTAMP,
    link                VARCHAR(255),
    cost                NUMERIC(10, 2),
    departure_location  VARCHAR(200) NOT NULL,
    arrival_location    VARCHAR(200) NOT NULL,
    trip_id             UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    created_at          TIMESTAMP NOT NULL,
    is_completed        BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_checkpoint_trip_id ON checkpoint(trip_id);

-- ---------- preparations ----------
CREATE TABLE preparations (
    id                  UUID PRIMARY KEY,
    title               VARCHAR(150) NOT NULL,
    note                TEXT,
    deadline            TIMESTAMP,
    link                VARCHAR(255),
    cost                NUMERIC(10, 2),
    trip_id             UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    assigned_member_id  UUID REFERENCES trip_members(id),
    created_at          TIMESTAMP NOT NULL,
    is_completed        BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_preparations_trip_id ON preparations(trip_id);
CREATE INDEX idx_preparations_assigned_member_id ON preparations(assigned_member_id);

-- ---------- transfers ----------
CREATE TABLE transfers (
    id                  UUID PRIMARY KEY,
    title               VARCHAR(150) NOT NULL,
    note                TEXT,
    transport_type      VARCHAR(20) NOT NULL
        CHECK (transport_type IN ('PLANE', 'CAR', 'SHIP', 'TRAIN', 'BUS', 'FOOT', 'OTHER')),
    departure_time      TIMESTAMP,
    arrival_time        TIMESTAMP,
    ticket              VARCHAR(255),
    cost                NUMERIC(10, 2),
    departure_location  VARCHAR(200) NOT NULL,
    arrival_location    VARCHAR(200) NOT NULL,
    trip_id             UUID NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    created_at          TIMESTAMP NOT NULL
);

CREATE INDEX idx_transfers_trip_id ON transfers(trip_id);