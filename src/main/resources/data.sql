insert into user (user_id, password, name, email) values ('admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin','kimmin7932@gmail.com');

insert into authority (authority_name) values ('ROLE_USER');
insert into authority (authority_name) values ('ROLE_ADMIN');

insert into user_authority (user_id, authority_name) values ('admin', 'ROLE_USER');
insert into user_authority (user_id, authority_name) values ('admin', 'ROLE_ADMIN');