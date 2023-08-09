
drop table management_request_result;
drop table management_request;
drop table book_reviews;
drop table rentals;
drop table new_book_request_result;
drop table new_book_request;
drop table administrator;
drop table book;
drop table member;

create table administrator (
                               administrator_id bigint not null auto_increment,
                               created_at datetime(6),
                               last_modified_at datetime(6),
                               authority varchar(255),
                               email varchar(255),
                               name varchar(255),
                               password varchar(255),
                               primary key (administrator_id)
) engine=InnoDB default charset=utf8;

create table book (
                      book_id bigint not null auto_increment,
                      created_at datetime(6),
                      last_modified_at datetime(6),
                      author varchar(255),
                      location varchar(255),
                      published_year integer not null,
                      publisher varchar(255),
                      title varchar(255),
                      book_status varchar(255),
                      type_code integer not null,
                      primary key (book_id)
) engine=InnoDB default charset=utf8;

create table book_reviews (
                              book_review_id bigint not null auto_increment,
                              created_at datetime(6),
                              last_modified_at datetime(6),
                              rate integer not null,
                              review_content varchar(255) not null,
                              review_title varchar(255) not null,
                              book_id bigint,
                              member_id bigint,
                              primary key (book_review_id)
) engine=InnoDB default charset=utf8;

create table management_request (
                                    management_request_id bigint not null auto_increment,
                                    created_at datetime(6),
                                    last_modified_at datetime(6),
                                    content varchar(255) not null,
                                    request_status varchar(255),
                                    title varchar(255) not null,
                                    member_id bigint,
                                    primary key (management_request_id)
) engine=InnoDB default charset=utf8;

create table management_request_result (
                                           management_reqeust_result_id bigint not null auto_increment,
                                           created_at datetime(6),
                                           last_modified_at datetime(6),
                                           result varchar(255),
                                           result_post_content varchar(255) not null,
                                           result_post_title varchar(255) not null,
                                           administrator_id bigint,
                                           management_request_id bigint,
                                           primary key (management_reqeust_result_id)
) engine=InnoDB default charset=utf8;

create table member (
                        member_id bigint not null auto_increment,
                        created_at datetime(6),
                        last_modified_at datetime(6),
                        city varchar(255),
                        legion varchar(255),
                        street varchar(255),
                        authority varchar(255),
                        birthday_code varchar(255) not null,
                        member_code varchar(255) not null,
                        name varchar(255) not null,
                        password varchar(255) not null,
                        primary key (member_id)
) engine=InnoDB default charset=utf8;

create table new_book_request (
                                  new_book_request_id bigint not null auto_increment,
                                  created_at datetime(6),
                                  last_modified_at datetime(6),
                                  request_book_title varchar(255) not null,
                                  request_content varchar(255) not null,
                                  request_status varchar(255),
                                  member_id bigint,
                                  primary key (new_book_request_id)
) engine=InnoDB default charset=utf8;

create table new_book_request_result (
                                         new_book_request_result_id bigint not null auto_increment,
                                         created_at datetime(6),
                                         last_modified_at datetime(6),
                                         result varchar(255),
                                         result_post_content varchar(255) not null,
                                         result_post_title varchar(255) not null,
                                         administrator_id bigint,
                                         new_book_request_id bigint,
                                         primary key (new_book_request_result_id)
) engine=InnoDB default charset=utf8;

create table rentals (
                         loan_id bigint not null auto_increment,
                         created_at datetime(6),
                         last_modified_at datetime(6),
                         extend_status varchar(255),
                         rental_end_date date not null,
                         rental_start_date date not null,
                         rental_status varchar(255),
                         book_id bigint,
                         member_id bigint,
                         primary key (loan_id)
) engine=InnoDB charset=utf8;

alter table member
    add constraint UK_hr7uduf2w8ho5jymgu9brrjq4 unique (member_code);

alter table book_reviews
    add constraint FKql32utr9nxcui2rgpyiucbrb
        foreign key (book_id)
            references book (book_id)
            on delete cascade;

alter table book_reviews
    add constraint FK7e6f1qedokghqrlx43unrgvar
        foreign key (member_id)
            references member (member_id);

alter table management_request
    add constraint FKc8bb8fhevqd509ixbbkhukqhq
        foreign key (member_id)
            references member (member_id);

alter table management_request_result
    add constraint FKbkuwo62px7pwhw86n944agj6j
        foreign key (administrator_id)
            references administrator (administrator_id);

alter table management_request_result
    add constraint FK6fuokgx5dlu1jycp74c50pep8
        foreign key (management_request_id)
            references management_request (management_request_id);

alter table new_book_request
    add constraint FKaeii4n40xiqckgohusieqgyf5
        foreign key (member_id)
            references member (member_id);

alter table new_book_request_result
    add constraint FKr51ah5yv448c03c67iq2kxy8r
        foreign key (administrator_id)
            references administrator (administrator_id);

alter table new_book_request_result
    add constraint FKmcq31m4hrtl0h5fh9oxwylnob
        foreign key (new_book_request_id)
            references new_book_request (new_book_request_id);

alter table rentals
    add constraint FKbl2au19b11j5m3odo0xomsyuu
        foreign key (book_id)
            references book (book_id);

alter table rentals
    add constraint FKkg4yqpdh1l98sndvxsbcoyl2u
        foreign key (member_id)
            references member (member_id);
