create table dmp_app.pg_query
(
    query_id        varchar(255) not null primary key,
    coordinator_url varchar(255) not null,
    json            text,
    create_time     datetime default CURRENT_TIMESTAMP null
);

