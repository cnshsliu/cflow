-- mysql dump 10.13  distrib 5.1.63, for win64 (unknown)
--
-- host: localhost    database: liukehong
-- ------------------------------------------------------
-- server version	5.1.63-community

/*!40101 set @old_character_set_client=@@character_set_client */;
/*!40101 set @old_character_set_results=@@character_set_results */;
/*!40101 set @old_collation_connection=@@collation_connection */;
/*!40101 set names utf8 */;
/*!40103 set @old_time_zone=@@time_zone */;
/*!40103 set time_zone='+00:00' */;
/*!40014 set @old_unique_checks=@@unique_checks, unique_checks=0 */;
/*!40014 set @old_foreign_key_checks=@@foreign_key_checks, foreign_key_checks=0 */;
/*!40101 set @old_sql_mode=@@sql_mode, sql_mode='no_auto_value_on_zero' */;
/*!40111 set @old_sql_notes=@@sql_notes, sql_notes=0 */;

--
-- table structure for table `qrtz_blob_triggers`
--

drop table if exists `qrtz_blob_triggers`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_blob_triggers` (
  `trigger_name` varchar(200) not null,
  `trigger_group` varchar(200) not null,
  `blob_data` blob,
  primary key (`trigger_name`,`trigger_group`),
  key `trigger_name` (`trigger_name`,`trigger_group`),
  constraint `qrtz_blob_triggers_ibfk_1` foreign key (`trigger_name`, `trigger_group`) references `qrtz_triggers` (`trigger_name`, `trigger_group`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_blob_triggers`
--

lock tables `qrtz_blob_triggers` write;
/*!40000 alter table `qrtz_blob_triggers` disable keys */;
/*!40000 alter table `qrtz_blob_triggers` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_calendars`
--

drop table if exists `qrtz_calendars`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_calendars` (
  `calendar_name` varchar(200) not null,
  `calendar` blob not null,
  primary key (`calendar_name`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_calendars`
--

lock tables `qrtz_calendars` write;
/*!40000 alter table `qrtz_calendars` disable keys */;
/*!40000 alter table `qrtz_calendars` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_cron_triggers`
--

drop table if exists `qrtz_cron_triggers`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_cron_triggers` (
  `trigger_name` varchar(200) not null,
  `trigger_group` varchar(200) not null,
  `cron_expression` varchar(120) not null,
  `time_zone_id` varchar(80) default null,
  primary key (`trigger_name`,`trigger_group`),
  key `trigger_name` (`trigger_name`,`trigger_group`),
  constraint `qrtz_cron_triggers_ibfk_1` foreign key (`trigger_name`, `trigger_group`) references `qrtz_triggers` (`trigger_name`, `trigger_group`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_cron_triggers`
--

lock tables `qrtz_cron_triggers` write;
/*!40000 alter table `qrtz_cron_triggers` disable keys */;
/*!40000 alter table `qrtz_cron_triggers` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_fired_triggers`
--

drop table if exists `qrtz_fired_triggers`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_fired_triggers` (
  `entry_id` varchar(95) not null,
  `trigger_name` varchar(200) not null,
  `trigger_group` varchar(200) not null,
  `is_volatile` varchar(1) not null,
  `instance_name` varchar(200) not null,
  `fired_time` bigint(13) not null,
  `priority` int(11) not null,
  `state` varchar(16) not null,
  `job_name` varchar(200) default null,
  `job_group` varchar(200) default null,
  `is_stateful` varchar(1) default null,
  `requests_recovery` varchar(1) default null,
  primary key (`entry_id`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_fired_triggers`
--

lock tables `qrtz_fired_triggers` write;
/*!40000 alter table `qrtz_fired_triggers` disable keys */;
/*!40000 alter table `qrtz_fired_triggers` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_job_details`
--

drop table if exists `qrtz_job_details`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_job_details` (
  `job_name` varchar(200) not null,
  `job_group` varchar(200) not null,
  `description` varchar(250) default null,
  `job_class_name` varchar(250) not null,
  `is_durable` varchar(1) not null,
  `is_volatile` varchar(1) not null,
  `is_stateful` varchar(1) not null,
  `requests_recovery` varchar(1) not null,
  `job_data` blob,
  primary key (`job_name`,`job_group`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_job_details`
--

lock tables `qrtz_job_details` write;
/*!40000 alter table `qrtz_job_details` disable keys */;
/*!40000 alter table `qrtz_job_details` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_job_listeners`
--

drop table if exists `qrtz_job_listeners`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_job_listeners` (
  `job_name` varchar(200) not null,
  `job_group` varchar(200) not null,
  `job_listener` varchar(200) not null,
  primary key (`job_name`,`job_group`,`job_listener`),
  key `job_name` (`job_name`,`job_group`),
  constraint `qrtz_job_listeners_ibfk_1` foreign key (`job_name`, `job_group`) references `qrtz_job_details` (`job_name`, `job_group`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_job_listeners`
--

lock tables `qrtz_job_listeners` write;
/*!40000 alter table `qrtz_job_listeners` disable keys */;
/*!40000 alter table `qrtz_job_listeners` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_locks`
--

drop table if exists `qrtz_locks`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_locks` (
  `lock_name` varchar(40) not null,
  primary key (`lock_name`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_locks`
--

lock tables `qrtz_locks` write;
/*!40000 alter table `qrtz_locks` disable keys */;
insert into `qrtz_locks` values ('calendar_access'),('job_access'),('misfire_access'),('state_access'),('trigger_access');
/*!40000 alter table `qrtz_locks` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_paused_trigger_grps`
--

drop table if exists `qrtz_paused_trigger_grps`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_paused_trigger_grps` (
  `trigger_group` varchar(200) not null,
  primary key (`trigger_group`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_paused_trigger_grps`
--

lock tables `qrtz_paused_trigger_grps` write;
/*!40000 alter table `qrtz_paused_trigger_grps` disable keys */;
/*!40000 alter table `qrtz_paused_trigger_grps` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_scheduler_state`
--

drop table if exists `qrtz_scheduler_state`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_scheduler_state` (
  `instance_name` varchar(200) not null,
  `last_checkin_time` bigint(13) not null,
  `checkin_interval` bigint(13) not null,
  primary key (`instance_name`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_scheduler_state`
--

lock tables `qrtz_scheduler_state` write;
/*!40000 alter table `qrtz_scheduler_state` disable keys */;
/*!40000 alter table `qrtz_scheduler_state` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_simple_triggers`
--

drop table if exists `qrtz_simple_triggers`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_simple_triggers` (
  `trigger_name` varchar(200) not null,
  `trigger_group` varchar(200) not null,
  `repeat_count` bigint(7) not null,
  `repeat_interval` bigint(12) not null,
  `times_triggered` bigint(10) not null,
  primary key (`trigger_name`,`trigger_group`),
  key `trigger_name` (`trigger_name`,`trigger_group`),
  constraint `qrtz_simple_triggers_ibfk_1` foreign key (`trigger_name`, `trigger_group`) references `qrtz_triggers` (`trigger_name`, `trigger_group`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_simple_triggers`
--

lock tables `qrtz_simple_triggers` write;
/*!40000 alter table `qrtz_simple_triggers` disable keys */;
/*!40000 alter table `qrtz_simple_triggers` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_triggers`
--

drop table if exists `qrtz_triggers`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_triggers` (
  `trigger_name` varchar(200) not null,
  `trigger_group` varchar(200) not null,
  `job_name` varchar(200) not null,
  `job_group` varchar(200) not null,
  `is_volatile` varchar(1) not null,
  `description` varchar(250) default null,
  `next_fire_time` bigint(13) default null,
  `prev_fire_time` bigint(13) default null,
  `priority` int(11) default null,
  `trigger_state` varchar(16) not null,
  `trigger_type` varchar(8) not null,
  `start_time` bigint(13) not null,
  `end_time` bigint(13) default null,
  `calendar_name` varchar(200) default null,
  `misfire_instr` smallint(2) default null,
  `job_data` blob,
  primary key (`trigger_name`,`trigger_group`),
  key `job_name` (`job_name`,`job_group`),
  constraint `qrtz_triggers_ibfk_1` foreign key (`job_name`, `job_group`) references `qrtz_job_details` (`job_name`, `job_group`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_triggers`
--

lock tables `qrtz_triggers` write;
/*!40000 alter table `qrtz_triggers` disable keys */;
/*!40000 alter table `qrtz_triggers` enable keys */;
unlock tables;

