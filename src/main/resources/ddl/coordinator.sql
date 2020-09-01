create table presto_gateway.coordinator
(
    host varchar(255) not null,
    port int          not null,
    constraint coordinator_host_port_uindex
        unique (host, port)
);

