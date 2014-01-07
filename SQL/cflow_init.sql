-- MySQL dump 10.13  Distrib 5.1.63, for Win64 (unknown)
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
  CONSTRAINT `QRTZ_BLOB_TRIGGERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
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
  CONSTRAINT `QRTZ_CRON_TRIGGERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
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
  CONSTRAINT `QRTZ_JOB_LISTENERS_ibfk_1` FOREIGN KEY (`JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`JOB_NAME`, `JOB_GROUP`)
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
  CONSTRAINT `QRTZ_SIMPLE_TRIGGERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_SIMPLE_TRIGGERS`
--

LOCK TABLES `QRTZ_SIMPLE_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_SIMPLE_TRIGGERS` DISABLE KEYS */;
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
  CONSTRAINT `QRTZ_TRIGGERS_ibfk_1` FOREIGN KEY (`JOB_NAME`, `JOB_GROUP`) REFERENCES `QRTZ_JOB_DETAILS` (`JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `QRTZ_TRIGGERS`
--

LOCK TABLES `QRTZ_TRIGGERS` WRITE;
/*!40000 ALTER TABLE `QRTZ_TRIGGERS` DISABLE KEYS */;
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
  CONSTRAINT `QRTZ_TRIGGER_LISTENERS_ibfk_1` FOREIGN KEY (`TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `QRTZ_TRIGGERS` (`TRIGGER_NAME`, `TRIGGER_GROUP`)
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
  `LANG` varchar(8) NOT NULL DEFAULT 'en-US',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_dev`
--

LOCK TABLES `cf_dev` WRITE;
/*!40000 ALTER TABLE `cf_dev` DISABLE KEYS */;
INSERT INTO `cf_dev` VALUES ('tester1','tester1','098f6bcd4621d373cade4e832627b4f6','test@null.com','zh-CN'),('tester2','Tester','098f6bcd4621d373cade4e832627b4f6','tester@null.com','zh-CN');
/*!40000 ALTER TABLE `cf_dev` ENABLE KEYS */;
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
  `WORKNAME` varchar(200) NOT NULL,
  `STATUS` varchar(20) NOT NULL,
  `DELEGATERID` varchar(40) NOT NULL,
  `PRCSUS` varchar(3) DEFAULT 'NO',
  `WORKSUS` varchar(3) DEFAULT 'NO',
  `TS` timestamp default CURRENT_TIMESTAMP,
  `TID` varchar(40) DEFAULT NULL,
  KEY `DEV` (`DEV`),
  KEY `USRID` (`USRID`),
  KEY `CFTASK_ibfk_2` (`PRCID`),
  CONSTRAINT `CFTASK_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `CFTASK_ibfk_2` FOREIGN KEY (`PRCID`) REFERENCES `cf_process` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5611 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_task`
--

LOCK TABLES `cf_task` WRITE;
/*!40000 ALTER TABLE `cf_task` DISABLE KEYS */;
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
  UNIQUE KEY `DEV_NAME` (`DEV`, `NAME`),
  CONSTRAINT `cf_team_ibfk_1` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_team`
--

LOCK TABLES `cf_team` WRITE;
/*!40000 ALTER TABLE `cf_team` DISABLE KEYS */;
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
  `ROLE` varchar(40) DEFAULT NULL,
  UNIQUE KEY `IDXTUR` (`TEAMID`,`USRID`,`ROLE`),
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
  `USRID` varchar(40) DEFAULT NULL,
  `DUE` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
INSERT INTO `cf_user` VALUES ('tester1','tester1','tester1','tester1','tester1','tester1','zh-CN','E'),('tester2','tester2','tester2','tester2','tester2','tester2','zh-CN','E');
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
) ENGINE=InnoDB AUTO_INCREMENT=1491 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_vts`
--

LOCK TABLES `cf_vts` WRITE;
/*!40000 ALTER TABLE `cf_vts` DISABLE KEYS */;
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
  `TS` timestamp DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY `IDXWFT` (`DEV`,`WFTID`),
  CONSTRAINT `CFUSERWFT_ibfk_2` FOREIGN KEY (`DEV`) REFERENCES `cf_dev` (`ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1849 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cf_wft`
--

LOCK TABLES `cf_wft` WRITE;
/*!40000 ALTER TABLE `cf_wft` DISABLE KEYS */;
/*!40000 ALTER TABLE `cf_wft` ENABLE KEYS */;
UNLOCK TABLES;

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
  `RECEIVEDAT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DELIVERED` boolean NOT NULL default false,
  `DELIVEREDAT` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `DAEMON` varchar(40) NOT NULL,
  `SUCCESS` boolean NOT NULL default false,
  `MSG` varchar(400) NOT NULL default '',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

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
  `RECEIVEDAT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DELIVERED` boolean NOT NULL default false,
  `DELIVEREDAT` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `DAEMON` varchar(40) NOT NULL,
  `SUCCESS` boolean NOT NULL default false,
  `MSG` varchar(400) NOT NULL default '',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-11-12  0:31:11
