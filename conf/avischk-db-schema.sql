create table batch
(
    batchid      varchar(255)             not null,
    roundtrip    integer                  not null,
    date         date                     not null,
    newspaper    varchar(255)             not null,
    state        text                     not null,
    lastmodified timestamp with time zone not null,
    num_problems integer                  not null,
    constraint batchid_newspaper
        primary key (batchid, newspaper)
);

alter table batch
    owner to kuanadpa;

create table newspaper
(
    absolute_path       varchar(1024) not null
        constraint absolute_path
            primary key,
    newspaperid         varchar(255)  not null,
    edition_date        date          not null,
    page_number         varchar(4)    not null,
    batchid             varchar(255)  not null,
    section_title       varchar(255)  not null,
    flowfile_attributes json          not null
);

alter table newspaper
    owner to kuanadpa;

create table newspaper_xml
(
    absolute_path       varchar(1024) not null
        constraint absolute_path_xml
            primary key,
    batchid             varchar(255)  not null,
    newspaper           varchar(255)  not null,
    flowfile_attributes json          not null,
    constraint newspaper_xml_batch
        foreign key (newspaper, batchid) references batch (newspaper, batchid)
);

alter table newspaper_xml
    owner to kuanadpa;

create table notes
(
    id            serial
        primary key,
    batchid       varchar(255),
    newspaperid   varchar(255),
    edition_date  date,
    edition_title varchar(255),
    section_title varchar(255),
    page_number   integer,
    username      varchar(10)              not null,
    notes         text                     not null,
    created       timestamp with time zone not null
);

alter table notes
    owner to kuanadpa;

create table problems
(
    batchid          varchar(255)                                        not null,
    file             varchar(1024)                                       not null
        constraint problems_newspaper
            references newspaper,
    verapdf_ruleid   varchar(255),
    problem_category varchar(255)                                        not null,
    problem          text                                                not null,
    newspaper        varchar(255)                                        not null,
    id               integer default nextval('problems_id_sq'::regclass) not null
        primary key,
    constraint problems_batch
        foreign key (batchid, newspaper) references batch
);

alter table problems
    owner to kuanadpa;

