
$mysql -u root 
mysql> CREATE DATABASE DatabaseName;
mysql> create user USERNAME identified by 'PASSWORD';
mysql> grant all privileges on DatabaseName.* to USERNAME@localhost ;


如遇顽固的 ERROR 1005 (HY000) : Can't create table (errno: 121)
先尝试用 
SET FOREIGN_KEY_CHECKS = 0;
完成后改回去
SET FOREIGN_KEY_CHECKS = 1;
如无法解决，
请执行 mysqladmin drop DatabaseName, then re-create it.

