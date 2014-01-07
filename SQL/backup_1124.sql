-- MySQL dump 10.13  Distrib 5.1.63, for Win32 (ia32)
--
-- Host: localhost    Database: liukehong
-- ------------------------------------------------------
-- Server version	5.1.63-community

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `QRTZ_BLOB_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_BLOB_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_BLOB_TRIGGERS` (
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_BLOB_TRIGGERS`
--

LOCK TABLES `QRTZ_BLOB_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_BLOB_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_BLOB_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_CALENDARS`
--

DROP TABLE IF EXISTS `QRTZ_CALENDARS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_CALENDARS` (
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_CALENDARS`
--

LOCK TABLES `QRTZ_CALENDARS` WRITE;
/*!40000 ALTER TABLE `QRTZ_CALENDARS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_CALENDARS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_CRON_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_CRON_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_CRON_TRIGGERS` (
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(120) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_CRON_TRIGGERS`
--

LOCK TABLES `QRTZ_CRON_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_CRON_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_CRON_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_FIRED_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_FIRED_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_FIRED_TRIGGERS` (
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_STATEFUL` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`ENTRY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_FIRED_TRIGGERS`
--

LOCK TABLES `QRTZ_FIRED_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_FIRED_TRIGGERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_FIRED_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_JOB_DETAILS`
--

DROP TABLE IF EXISTS `QRTZ_JOB_DETAILS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_JOB_DETAILS` (
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `IS_STATEFUL` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`JOB_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_JOB_DETAILS`
--

LOCK TABLES `QRTZ_JOB_DETAILS` WRITE;
/*!40000 ALTER TABLE `QRTZ_JOB_DETAILS` DISABLE KEYS */;
INSERT INTO `QRTZ_JOB_DETAILS` VALUES ('CFTimerJob','CFTimer',NULL,'com.lkh.cflow.job.CFTimerJob','0','0','0','0','¨Ì\0sr\0org.quartz.JobDataMapü∞ÉËø©∞À\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMapÇË√˚≈](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMapÊ.≠(v\nŒ\0Z\0dirtyL\0mapt\0Ljava/util/Map;xp\0sr\0java.util.HashMap⁄¡√`—\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0\0x\0');
/*!40000 ALTER TABLE `QRTZ_JOB_DETAILS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_JOB_LISTENERS`
--

DROP TABLE IF EXISTS `QRTZ_JOB_LISTENERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_JOB_LISTENERS` (
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `JOB_LISTENER` varchar(200) NOT NULL,
  PRIMARY KEY (`JOB_NAME`,`JOB_GROUP`,`JOB_LISTENER`),
  KEY `JOB_NAME` (`JOB_NAME`,`JOB_GROUP`),
  CONSTRAINT `QRTZ_JOB_LISTENERS_ibfk_1` FOREIGN KEY (`JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_JOB_LISTENERS`
--

LOCK TABLES `QRTZ_JOB_LISTENERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_JOB_LISTENERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_JOB_LISTENERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_LOCKS`
--

DROP TABLE IF EXISTS `QRTZ_LOCKS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_LOCKS` (
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_LOCKS`
--

LOCK TABLES `QRTZ_LOCKS` WRITE;
/*!40000 ALTER TABLE `QRTZ_LOCKS` DISABLE KEYS */;
INSERT INTO `QRTZ_LOCKS` VALUES ('CALENDAR_ACCESS'),('JOB_ACCESS'),('MISFIRE_ACCESS'),('STATE_ACCESS'),('TRIGGER_ACCESS');
/*!40000 ALTER TABLE `QRTZ_LOCKS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

DROP TABLE IF EXISTS `QRTZ_PAUSED_TRIGGER_GRPS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_PAUSED_TRIGGER_GRPS` (
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_PAUSED_TRIGGER_GRPS`
--

LOCK TABLES `QRTZ_PAUSED_TRIGGER_GRPS` WRITE;
/*!40000 ALTER TABLE `QRTZ_PAUSED_TRIGGER_GRPS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_PAUSED_TRIGGER_GRPS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SCHEDULER_STATE`
--

DROP TABLE IF EXISTS `QRTZ_SCHEDULER_STATE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_SCHEDULER_STATE` (
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SCHEDULER_STATE`
--

LOCK TABLES `QRTZ_SCHEDULER_STATE` WRITE;
/*!40000 ALTER TABLE `QRTZ_SCHEDULER_STATE` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_SCHEDULER_STATE` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_SIMPLE_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_SIMPLE_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_SIMPLE_TRIGGERS` (
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SIMPLE_TRIGGERS`
--

LOCK TABLES `QRTZ_SIMPLE_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_SIMPLE_TRIGGERS` DISABLE KEYS */;
INSERT INTO `QRTZ_SIMPLE_TRIGGERS` VALUES ('trigger1','CFTimer',-1,60000,3);
/*!40000 ALTER TABLE `QRTZ_SIMPLE_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_TRIGGERS`
--

DROP TABLE IF EXISTS `QRTZ_TRIGGERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_TRIGGERS` (
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `IS_VOLATILE` varchar(1) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `JOB_NAME` (`JOB_NAME`,`JOB_GROUP`),
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_TRIGGERS`
--

LOCK TABLES `QRTZ_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_TRIGGERS` DISABLE KEYS */;
INSERT INTO `QRTZ_TRIGGERS` VALUES ('trigger1','CFTimer','CFTimerJob','CFTimer','0',NULL,1353765052909,1353764992909,5,'WAITING','SIMPLE',1353764872909,0,NULL,0,'');
/*!40000 ALTER TABLE `QRTZ_TRIGGERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `QRTZ_TRIGGER_LISTENERS`
--

DROP TABLE IF EXISTS `QRTZ_TRIGGER_LISTENERS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QRTZ_TRIGGER_LISTENERS` (
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `TRIGGER_LISTENER` varchar(200) NOT NULL,
  PRIMARY KEY (`TRIGGER_NAME`,`TRIGGER_GROUP`,`TRIGGER_LISTENER`),
  KEY `TRIGGER_NAME` (`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `QRTZ_TRIGGER_LISTENERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_TRIGGER_LISTENERS`
--

LOCK TABLES `QRTZ_TRIGGER_LISTENERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_TRIGGER_LISTENERS` DISABLE KEYS */;
/*!40000 ALTER TABLE `QRTZ_TRIGGER_LISTENERS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_delegate`
--

DROP TABLE IF EXISTS `cf_delegate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_delegate` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `DEV` varchar(24) NOT NULL,
  `DELEGATER` varchar(40) DEFAULT NULL,
  `DELEGATEE` varchar(40) DEFAULT NULL,
  `STARTTIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ENDTIME` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `PRCID` varchar(40) DEFAULT NULL,
  `NODEID` varchar(40) DEFAULT NULL,
  `SESSID` varchar(40) DEFAULT NULL,
  `DELEGATETYPE` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDXDELEGATE` (`PRCID`,`NODEID`,`DELEGATETYPE`),
  KEY `DELEGATER` (`DELEGATER`),
  KEY `DELEGATEE` (`DELEGATEE`),
  KEY `DEV` (`DEV`),
  CONSTRAINT `CFDELEGATE_ibfk_3` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_delegate`
--

LOCK TABLES `cf_delegate` WRITE;
/*!40000 ALTER TABLE `cf_delegate` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_delegate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_dev`
--

DROP TABLE IF EXISTS `cf_dev`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_dev` (
  `ID` varchar(24) NOT NULL,
  `NAME` varchar(40) DEFAULT NULL,
  `PASSWORD` varchar(40) DEFAULT NULL,
  `EMAIL` varchar(50) DEFAULT NULL,
  `TZID` char(9) NOT NULL DEFAULT 'GMT+00:00',
  `LANG` varchar(8) NOT NULL DEFAULT 'en-US',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_dev`
--

LOCK TABLES `cf_dev` WRITE;
/*!40000 ALTER TABLE `cf_dev` DISABLE KEYS */;
INSERT INTO `cf_dev` VALUES ('abcdefghijklmnopqrstuvwx','name','name','name','GMT+00:00','en-US'),('aliyun','Aliyun','2cbeb596935e23838917c8e8c6595f21','aliyun@null.com','GMT+08:00','zh-CN'),('tester1','tester1','098f6bcd4621d373cade4e832627b4f6','test@null.com','GMT+08:00','zh-CN'),('tester2','Tester','098f6bcd4621d373cade4e832627b4f6','tester@null.com','GMT+08:00','zh-CN');
/*!40000 ALTER TABLE `cf_dev` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_frontdesk_dt`
--

DROP TABLE IF EXISTS `cf_frontdesk_dt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_frontdesk_dt` (
  `ID` varchar(40) NOT NULL,
  `DOER` varchar(40) NOT NULL,
  `PRCID` varchar(40) NOT NULL,
  `NODEID` varchar(24) NOT NULL,
  `SESSID` varchar(40) NOT NULL,
  `OPTIONPICKED` varchar(40) NOT NULL,
  `TASKINPUT` varchar(20000) DEFAULT NULL,
  `TZID` char(9) NOT NULL DEFAULT 'GMT+00:00',
  `RECEIVEDAT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DELIVERED` tinyint(1) NOT NULL DEFAULT '0',
  `DELIVEREDAT` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `DAEMON` varchar(40) NOT NULL,
  `SUCCESS` tinyint(1) NOT NULL DEFAULT '0',
  `MSG` varchar(400) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_frontdesk_dt`
--

LOCK TABLES `cf_frontdesk_dt` WRITE;
/*!40000 ALTER TABLE `cf_frontdesk_dt` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_frontdesk_dt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_frontdesk_sw`
--

DROP TABLE IF EXISTS `cf_frontdesk_sw`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_frontdesk_sw` (
  `ID` varchar(40) NOT NULL,
  `PRCID` varchar(40) NOT NULL,
  `DEVID` varchar(24) NOT NULL,
  `WFTID` varchar(40) NOT NULL,
  `TEAMID` varchar(40) NOT NULL,
  `STARTBY` varchar(40) NOT NULL,
  `INSTANCENAME` varchar(200) DEFAULT NULL,
  `CTX` varchar(20000) DEFAULT NULL,
  `PARENTPROCESSID` varchar(40) NOT NULL,
  `PARENTPROCESSNODEID` varchar(40) NOT NULL,
  `PARENTPROCESSSESSID` varchar(40) NOT NULL,
  `TZID` char(9) NOT NULL DEFAULT 'GMT+00:00',
  `RECEIVEDAT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DELIVERED` tinyint(1) NOT NULL DEFAULT '0',
  `DELIVEREDAT` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `DAEMON` varchar(40) NOT NULL,
  `SUCCESS` tinyint(1) NOT NULL DEFAULT '0',
  `MSG` varchar(400) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_frontdesk_sw`
--

LOCK TABLES `cf_frontdesk_sw` WRITE;
/*!40000 ALTER TABLE `cf_frontdesk_sw` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_frontdesk_sw` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_limit`
--

DROP TABLE IF EXISTS `cf_limit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_limit` (
  `DEV` varchar(24) NOT NULL,
  `PRCLIMIT` int(11) DEFAULT '0',
  `WFTLIMIT` int(11) DEFAULT '0',
  KEY `DEV` (`DEV`),
  CONSTRAINT `cf_limit_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_limit`
--

LOCK TABLES `cf_limit` WRITE;
/*!40000 ALTER TABLE `cf_limit` DISABLE KEYS */;
INSERT INTO `cf_limit` VALUES ('ORACL',49,98),('PASS8TSAAS',122,100);
/*!40000 ALTER TABLE `cf_limit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_process`
--

DROP TABLE IF EXISTS `cf_process`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_process` (
  `DEV` varchar(24) NOT NULL,
  `ID` varchar(40) NOT NULL,
  `STARTBY` varchar(40) DEFAULT NULL,
  `STARTAT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ENDAT` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `STATUS` varchar(15) DEFAULT NULL,
  `WFTNAME` varchar(40) DEFAULT NULL,
  `WFTID` varchar(40) DEFAULT NULL,
  `PPID` varchar(40) DEFAULT NULL,
  `PPNODEID` varchar(40) DEFAULT NULL,
  `PPSESSID` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `DEV` (`DEV`),
  KEY `CFPROCESS_ibfk_2` (`PPID`),
  CONSTRAINT `CFPROCESS_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `CFPROCESS_ibfk_2` FOREIGN KEY (`PPID`) REFERENCES `cf_process` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_process`
--

LOCK TABLES `cf_process` WRITE;
/*!40000 ALTER TABLE `cf_process` DISABLE KEYS */;
INSERT INTO `cf_process` VALUES ('aliyun','01ec442022084765b1fc42454664dada','aliyun','2012-11-19 12:38:57','2012-11-19 12:41:04','finished','dfasf','6ced6bfac8c74e3d821fb65cff435f73',NULL,NULL,NULL),('aliyun','033de3282d7848049d2b9dce0dd0065b','aliyun','2012-11-19 10:29:47','2012-11-19 10:31:55','finished','ddd','9b2ec3471f89492b9393b5e9144144d1',NULL,NULL,NULL),('aliyun','036d5c2134f04f0590b489ad44e3d123','aliyun','2012-11-19 13:44:46','2012-11-19 14:56:29','finished','dddd','6ced6bfac8c74e3d821fb65cff435f73',NULL,NULL,NULL),('aliyun','03ec883e157c4dd49bbfd1cdfa951515','aliyun','2012-11-19 11:00:37','2012-11-19 11:36:19','canceled','fadfsf','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','06c7d191f2db4d83a6bfcb17f18769d5','peixin.baipx','2012-11-20 15:24:56','2012-11-20 15:24:56','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','08266cb080654f3b80bfcc36e4705342','aliyun','2012-11-19 14:27:11','2012-11-19 14:27:11','running','ddd','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','0e51377e85934014aac8819a0fb64b7b','aliyun','2012-11-19 10:36:14','2012-11-19 11:36:24','canceled','dddd','9b2ec3471f89492b9393b5e9144144d1',NULL,NULL,NULL),('aliyun','103c80b5d346485f80c61efbb5694209','aliyun','2012-11-19 14:12:42','2012-11-19 14:55:47','finished','dddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','123ca01197f7415f97d0c1220b61352f','aliyun','2012-11-19 11:03:44','2012-11-19 11:09:28','finished','dddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','124a7f224ee74f96be2dbb535a9342ac','aliyun','2012-11-19 10:28:49','2012-11-19 10:29:14','finished','aaa','9b2ec3471f89492b9393b5e9144144d1',NULL,NULL,NULL),('aliyun','16fa77642c6d4661bcfcf46debc7f712','aliyun','2012-11-13 07:20:30','2012-11-13 09:05:32','finished','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','18eb513dbb944172bae395384a74023d','aliyun','2012-11-19 12:03:35','2012-11-19 12:34:13','finished','dfssaf','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','1dd61c82071c4ab68264c884fbdef26a','aliyun','2012-11-19 10:56:15','2012-11-19 10:58:27','finished','ddd','9b2ec3471f89492b9393b5e9144144d1',NULL,NULL,NULL),('aliyun','1e764a57fee94ffaa726e2fd3e612ccc','aliyun','2012-11-19 14:51:51','2012-11-19 14:55:37','finished','dddd','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','20054e029d3e4139a3b42e747c1fee28','aliyun','2012-11-19 09:44:24','2012-11-19 09:44:41','canceled','eeeee','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','21ebcef12a014146bb32c4d7d3ef7acb','peixin.baipx','2012-11-20 15:08:02','2012-11-20 15:08:02','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','238aa8f9722b40af8518c34eaa0ab583','aliyun','2012-11-19 11:42:06','2012-11-19 11:43:21','finished','ddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','2ded288d559f4dd89234daf7d1e6fc8f','peixin.baipx','2012-11-20 14:42:28','2012-11-20 14:42:28','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','3a4f28eb69084d8d92e13a4baebc0794','aliyun','2012-11-19 11:34:40','2012-11-19 11:34:58','finished','dddd','6ced6bfac8c74e3d821fb65cff435f73',NULL,NULL,NULL),('aliyun','3cb3c53c9fc24882b0fbf3df25c3ee54','aliyun','2012-11-19 14:57:35','2012-11-19 14:58:39','finished','ddd','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','3db4e1781be748e5940c02a6b8cc6405','peixin.baipx','2012-11-20 15:02:42','2012-11-20 15:02:42','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','424fe4a0066243e4bb27d0634ed83753','aliyun','2012-11-19 09:45:45','2012-11-19 09:46:20','finished','sj4','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','45df163f0ae642dea10976a5c5d4fb46','aliyun','2012-11-19 10:16:26','2012-11-19 10:16:56','finished','tst1','9b2ec3471f89492b9393b5e9144144d1',NULL,NULL,NULL),('aliyun','4bdb9fe01a8c4effb9a61a124b799c89','peixin.baipx','2012-11-20 16:24:27','2012-11-20 16:24:31','finished','v1','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','4bfd2e77319a43f283b73552d1e90770','aliyun','2012-11-13 09:41:03','2012-11-19 09:44:46','canceled','ddd','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','4fbd214b053347e89d631946b79b5d86','aliyun','2012-11-19 11:09:33','2012-11-19 11:32:05','finished','ddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','5534638cea6f4af38793f7d38065d225','aliyun','2012-11-19 13:46:40','2012-11-19 14:55:50','finished','ddddd','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','58723ec2443b4a42b430efb6b7182f78','aliyun','2012-11-19 09:48:27','2012-11-19 10:12:08','finished','sj5','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','59a2f7f8fd114538af97d4ad503084ce','aliyun','2012-11-19 11:37:06','2012-11-19 11:41:59','canceled','dddddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','64396aae8ebc4b649166016c0358c257','aliyun','2012-11-19 14:19:56','2012-11-19 14:55:44','finished','abcd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','66164ca5ebf44cdb8e1f09ca2f09c848','aliyun','2012-11-22 10:21:32','2012-11-22 10:22:12','finished','dddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','6868fade206e44bea5cca941b4bbd487','aliyun','2012-11-13 13:19:07','2012-11-13 13:39:23','finished','ffff','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','68bfe03b4ac142a48aab46b862b68b84','peixin.baipx','2012-11-20 15:27:13','2012-11-20 15:27:13','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','74d3d2c7850142f7b5f90bc81ffe10a8','peixin.baipx','2012-11-20 16:22:59','2012-11-20 16:23:04','finished','v1','ced045deab464fabab7d806b6137cc74',NULL,NULL,NULL),('aliyun','75f61b6b10ba458aa7afb9abf6eee5b6','peixin.baipx','2012-11-20 16:24:36','2012-11-20 16:24:43','finished','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','765f693427274f49977628218647c6e3','aliyun','2012-11-13 13:59:52','2012-11-19 09:44:56','canceled','ggg','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','766ea1ccc54042afb96bf5b2583f29f7','peixin.baipx','2012-11-20 16:22:57','2012-11-20 16:22:59','finished','v1','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','7a3414c5b374427b8f97fb204064afdd','peixin.baipx','2012-11-20 15:27:45','2012-11-20 15:28:32','canceled','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','7b8e3b131021449e97d3e75f4c64d023','aliyun','2012-11-19 11:35:25','2012-11-19 11:35:34','finished','dddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','7dd4705ba6db4c7cbc545b1c69b7be99','peixin.baipx','2012-11-20 15:01:05','2012-11-20 15:01:05','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','81fec3766ca24b3a998fb50810ef4228','aliyun','2012-11-19 11:53:59','2012-11-19 11:57:29','finished','dddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','8335d7a07061433389c682766099d583','peixin.baipx','2012-11-20 14:41:42','2012-11-20 14:41:42','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','8390c8f25df340a8bbdb7720b09b0c09','aliyun','2012-11-19 14:31:17','2012-11-19 14:31:17','running','ddd','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','8cf25d5cf1814e43b5209b15b38f0064','peixin.baipx','2012-11-20 16:10:21','2012-11-20 16:10:23','finished','v1','231066c8eaa24dfd826686a41c229fda',NULL,NULL,NULL),('aliyun','8f2ee7115c4c4423b45592a12346d6a0','aliyun','2012-11-19 11:59:46','2012-11-19 12:00:28','finished','dafsaf','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','902c57a2892747219b63b4854db777c8','aliyun','2012-11-19 12:01:18','2012-11-19 12:02:13','finished','adfafdas','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','9662ae59789b41e38b78f8e689fe4ef2','aliyun','2012-11-19 12:39:01','2012-11-19 12:55:58','finished','dfasf','6ced6bfac8c74e3d821fb65cff435f73',NULL,NULL,NULL),('aliyun','9b040e117bbb48eba1c428577e7f58cc','aliyun','2012-11-19 09:39:15','2012-11-19 09:43:14','finished','ddd','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','9b6a0fc726154a2fbddc76b167ed581f','peixin.baipx','2012-11-20 16:16:01','2012-11-20 16:16:03','finished','v1','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','9b8555deb3c2444b9c13e070ea01c663','aliyun','2012-11-19 11:33:48','2012-11-19 11:34:47','finished','È°∂È°∂È°∂È°∂','6ced6bfac8c74e3d821fb65cff435f73',NULL,NULL,NULL),('aliyun','9cfe997c741646668bf236dc381ea801','aliyun','2012-11-19 09:44:15','2012-11-19 09:44:36','canceled','eeeee','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','ab14d6f80024446680d451cba2394475','aliyun','2012-11-19 09:43:07','2012-11-19 09:43:49','finished','ddddd','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','af3a9a093ff84744885faf5ab87d266f','peixin.baipx','2012-11-20 15:01:47','2012-11-20 15:01:47','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','b03747cbeccd4bd4b72991c07c776168','aliyun','2012-11-19 15:15:58','2012-11-19 15:15:58','running','3333333333','8cbc25a397fa442d849a0d2d4a2f37fd',NULL,NULL,NULL),('aliyun','b5ab99dd92904876b34559cdaa68e66f','aliyun','2012-11-13 09:09:01','2012-11-13 09:59:46','finished','dddd','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','b61ca309939441b8ba60cc1a163c1275','peixin.baipx','2012-11-20 14:42:58','2012-11-20 14:42:58','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','b7a4dd6bf71e4c23846e6f6d5975809f','peixin.baipx','2012-11-20 16:24:32','2012-11-20 16:24:35','finished','v1','231066c8eaa24dfd826686a41c229fda',NULL,NULL,NULL),('aliyun','b8ddc9b3930c431ca0962ec4219a956a','aliyun','2012-11-19 14:21:25','2012-11-19 14:55:41','finished','abcdefg','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','c5b89a18d47b4389abfef0c8de07b9ef','aliyun','2012-11-19 11:32:11','2012-11-19 11:32:44','finished','dddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','c5de39ed810b48e0ad9a364907bfc255','aliyun','2012-11-19 12:56:14','2012-11-19 13:01:15','canceled','ddd','6ced6bfac8c74e3d821fb65cff435f73',NULL,NULL,NULL),('aliyun','c6f76b9a00934cdf9b4303a704277d89','aliyun','2012-11-19 13:01:22','2012-11-19 13:03:43','finished','ddd','6ced6bfac8c74e3d821fb65cff435f73',NULL,NULL,NULL),('aliyun','c73a38456ae64a878ca1f2d4d1a29d25','aliyun','2012-11-19 10:33:52','2012-11-19 10:35:17','finished','ddd','9b2ec3471f89492b9393b5e9144144d1',NULL,NULL,NULL),('aliyun','c99d7c7d77e7478fa7cdf3b27df02e44','aliyun','2012-11-19 09:32:12','2012-11-19 09:32:41','finished','ddd','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','c9f1adfcb5db494c8da8becbd001cc0f','peixin.baipx','2012-11-20 16:01:23','2012-11-20 16:03:20','finished','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','cbf44db790514373907f21089e7036f8','aliyun','2012-11-19 09:45:07','2012-11-19 09:45:26','finished','sj3','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','cd3fa673122f409dbc968b224e831869','aliyun','2012-11-19 11:46:33','2012-11-19 11:52:56','finished','eeee','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','d1a9cda42fe14e069b7bd5505c449e0f','aliyun','2012-11-19 15:17:46','2012-11-19 15:17:46','running','444444444','ced045deab464fabab7d806b6137cc74',NULL,NULL,NULL),('aliyun','d5d3f8cd76ac4d2c9b663832ed4509cc','aliyun','2012-11-19 10:57:21','2012-11-19 10:59:22','finished','dddd','9b2ec3471f89492b9393b5e9144144d1',NULL,NULL,NULL),('aliyun','d7a55b771b044dc4afb36fd6bac87931','aliyun','2012-11-19 14:59:01','2012-11-19 14:59:01','running','ddd','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','d9d61807108343aab2d6bbf9e4df4a46','peixin.baipx','2012-11-21 06:02:43','2012-11-21 06:02:43','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','de546908274244589e18e44d095d230a','aliyun','2012-11-13 10:07:15','2012-11-19 09:44:53','canceled','eeee','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','dfb03aedb9f84ebb8286929451c36e3a','aliyun','2012-11-19 12:38:39','2012-11-19 12:38:50','finished','ddd','6ced6bfac8c74e3d821fb65cff435f73',NULL,NULL,NULL),('aliyun','e00e8a148d7745259ac06394124382ba','aliyun','2012-11-19 13:46:10','2012-11-19 14:56:17','finished','ddd','6ced6bfac8c74e3d821fb65cff435f73',NULL,NULL,NULL),('aliyun','e1ba9788db9d45a7868a0c638391e7b0','aliyun','2012-11-19 11:15:26','2012-11-19 11:16:04','finished','dddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','e48eb056a5ed4adaa45a604d7b0af469','aliyun','2012-11-13 10:00:51','2012-11-13 10:06:56','finished','eeee','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','e4c657b20a9c4eabaae0489b0edbd01b','peixin.baipx','2012-11-20 15:00:31','2012-11-20 15:00:31','running','v1','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','e8c9e7366bf44fdf80d510d30d651da9','peixin.baipx','2012-11-20 16:24:20','2012-11-20 16:24:22','finished','v1','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','ed46ce596b6143569a754359a09b899c','aliyun','2012-11-19 09:19:35','2012-11-19 09:28:34','finished','ddddd','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','ee3c446417434004abcf365812537a34','aliyun','2012-11-19 11:43:55','2012-11-19 11:46:28','canceled','dddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','f04971fd252a4dc2b8c8cd1524d68c1b','aliyun','2012-11-19 11:53:01','2012-11-19 11:53:26','finished','ddeeee','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','f0e78d8186f840a7bbdedf6ec72aee13','aliyun','2012-11-19 14:30:09','2012-11-19 14:52:51','finished','h5','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','f18eb6166b224625aca9883daeb902ae','aliyun','2012-11-13 10:00:23','2012-11-19 09:44:50','canceled','ddd','3a347ab442164ccf88663ad4070acebf',NULL,NULL,NULL),('aliyun','f4322d3669fa41ab82d68bf302a15ea3','aliyun','2012-11-19 11:42:29','2012-11-19 11:43:00','finished','dddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','f5f9406d26f6437394c9c92821d2e7ec','aliyun','2012-11-22 10:22:31','2012-11-22 10:22:44','finished','sdfsafsafsf','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','f7d4a3988d0d447e8302ecfffcdc4a54','aliyun','2012-11-19 11:53:51','2012-11-19 13:01:11','canceled','efefa','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL),('aliyun','f7e5c9cdd5184a3cba279d297b80bffc','aliyun','2012-11-19 14:28:08','2012-11-19 14:52:48','finished','sh3','c509763360e84121b980f8f32bc1efec',NULL,NULL,NULL),('aliyun','f806eb7c8df74b3fba9da9b71d5dc0fb','peixin.baipx','2012-11-20 16:24:22','2012-11-20 16:24:26','finished','v1','ced045deab464fabab7d806b6137cc74',NULL,NULL,NULL),('aliyun','f970d7a9bdf9454fb583a5fcfeb64d9b','aliyun','2012-11-19 12:36:40','2012-11-19 12:37:56','finished','dddd','bd069315979d4eec95afa71919390f75',NULL,NULL,NULL);
/*!40000 ALTER TABLE `cf_process` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_sess`
--

DROP TABLE IF EXISTS `cf_sess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_sess` (
  `DEV` varchar(24) NOT NULL,
  `SESSKEY` varchar(40) DEFAULT NULL,
  `SESSTIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `IDXSESSION` (`SESSKEY`),
  KEY `cf_sess_ibfk_1` (`DEV`),
  CONSTRAINT `cf_sess_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_sess`
--

LOCK TABLES `cf_sess` WRITE;
/*!40000 ALTER TABLE `cf_sess` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_sess` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_task`
--

DROP TABLE IF EXISTS `cf_task`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_task` (
  `DEV` varchar(24) NOT NULL,
  `USRID` varchar(24) NOT NULL,
  `PRCID` varchar(40) NOT NULL,
  `NODEID` varchar(40) NOT NULL,
  `SESSID` varchar(40) NOT NULL,
  `WORKNAME` varchar(200) DEFAULT NULL,
  `STATUS` varchar(20) NOT NULL,
  `DELEGATERID` varchar(40) NOT NULL,
  `PRCSUS` varchar(3) DEFAULT 'NO',
  `WORKSUS` varchar(3) DEFAULT 'NO',
  `TID` varchar(40) DEFAULT NULL,
  `TS` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY `DEV` (`DEV`),
  KEY `USRID` (`USRID`),
  KEY `CFTASK_ibfk_2` (`PRCID`),
  CONSTRAINT `CFTASK_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `CFTASK_ibfk_2` FOREIGN KEY (`PRCID`) REFERENCES `cf_process` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_task`
--

LOCK TABLES `cf_task` WRITE;
/*!40000 ALTER TABLE `cf_task` DISABLE KEYS */;
INSERT INTO `cf_task` VALUES ('aliyun','aliyun','d7a55b771b044dc4afb36fd6bac87931','7DDD0926-3CD0-0545-946F-17D233523791','329f888db6a746ebb7dfdfda054b5579','ÂàÜÊûêÁªìÊûú','task_running','SYSTEM','NO','NO','ab54a0d3b8a24f20ac206e4b94230596','2012-11-19 14:59:37'),('aliyun','aliyun','b03747cbeccd4bd4b72991c07c776168','A5ECFB84-6135-58B4-C9C9-17BFADE759CE','1a6b29923a604522a04eb1419626872c','ÈÄâÊã©Ë¥≠‰π∞Êúà‰ªΩ','task_running','SYSTEM','NO','NO','a4fa5f969d2c4b56ab55f9d59e012cd9','2012-11-19 15:15:59'),('aliyun','aliyun','d1a9cda42fe14e069b7bd5505c449e0f','720C54BE-E2E0-15D5-692A-EB100B618B91','6bea9a2851794099a2dbc7663064a272','Error','task_running','SYSTEM','NO','NO','3a9c28ef83c7402c898fd241bf1a957c','2012-11-19 15:17:59'),('aliyun','peixin.baipx','8335d7a07061433389c682766099d583','F3518383-8E11-E6D2-A536-59F4CA5D114B','7a4128c836c44ac5a57e70434e1f48ba','Voucher Application','task_running','SYSTEM','NO','NO','0650354f71ef4024b36fab5089d71312','2012-11-20 14:41:44'),('aliyun','peixin.baipx','2ded288d559f4dd89234daf7d1e6fc8f','F3518383-8E11-E6D2-A536-59F4CA5D114B','7073e4f8d4fe4cafa28c2a06b8217e3c','Voucher Application','task_running','SYSTEM','NO','NO','03dd528147154036ba86784b87a18c65','2012-11-20 14:42:28'),('aliyun','peixin.baipx','b61ca309939441b8ba60cc1a163c1275','F3518383-8E11-E6D2-A536-59F4CA5D114B','2bbc5a9ad9a845e38d9296a06ce62b6d','Voucher Application','task_running','SYSTEM','NO','NO','0e091ab69c4643d2b9e8c354b6ad2aba','2012-11-20 14:42:58'),('aliyun','peixin.baipx','e4c657b20a9c4eabaae0489b0edbd01b','F3518383-8E11-E6D2-A536-59F4CA5D114B','5cc7bf8446f54edb82ce285f03a5f2a8','Voucher Application','task_running','SYSTEM','NO','NO','61ecac7cad114c03b45b1535bbe92117','2012-11-20 15:00:31'),('aliyun','peixin.baipx','7dd4705ba6db4c7cbc545b1c69b7be99','F3518383-8E11-E6D2-A536-59F4CA5D114B','3bd7c7d41e734608a6f7277373313291','Voucher Application','task_running','SYSTEM','NO','NO','ba0aa1b4b2984955b899fd46350e92ea','2012-11-20 15:01:05'),('aliyun','peixin.baipx','af3a9a093ff84744885faf5ab87d266f','F3518383-8E11-E6D2-A536-59F4CA5D114B','ee67d5edc51b4c27bce7327165a70a1c','Voucher Application','task_running','SYSTEM','NO','NO','f04012d731d0485984590aa80767a61d','2012-11-20 15:01:48'),('aliyun','peixin.baipx','3db4e1781be748e5940c02a6b8cc6405','F3518383-8E11-E6D2-A536-59F4CA5D114B','c09d0463114a4c4e9e935c0437fbbd0a','Voucher Application','task_running','SYSTEM','NO','NO','6a992ade246e458a8c1caf036407722a','2012-11-20 15:02:42'),('aliyun','peixin.baipx','21ebcef12a014146bb32c4d7d3ef7acb','F3518383-8E11-E6D2-A536-59F4CA5D114B','6f11da4a834946b98de3cdf0660da4f5','Voucher Application','task_running','SYSTEM','NO','NO','4436e598798447afb617b2426b23a2fb','2012-11-20 15:08:03'),('aliyun','peixin.baipx','d9d61807108343aab2d6bbf9e4df4a46','F3518383-8E11-E6D2-A536-59F4CA5D114B','682883ccce554388a2b9f855d9bdcbac','Voucher Application','task_running','SYSTEM','NO','NO','d490e6aac47741008b0bbedac661316f','2012-11-21 06:02:43');
/*!40000 ALTER TABLE `cf_task` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_team`
--

DROP TABLE IF EXISTS `cf_team`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_team` (
  `DEV` varchar(24) NOT NULL,
  `ID` varchar(50) NOT NULL,
  `NAME` varchar(40) DEFAULT NULL,
  `EMAIL` varchar(50) DEFAULT NULL,
  `LOGO` varchar(100) DEFAULT '/cflow/images/logo.gif',
  `MEMO` varchar(40) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `DEV_NAME` (`DEV`,`NAME`),
  KEY `DEV` (`DEV`),
  KEY `NAME` (`NAME`),
  CONSTRAINT `cf_team_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_team`
--

LOCK TABLES `cf_team` WRITE;
/*!40000 ALTER TABLE `cf_team` DISABLE KEYS */;
INSERT INTO `cf_team` VALUES ('aliyun','4a4013013e0d4628b32567dacf3dc2dc','LeaveApprovalTeam',NULL,'/cflow/images/logo.gif','LeaveApprovalTeam'),('aliyun','6993b683e42b47c3a78ea808da80f184','VoucherAdmin1',NULL,'/cflow/images/logo.gif','VoucherAdmin1'),('tester1','b9c6b2a570e848cc99ca9a0971f72a1a','teama',NULL,'/cflow/images/logo.gif','ddd');
/*!40000 ALTER TABLE `cf_team` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_teamuser`
--

DROP TABLE IF EXISTS `cf_teamuser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_teamuser` (
  `TEAMID` varchar(40) DEFAULT NULL,
  `USRID` varchar(40) DEFAULT NULL,
  `ROLE` varchar(30) DEFAULT NULL,
  UNIQUE KEY `IDX_TUR` (`TEAMID`,`USRID`,`ROLE`),
  KEY `IDXUSER` (`USRID`),
  KEY `IDXTEAM` (`TEAMID`),
  CONSTRAINT `CFTEAMUSER_ibfk_1` FOREIGN KEY (`TEAMID`) REFERENCES `cf_team` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_teamuser`
--

LOCK TABLES `cf_teamuser` WRITE;
/*!40000 ALTER TABLE `cf_teamuser` DISABLE KEYS */;
INSERT INTO `cf_teamuser` VALUES ('4a4013013e0d4628b32567dacf3dc2dc','allen','Approver'),('6993b683e42b47c3a78ea808da80f184','allen','1stApprover'),('6993b683e42b47c3a78ea808da80f184','cjp1957','finalApprover'),('6993b683e42b47c3a78ea808da80f184','huali.shihl','opLead'),('6993b683e42b47c3a78ea808da80f184','nick.yixy','opDirector'),('b9c6b2a570e848cc99ca9a0971f72a1a','T002','Approver');
/*!40000 ALTER TABLE `cf_teamuser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_timecontrol`
--

DROP TABLE IF EXISTS `cf_timecontrol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_timecontrol` (
  `DEV` varchar(30) DEFAULT NULL,
  `PRCID` varchar(40) DEFAULT NULL,
  `NODEID` varchar(40) DEFAULT NULL,
  `DUE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `USRID` varchar(40) NOT NULL,
  KEY `DEV` (`DEV`),
  KEY `cf_timecontrol_ibfk_2` (`PRCID`),
  CONSTRAINT `cf_timecontrol_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `cf_timecontrol_ibfk_2` FOREIGN KEY (`PRCID`) REFERENCES `cf_process` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_timecontrol`
--

LOCK TABLES `cf_timecontrol` WRITE;
/*!40000 ALTER TABLE `cf_timecontrol` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_timecontrol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_timer`
--

DROP TABLE IF EXISTS `cf_timer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_timer` (
  `DEV` varchar(30) DEFAULT NULL,
  `DOER` varchar(40) DEFAULT NULL,
  `PRCID` varchar(40) DEFAULT NULL,
  `SESSID` varchar(40) DEFAULT NULL,
  `NODEID` varchar(40) DEFAULT NULL,
  `DUE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `TZID` varchar(10) DEFAULT NULL,
  KEY `DEV` (`DEV`),
  KEY `cf_timer_ibfk_2` (`PRCID`),
  CONSTRAINT `cf_timer_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `cf_timer_ibfk_2` FOREIGN KEY (`PRCID`) REFERENCES `cf_process` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_timer`
--

LOCK TABLES `cf_timer` WRITE;
/*!40000 ALTER TABLE `cf_timer` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_timer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_user`
--

DROP TABLE IF EXISTS `cf_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_user` (
  `DEV` varchar(24) NOT NULL,
  `ID` varchar(40) NOT NULL,
  `IDENTITY` varchar(40) NOT NULL,
  `NAME` varchar(40) DEFAULT NULL,
  `PASSWORD` varchar(40) DEFAULT NULL,
  `EMAIL` varchar(50) DEFAULT NULL,
  `TZID` char(9) NOT NULL DEFAULT 'GMT+00:00',
  `LANG` varchar(8) NOT NULL DEFAULT 'en-US',
  `NOTIFY` char(9) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDXIDENTITY` (`DEV`,`IDENTITY`),
  KEY `DEV` (`DEV`),
  CONSTRAINT `cf_user_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_user`
--

LOCK TABLES `cf_user` WRITE;
/*!40000 ALTER TABLE `cf_user` DISABLE KEYS */;
INSERT INTO `cf_user` VALUES ('tester2','0338a7a7a3584dea9bdafdc77d2e2066','T003','T003','9003d1df22eb4d3820015070385194c8','T003@null.com','GMT+08:00','zh-CN','E'),('tester2','0e4d5b9d2fcc453e98186a042e37180e','T005','T005','9003d1df22eb4d3820015070385194c8','T005@null.com','GMT+08:00','zh-CN','E'),('tester1','1e268790d5d54ae7b6a399aeca9641b2','T003','T003','9003d1df22eb4d3820015070385194c8','T003@null.com','GMT+08:00','zh-CN','E'),('tester1','26090cb371214cce9528b3ab32483be6','T004','T004','9003d1df22eb4d3820015070385194c8','T004@null.com','GMT+08:00','zh-CN','E'),('aliyun','272a13285d5141838d69b1c8ed517e0a','peixin.baipx','Bai Peixin','9003d1df22eb4d3820015070385194c8','peixin.baipx@alibaba-inc.com','GMT+08:00','zh-CN','E'),('aliyun','33f3e1369d62498d9810e009727c368f','allen','Zhang Jing','9003d1df22eb4d3820015070385194c8','allen@alibaba-inc.com','GMT+08:00','zh-CN','E'),('tester1','364118271f954635870873db43304cd4','T001','T001','9003d1df22eb4d3820015070385194c8','T001_newemail@null.com','GMT+08:00','zh_CN','E'),('aliyun','5e009d7768ea4104b67145e50b35dbd6','huali.shihl','Shi Huali','9003d1df22eb4d3820015070385194c8','huali.shihl@alibaba-inc.com','GMT+08:00','zh-CN','E'),('abcdefghijklmnopqrstuvwx','63247a52d0fb45a4b14916ae69905a71','abcdefghijklmnopqrstuvwx','name','0d35c1f17675a8a2bf3caaacd59a65de','name','GMT+00:00','en-US','E'),('aliyun','655f68ffff1344b2ac2459929706e851','nick.yixy','Yi Xinyu','9003d1df22eb4d3820015070385194c8','nick.yixy@alibaba-inc.com','GMT+08:00','zh-CN','E'),('tester2','77a0aa336b884b94b27f3f70b6d3b5cb','T001','T001','9003d1df22eb4d3820015070385194c8','T001@null.com','GMT+08:00','zh-CN','E'),('tester1','78cf8f76fd054ff79444ae8e7a0d0e4e','T005','T005','9003d1df22eb4d3820015070385194c8','T005@null.com','GMT+08:00','zh-CN','E'),('aliyun','7ab2cf88b55d4c798f63dd381163313a','kehong.liu','Liu Kehong','9003d1df22eb4d3820015070385194c8','kehong.liu@alibaba-inc.com','GMT+08:00','zh-CN','E'),('tester2','b919de116be14157b4968c2c962fae16','T002','T002','9003d1df22eb4d3820015070385194c8','T002@null.com','GMT+08:00','zh-CN','E'),('tester1','c9ec55abd21d4cafa09853948d8eaf3b','T002','T002','9003d1df22eb4d3820015070385194c8','T002@null.com','GMT+08:00','zh-CN','E'),('tester2','d27bc8bf9f6b43c99114f48758c254e3','T004','T004','9003d1df22eb4d3820015070385194c8','T004@null.com','GMT+08:00','zh-CN','E'),('aliyun','d4afcf81bfc448c191d2aadba52d5dd5','cjp1957','Chen Jinpei','9003d1df22eb4d3820015070385194c8','cjp1957@alibaba-inc.com','GMT+08:00','zh-CN','E'),('aliyun','e7eb356fbaa1426f830d05ed04383450','ying.zhouying','Zhou Ying','9003d1df22eb4d3820015070385194c8','ying.zhouying@alibaba-inc.com','GMT+08:00','zh-CN','E'),('aliyun','f4abff0dde3b4d4fb2743a8d8117df8c','zhengyongsheng','Zheng Yongsheng','9003d1df22eb4d3820015070385194c8','zhengyongsheng@alibaba-inc.com','GMT+08:00','zh-CN','E'),('aliyun','fe1b450251424086b91b850973eb756a','aliyun','Aliyun','2cbeb596935e23838917c8e8c6595f21','aliyun@null.com','GMT+08:00','zh-CN','E'),('tester1','tester1','tester1','tester1','tester1','tester1','GMT+08:00','zh-CN','E'),('tester2','tester2','tester2','tester2','tester2','tester2','GMT+08:00','zh-CN','E');
/*!40000 ALTER TABLE `cf_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_userprocess`
--

DROP TABLE IF EXISTS `cf_userprocess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_userprocess` (
  `DEV` varchar(30) DEFAULT NULL,
  `USRID` varchar(30) DEFAULT NULL,
  `PRCID` varchar(40) DEFAULT NULL,
  `WFTID` varchar(40) DEFAULT NULL,
  `USERTYPE` varchar(10) DEFAULT 'USER',
  UNIQUE KEY `IDXUP` (`USRID`,`PRCID`),
  KEY `IDXDEV` (`DEV`),
  KEY `CFUSERPROCESS_ibfk_1` (`PRCID`),
  CONSTRAINT `CFUSERPROCESS_ibfk_1` FOREIGN KEY (`PRCID`) REFERENCES `cf_process` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `CFUSERPROCESS_ibfk_2` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_userprocess`
--

LOCK TABLES `cf_userprocess` WRITE;
/*!40000 ALTER TABLE `cf_userprocess` DISABLE KEYS */;
INSERT INTO `cf_userprocess` VALUES ('aliyun','aliyun','16fa77642c6d4661bcfcf46debc7f712','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','aliyun','b5ab99dd92904876b34559cdaa68e66f','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','aliyun','4bfd2e77319a43f283b73552d1e90770','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','aliyun','f18eb6166b224625aca9883daeb902ae','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','aliyun','e48eb056a5ed4adaa45a604d7b0af469','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','aliyun','de546908274244589e18e44d095d230a','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','aliyun','6868fade206e44bea5cca941b4bbd487','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','aliyun','765f693427274f49977628218647c6e3','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','aliyun','ed46ce596b6143569a754359a09b899c','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','c99d7c7d77e7478fa7cdf3b27df02e44','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','9b040e117bbb48eba1c428577e7f58cc','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','ab14d6f80024446680d451cba2394475','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','9cfe997c741646668bf236dc381ea801','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','20054e029d3e4139a3b42e747c1fee28','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','cbf44db790514373907f21089e7036f8','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','424fe4a0066243e4bb27d0634ed83753','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','58723ec2443b4a42b430efb6b7182f78','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','45df163f0ae642dea10976a5c5d4fb46','9b2ec3471f89492b9393b5e9144144d1','STARTER'),('aliyun','aliyun','124a7f224ee74f96be2dbb535a9342ac','9b2ec3471f89492b9393b5e9144144d1','STARTER'),('aliyun','aliyun','033de3282d7848049d2b9dce0dd0065b','9b2ec3471f89492b9393b5e9144144d1','STARTER'),('aliyun','aliyun','c73a38456ae64a878ca1f2d4d1a29d25','9b2ec3471f89492b9393b5e9144144d1','STARTER'),('aliyun','aliyun','0e51377e85934014aac8819a0fb64b7b','9b2ec3471f89492b9393b5e9144144d1','STARTER'),('aliyun','aliyun','1dd61c82071c4ab68264c884fbdef26a','9b2ec3471f89492b9393b5e9144144d1','STARTER'),('aliyun','aliyun','d5d3f8cd76ac4d2c9b663832ed4509cc','9b2ec3471f89492b9393b5e9144144d1','STARTER'),('aliyun','aliyun','03ec883e157c4dd49bbfd1cdfa951515','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','123ca01197f7415f97d0c1220b61352f','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','4fbd214b053347e89d631946b79b5d86','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','e1ba9788db9d45a7868a0c638391e7b0','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','c5b89a18d47b4389abfef0c8de07b9ef','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','9b8555deb3c2444b9c13e070ea01c663','6ced6bfac8c74e3d821fb65cff435f73','STARTER'),('aliyun','aliyun','3a4f28eb69084d8d92e13a4baebc0794','6ced6bfac8c74e3d821fb65cff435f73','STARTER'),('aliyun','aliyun','7b8e3b131021449e97d3e75f4c64d023','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','59a2f7f8fd114538af97d4ad503084ce','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','238aa8f9722b40af8518c34eaa0ab583','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','f4322d3669fa41ab82d68bf302a15ea3','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','ee3c446417434004abcf365812537a34','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','cd3fa673122f409dbc968b224e831869','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','f04971fd252a4dc2b8c8cd1524d68c1b','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','f7d4a3988d0d447e8302ecfffcdc4a54','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','81fec3766ca24b3a998fb50810ef4228','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','8f2ee7115c4c4423b45592a12346d6a0','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','902c57a2892747219b63b4854db777c8','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','18eb513dbb944172bae395384a74023d','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','f970d7a9bdf9454fb583a5fcfeb64d9b','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','dfb03aedb9f84ebb8286929451c36e3a','6ced6bfac8c74e3d821fb65cff435f73','STARTER'),('aliyun','aliyun','01ec442022084765b1fc42454664dada','6ced6bfac8c74e3d821fb65cff435f73','STARTER'),('aliyun','aliyun','9662ae59789b41e38b78f8e689fe4ef2','6ced6bfac8c74e3d821fb65cff435f73','STARTER'),('aliyun','aliyun','c5de39ed810b48e0ad9a364907bfc255','6ced6bfac8c74e3d821fb65cff435f73','STARTER'),('aliyun','aliyun','c6f76b9a00934cdf9b4303a704277d89','6ced6bfac8c74e3d821fb65cff435f73','STARTER'),('aliyun','aliyun','036d5c2134f04f0590b489ad44e3d123','6ced6bfac8c74e3d821fb65cff435f73','STARTER'),('aliyun','aliyun','e00e8a148d7745259ac06394124382ba','6ced6bfac8c74e3d821fb65cff435f73','STARTER'),('aliyun','aliyun','5534638cea6f4af38793f7d38065d225','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','103c80b5d346485f80c61efbb5694209','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','64396aae8ebc4b649166016c0358c257','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','b8ddc9b3930c431ca0962ec4219a956a','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','08266cb080654f3b80bfcc36e4705342','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','f7e5c9cdd5184a3cba279d297b80bffc','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','f0e78d8186f840a7bbdedf6ec72aee13','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','8390c8f25df340a8bbdb7720b09b0c09','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','1e764a57fee94ffaa726e2fd3e612ccc','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','3cb3c53c9fc24882b0fbf3df25c3ee54','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','d7a55b771b044dc4afb36fd6bac87931','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','aliyun','b03747cbeccd4bd4b72991c07c776168','8cbc25a397fa442d849a0d2d4a2f37fd','STARTER'),('aliyun','aliyun','d1a9cda42fe14e069b7bd5505c449e0f','ced045deab464fabab7d806b6137cc74','STARTER'),('aliyun','peixin.baipx','8335d7a07061433389c682766099d583','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','peixin.baipx','2ded288d559f4dd89234daf7d1e6fc8f','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','peixin.baipx','b61ca309939441b8ba60cc1a163c1275','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','peixin.baipx','e4c657b20a9c4eabaae0489b0edbd01b','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','peixin.baipx','7dd4705ba6db4c7cbc545b1c69b7be99','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','peixin.baipx','af3a9a093ff84744885faf5ab87d266f','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','peixin.baipx','3db4e1781be748e5940c02a6b8cc6405','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','peixin.baipx','21ebcef12a014146bb32c4d7d3ef7acb','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','peixin.baipx','06c7d191f2db4d83a6bfcb17f18769d5','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','allen','06c7d191f2db4d83a6bfcb17f18769d5','06c7d191f2db4d83a6bfcb17f18769d5','USER'),('aliyun','peixin.baipx','68bfe03b4ac142a48aab46b862b68b84','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','allen','68bfe03b4ac142a48aab46b862b68b84','68bfe03b4ac142a48aab46b862b68b84','USER'),('aliyun','peixin.baipx','7a3414c5b374427b8f97fb204064afdd','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','allen','7a3414c5b374427b8f97fb204064afdd','7a3414c5b374427b8f97fb204064afdd','USER'),('aliyun','peixin.baipx','c9f1adfcb5db494c8da8becbd001cc0f','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','allen','c9f1adfcb5db494c8da8becbd001cc0f','c9f1adfcb5db494c8da8becbd001cc0f','USER'),('aliyun','huali.shihl','c9f1adfcb5db494c8da8becbd001cc0f','c9f1adfcb5db494c8da8becbd001cc0f','USER'),('aliyun','peixin.baipx','8cf25d5cf1814e43b5209b15b38f0064','231066c8eaa24dfd826686a41c229fda','STARTER'),('aliyun','allen','8cf25d5cf1814e43b5209b15b38f0064','8cf25d5cf1814e43b5209b15b38f0064','USER'),('aliyun','peixin.baipx','9b6a0fc726154a2fbddc76b167ed581f','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','peixin.baipx','766ea1ccc54042afb96bf5b2583f29f7','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','peixin.baipx','74d3d2c7850142f7b5f90bc81ffe10a8','ced045deab464fabab7d806b6137cc74','STARTER'),('aliyun','peixin.baipx','e8c9e7366bf44fdf80d510d30d651da9','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','peixin.baipx','f806eb7c8df74b3fba9da9b71d5dc0fb','ced045deab464fabab7d806b6137cc74','STARTER'),('aliyun','peixin.baipx','4bdb9fe01a8c4effb9a61a124b799c89','c509763360e84121b980f8f32bc1efec','STARTER'),('aliyun','peixin.baipx','b7a4dd6bf71e4c23846e6f6d5975809f','231066c8eaa24dfd826686a41c229fda','STARTER'),('aliyun','allen','b7a4dd6bf71e4c23846e6f6d5975809f','b7a4dd6bf71e4c23846e6f6d5975809f','USER'),('aliyun','peixin.baipx','75f61b6b10ba458aa7afb9abf6eee5b6','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','allen','75f61b6b10ba458aa7afb9abf6eee5b6','75f61b6b10ba458aa7afb9abf6eee5b6','USER'),('aliyun','huali.shihl','75f61b6b10ba458aa7afb9abf6eee5b6','75f61b6b10ba458aa7afb9abf6eee5b6','USER'),('aliyun','peixin.baipx','d9d61807108343aab2d6bbf9e4df4a46','3a347ab442164ccf88663ad4070acebf','STARTER'),('aliyun','aliyun','66164ca5ebf44cdb8e1f09ca2f09c848','bd069315979d4eec95afa71919390f75','STARTER'),('aliyun','aliyun','f5f9406d26f6437394c9c92821d2e7ec','bd069315979d4eec95afa71919390f75','STARTER');
/*!40000 ALTER TABLE `cf_userprocess` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_vts`
--

DROP TABLE IF EXISTS `cf_vts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_vts` (
  `DEV` varchar(24) NOT NULL,
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `VTNAME` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `IDXDEVVTNAME` (`DEV`,`VTNAME`),
  KEY `DEV` (`DEV`),
  CONSTRAINT `CFUSERVTS_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1502 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_vts`
--

LOCK TABLES `cf_vts` WRITE;
/*!40000 ALTER TABLE `cf_vts` DISABLE KEYS */;
INSERT INTO `cf_vts` VALUES ('aliyun',1496,'ddd'),('aliyun',1497,'ddde'),('aliyun',1494,'demo_sj_method'),('aliyun',1493,'demo_sj_month'),('aliyun',1495,'demo_sj_result'),('aliyun',1492,'VoucharApply1');
/*!40000 ALTER TABLE `cf_vts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cf_wft`
--

DROP TABLE IF EXISTS `cf_wft`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cf_wft` (
  `DEV` varchar(24) NOT NULL,
  `WFTID` varchar(40) DEFAULT NULL,
  `WFTNAME` varchar(40) DEFAULT NULL,
  `TS` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `IDXWFT` (`DEV`,`WFTID`),
  CONSTRAINT `CFUSERWFT_ibfk_2` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_wft`
--

LOCK TABLES `cf_wft` WRITE;
/*!40000 ALTER TABLE `cf_wft` DISABLE KEYS */;
INSERT INTO `cf_wft` VALUES ('aliyun','3a347ab442164ccf88663ad4070acebf','Voucher Application','0000-00-00 00:00:00'),('aliyun','231066c8eaa24dfd826686a41c229fda','Leave Application','0000-00-00 00:00:00'),('aliyun','c509763360e84121b980f8f32bc1efec','Êï∞ÊçÆËã±‰Ω≥','2012-11-19 09:17:31'),('aliyun','9b2ec3471f89492b9393b5e9144144d1','test1','2012-11-19 10:16:16'),('aliyun','bd069315979d4eec95afa71919390f75','Demo_WEB','2012-11-19 10:59:33'),('aliyun','6ced6bfac8c74e3d821fb65cff435f73','Demo_JAVASCRIPT','2012-11-19 11:33:11'),('aliyun','8cbc25a397fa442d849a0d2d4a2f37fd','Demo_SJYJ','2012-11-19 15:15:33'),('aliyun','ced045deab464fabab7d806b6137cc74','Demo_WEB_TIMEOUT','2012-11-19 15:17:39'),('aliyun','e9bbe658ddf3420eb5bba0ffd1319563','ÂõæÁâáËá™Âä®Âä†Â∑•','2012-11-22 05:23:55'),('aliyun','87636f9e21484ab5bc51dfe86975f0fa','ÂÆ°Ê†∏Â§çÊ†∏','2012-11-22 05:25:59'),('aliyun','fe6c694474fc49baa1ac63dee17a5926','Demo_JAVA','2012-11-22 05:59:30'),('aliyun','32574e3fc80249c19e90b447585d68d6','Demo_AND','2012-11-22 06:11:29'),('aliyun','8b754b437524490ab6be26ec9322108f','Demo_OR','2012-11-22 06:14:26');
/*!40000 ALTER TABLE `cf_wft` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-11-24 21:50:46
