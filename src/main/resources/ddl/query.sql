create table presto_gateway.query
(
    query_id        varchar(255)                       not null
        primary key,
    coordinator_url varchar(255)                       not null,
    create_time     datetime default CURRENT_TIMESTAMP null
);

alter table query
	add elapsed_time int null comment '查询耗时 单位ms';

alter table query
	add user varchar(64) null comment '查询用户';

alter table query
	add status varchar(64) null comment '查询状态 FAILED FINISHED';

alter table query
	add error_type varchar(255) null comment '错误类型';

alter table query
	add `sql` text null comment 'sql文本';

alter table query
	add resource_group varchar(64) null comment '资源组';

alter table query
	add source varchar(128) null comment '查询来源';