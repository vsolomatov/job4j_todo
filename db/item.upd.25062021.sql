alter table item add column user_id int;
--update item set user_id = 1;
--commit;

alter table item add constraint item_user FOREIGN KEY(user_id) references j_user(id);