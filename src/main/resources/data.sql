insert into role(name) values('User');
insert into role(name) values('Administrator');

insert into users(username,password) values('user', 'user');
insert into users(username,password) values('admin', 'admin');

insert into users_roles(roles_id, user_id) values(1, 1);
insert into users_roles(roles_id, user_id) values(1, 2);
insert into users_roles(roles_id, user_id) values(2, 2);

insert into price(amount, currency, deleted) values(1.49, 'EUR', false);
insert into price(amount, currency, deleted) values(2.49, 'EUR', false);
insert into price(amount, currency, deleted) values(3.49, 'EUR', false);

insert into product(name, price_id, deleted) values('One', 1, false);
insert into product(name, price_id, deleted) values('Two', 2, false);
insert into product(name, price_id, deleted) values('Three', 3, false);