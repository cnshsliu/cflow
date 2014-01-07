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
  `TZID` char(9) NOT NULL DEFAULT 'GMT+00:00',
  `RECEIVEDAT` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `DELIVERED` boolean NOT NULL default false,
  `DELIVEREDAT` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `DAEMON` varchar(40) NOT NULL,
  `SUCCESS` boolean NOT NULL default false,
  `MSG` varchar(400) NOT NULL default '',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-11-12  0:31:11
