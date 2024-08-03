-- noinspection SqlDialectInspectionForFile
-- noinspection SqlNoDataSourceInspectionForFile

------------------------------------------------------------------------------------------------------------------------
-------------------------------------------- Replication Slot ----------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
-- slot
-- @formatter:off
-- SELECT * FROM pg_replication_slots
-- SELECT pg_drop_replication_slot('eventuate_slot') FROM pg_replication_slots WHERE slot_name = 'eventuate_slot';
-- SELECT pg_drop_replication_slot('eventuate_slot2') FROM pg_replication_slots WHERE slot_name = 'eventuate_slot2';
-- SELECT * FROM pg_create_logical_replication_slot('eventuate_slot', 'wal2json');
-- SELECT * FROM pg_create_logical_replication_slot('eventuate_slot2', 'wal2json');
DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_replication_slots WHERE slot_name = 'eventuate_slot') THEN
            PERFORM * FROM pg_create_logical_replication_slot('eventuate_slot', 'wal2json');
        END IF;
        IF NOT EXISTS (SELECT 1 FROM pg_replication_slots WHERE slot_name = 'eventuate_slot2') THEN
            PERFORM * FROM pg_create_logical_replication_slot('eventuate_slot2', 'wal2json');
        END IF;
    END
$$;
-- @formatter:on

------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------ Schema ----------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
-- schema
CREATE SCHEMA IF NOT EXISTS eventuate;

------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------ Based Tables ----------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
-- entities table
CREATE TABLE IF NOT EXISTS eventuate.entities
(
    entity_type    VARCHAR,
    entity_id      VARCHAR,
    entity_version VARCHAR NOT NULL,
    PRIMARY KEY (entity_type, entity_id)
);
CREATE INDEX IF NOT EXISTS entities_idx ON eventuate.entities (entity_type, entity_id);

-- entities table
CREATE TABLE IF NOT EXISTS eventuate.snapshots
(
    entity_type       VARCHAR,
    entity_id         VARCHAR,
    entity_version    VARCHAR,
    snapshot_type     VARCHAR NOT NULL,
    snapshot_json     VARCHAR NOT NULL,
    triggering_events VARCHAR,
    PRIMARY KEY (entity_type, entity_id, entity_version)
);

-- cdc_monitoring table
CREATE TABLE IF NOT EXISTS eventuate.cdc_monitoring
(
    reader_id VARCHAR PRIMARY KEY,
    last_time BIGINT
);

-- offset_store received_messages
CREATE TABLE IF NOT EXISTS eventuate.received_messages
(
    consumer_id   VARCHAR,
    message_id    VARCHAR,
    creation_time BIGINT,
    published     SMALLINT DEFAULT 0,
    PRIMARY KEY (consumer_id, message_id)
);

-- offset_store table
CREATE TABLE IF NOT EXISTS eventuate.offset_store
(
    client_name       VARCHAR NOT NULL PRIMARY KEY,
    serialized_offset VARCHAR
);

-- message table
CREATE SEQUENCE IF NOT EXISTS eventuate.message_table_id_sequence START 1;
SELECT setval('eventuate.message_table_id_sequence', (ROUND(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000)) :: BIGINT);
CREATE TABLE IF NOT EXISTS eventuate.message
(
    dbid              BIGINT NOT NULL DEFAULT nextval('eventuate.message_table_id_sequence') PRIMARY KEY,
    id                VARCHAR,
    destination       TEXT   NOT NULL,
    headers           TEXT   NOT NULL,
    payload           TEXT   NOT NULL,
    published         SMALLINT        DEFAULT 0,
    message_partition SMALLINT,
    creation_time     BIGINT
);
ALTER SEQUENCE eventuate.message_table_id_sequence OWNED BY eventuate.message.dbid;

-- events table
CREATE SEQUENCE IF NOT EXISTS eventuate.events_table_id_sequence START 1;
SELECT setval('eventuate.events_table_id_sequence', (ROUND(EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000)) :: BIGINT);
CREATE TABLE IF NOT EXISTS eventuate.events
(
    id               BIGINT  NOT NULL DEFAULT nextval('eventuate.events_table_id_sequence') PRIMARY KEY,
    event_id         VARCHAR,
    event_type       VARCHAR,
    event_data       VARCHAR NOT NULL,
    entity_type      VARCHAR NOT NULL,
    entity_id        VARCHAR NOT NULL,
    triggering_event VARCHAR,
    metadata         VARCHAR,
    published        SMALLINT         DEFAULT 0
);
CREATE INDEX IF NOT EXISTS events_idx ON eventuate.events (entity_type, entity_id, id);
CREATE INDEX IF NOT EXISTS events_published_idx ON eventuate.events (published, id);
ALTER SEQUENCE eventuate.events_table_id_sequence OWNED BY eventuate.events.id;

------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------ Saga Tables -----------------------------------------------------
------------------------------------------------------------------------------------------------------------------------
--- saga_instance_participants
CREATE TABLE IF NOT EXISTS eventuate.saga_instance_participants
(
    saga_type   VARCHAR NOT NULL,
    saga_id     VARCHAR NOT NULL,
    destination VARCHAR NOT NULL,
    resource    VARCHAR NOT NULL,
    PRIMARY KEY (saga_type, saga_id, destination, resource)
);

--- saga_instance
CREATE TABLE IF NOT EXISTS eventuate.saga_instance
(
    saga_type       VARCHAR NOT NULL,
    saga_id         VARCHAR NOT NULL,
    state_name      VARCHAR NOT NULL,
    last_request_id VARCHAR,
    end_state       BOOLEAN,
    compensating    BOOLEAN,
    failed          BOOLEAN,
    saga_data_type  VARCHAR NOT NULL,
    saga_data_json  VARCHAR NOT NULL,
    PRIMARY KEY (saga_type, saga_id)
);

--- saga_lock_table
CREATE TABLE IF NOT EXISTS eventuate.saga_lock_table
(
    target    VARCHAR PRIMARY KEY,
    saga_type VARCHAR NOT NULL,
    saga_id   VARCHAR NOT NULL
);

--- saga_lock_table
CREATE TABLE IF NOT EXISTS eventuate.saga_stash_table
(
    message_id      VARCHAR PRIMARY KEY,
    target          VARCHAR NOT NULL,
    saga_type       VARCHAR NOT NULL,
    saga_id         VARCHAR NOT NULL,
    message_headers VARCHAR NOT NULL,
    message_payload VARCHAR NOT NULL
);
