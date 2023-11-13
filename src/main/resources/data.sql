insert into user (user_id, password, name, email) values ('admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin','kimmin7932@gmail.com');

insert into authority (authority_name) values ('ROLE_USER');
insert into authority (authority_name) values ('ROLE_ADMIN');

insert into user_authority (user_id, authority_name) values ('admin', 'ROLE_USER');
insert into user_authority (user_id, authority_name) values ('admin', 'ROLE_ADMIN');


insert into goal (goal_id, goal_title) values (1,'빈곤 종식');
insert into goal (goal_id, goal_title) values (2,'기아 종식');
insert into goal (goal_id, goal_title) values (3,'건강과 웰빙');
insert into goal (goal_id, goal_title) values (4,'양질의 교육');
insert into goal (goal_id, goal_title) values (5,'성평등');
insert into goal (goal_id, goal_title) values (6,'깨끗한 물과 위생');
insert into goal (goal_id, goal_title) values (7,'지속 가능한 에너지');
insert into goal (goal_id, goal_title) values (8,'양질의 일자리와 경제성장');
insert into goal (goal_id, goal_title) values (9,'혁신과 인프라 구축');
insert into goal (goal_id, goal_title) values (10,'불평등 완화');
insert into goal (goal_id, goal_title) values (11,'지속 가능한 도시 및 거주지 조성');
insert into goal (goal_id, goal_title) values (12,'책임 있는 소비와 생산');
insert into goal (goal_id, goal_title) values (13,'기후행동');
insert into goal (goal_id, goal_title) values (14,'해양 생태계 보호');
insert into goal (goal_id, goal_title) values (15,'육상 생태계 보호');
insert into goal (goal_id, goal_title) values (16,'평화 및 제도 구축');
insert into goal (goal_id, goal_title) values (17,'목표 달성을 위한 파트너십');
