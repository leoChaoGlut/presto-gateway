create table presto_gateway.query
(
    query_id        varchar(255) not null
        primary key,
    coordinator_url varchar(255) not null
);