--
-- table structure for table `qrtz_trigger_listeners`
--

drop table if exists `qrtz_trigger_listeners`;
/*!40101 set @saved_cs_client     = @@character_set_client */;
/*!40101 set character_set_client = utf8 */;
create table `qrtz_trigger_listeners` (
  `trigger_name` varchar(200) not null,
  `trigger_group` varchar(200) not null,
  `trigger_listener` varchar(200) not null,
  primary key (`trigger_name`,`trigger_group`,`trigger_listener`),
  key `trigger_name` (`trigger_name`,`trigger_group`),
  constraint `qrtz_trigger_listeners_ibfk_1` foreign key (`trigger_name`, `trigger_group`) references `qrtz_triggers` (`trigger_name`, `trigger_group`)
) engine=innodb default charset=latin1;
/*!40101 set character_set_client = @saved_cs_client */;

--
-- dumping data for table `qrtz_trigger_listeners`
--

lock tables `qrtz_trigger_listeners` write;
/*!40000 alter table `qrtz_trigger_listeners` disable keys */;
/*!40000 alter table `qrtz_trigger_listeners` enable keys */;
unlock tables;



/*!40101 set sql_mode=@old_sql_mode */;
/*!40014 set foreign_key_checks=@old_foreign_key_checks */;
/*!40014 set unique_checks=@old_unique_checks */;
/*!40101 set character_set_client=@old_character_set_client */;
/*!40101 set character_set_results=@old_character_set_results */;
/*!40101 set collation_connection=@old_collation_connection */;
/*!40111 set sql_notes=@old_sql_notes */;

-- dump completed on 2012-11-12  0:31:11
