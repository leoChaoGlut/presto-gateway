create table dmp_app.pg_coordinator
(
    host   varchar(255) null,
    port   int          null,
    active tinyint(1)   not null,
    constraint coordinator_host_port_uindex
        unique (host, port)
);

