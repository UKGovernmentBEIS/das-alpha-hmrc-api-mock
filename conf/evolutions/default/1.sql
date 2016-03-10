# --- !Ups

create table "ORGANISATION" ("UTR" VARCHAR NOT NULL PRIMARY KEY, NAME VARCHAR NOT NULL);
create table "SCHEME" (
    "EMPREF" VARCHAR NOT NULL PRIMARY KEY,
    "UTR" VARCHAR NULL,
     FOREIGN KEY (UTR) REFERENCES ORGANISATION(UTR)
);

# --- !Downs

drop table "SCHEME";
drop table "ORGANISATION";