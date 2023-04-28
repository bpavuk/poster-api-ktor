create table if not exists USERS (
    id int not null auto_increment,
    username varchar(45) not null,
    profile_img varchar(100) not null,
    primary key (id)
);