create table if not exists POSTS (
                       ID int not null auto_increment,
                       DESCRIPTION varchar(1000) not null,
                       PHOTOS_LIST text not null,
                       AUTHOR int not null,
                       foreign key (AUTHOR) REFERENCES USERS(id)
)