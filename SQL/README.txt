
$mysql -u root 
mysql> CREATE DATABASE DatabaseName;
mysql> create user USERNAME identified by 'PASSWORD';
mysql> grant all privileges on DatabaseName.* to USERNAME@localhost ;


������̵� ERROR 1005 (HY000) : Can't create table (errno: 121)
�ȳ����� 
SET FOREIGN_KEY_CHECKS = 0;
��ɺ�Ļ�ȥ
SET FOREIGN_KEY_CHECKS = 1;
���޷������
��ִ�� mysqladmin drop DatabaseName, then re-create it.

