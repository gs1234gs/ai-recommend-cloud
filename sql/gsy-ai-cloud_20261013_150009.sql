-- MySQL dump 10.13  Distrib 8.4.3, for Win64 (x86_64)
--
-- Host: localhost    Database: gsy-ai-cloud
-- ------------------------------------------------------
-- Server version	8.4.3

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `active_similarity_threshold_configuration`
--

DROP TABLE IF EXISTS `active_similarity_threshold_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `active_similarity_threshold_configuration` (
  `id` bigint unsigned NOT NULL COMMENT '固定为1，确保单行',
  `similarity_threshold` double NOT NULL COMMENT '当前生效的策略配置ID',
  `creator` bigint unsigned DEFAULT NULL,
  `updater` bigint unsigned DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `del_flag` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  CONSTRAINT `chk_single_row` CHECK ((`id` = 1))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='当前生效的相似度阈值配置（单例表）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `active_similarity_threshold_configuration`
--

LOCK TABLES `active_similarity_threshold_configuration` WRITE;
/*!40000 ALTER TABLE `active_similarity_threshold_configuration` DISABLE KEYS */;
INSERT INTO `active_similarity_threshold_configuration` VALUES (1,0.6,1,1,'2026-02-13 22:24:35','2026-04-10 21:00:28',0);
/*!40000 ALTER TABLE `active_similarity_threshold_configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `api_key`
--

DROP TABLE IF EXISTS `api_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `api_key` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ali_oss_properties` text COMMENT '阿里云OSS配置JSON',
  `ali_sms_properties` text COMMENT '阿里云短信配置JSON',
  `qq` text COMMENT 'QQ配置JSON',
  `ai_dashscope` text COMMENT 'AI Dashscope配置JSON',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='API密钥配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_key`
--

LOCK TABLES `api_key` WRITE;
/*!40000 ALTER TABLE `api_key` DISABLE KEYS */;
INSERT INTO `api_key` VALUES (1,'{}');
/*!40000 ALTER TABLE `api_key` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `big_model`
--

DROP TABLE IF EXISTS `big_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `big_model` (
  `id` bigint NOT NULL DEFAULT '1' COMMENT '主键id',
  `name` varchar(255) DEFAULT NULL COMMENT '大模型名称',
  `type` int COMMENT '大模型类型',
  `status` tinyint DEFAULT '1' COMMENT '大模型状态,0-未启用，1-启用',
  `creator` bigint DEFAULT NULL COMMENT '创建者id',
  `updater` bigint DEFAULT NULL COMMENT '更新者id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint DEFAULT '0' COMMENT '是否删除，删除标记,0-未删除，1-已删除',
  `description` varchar(255) DEFAULT NULL COMMENT '大模型描述',
  `version` varchar(255) DEFAULT NULL COMMENT '大模型版本',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `big_model`
--

LOCK TABLES `big_model` WRITE;
/*!40000 ALTER TABLE `big_model` DISABLE KEYS */;
INSERT INTO `big_model` VALUES (1,'测试',1,0,NULL,17,NULL,'2026-04-10 21:16:47',0,'测试','1.0');
/*!40000 ALTER TABLE `big_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_record`
--

DROP TABLE IF EXISTS `chat_record`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_record` (
  `id` bigint NOT NULL DEFAULT '1' COMMENT '聊天记录id',
  `title` varchar(64) DEFAULT NULL,
  `del_flag` tinyint DEFAULT '0' COMMENT '是否删除，0表示否，1表示是',
  `creator` bigint DEFAULT NULL COMMENT '创建者id',
  `updater` bigint DEFAULT NULL COMMENT '更新者id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_record`
--

LOCK TABLES `chat_record` WRITE;
/*!40000 ALTER TABLE `chat_record` DISABLE KEYS */;
INSERT INTO `chat_record` VALUES (46892351616,'第三版推荐Pyt',0,17,NULL,'2026-02-14 21:07:56',NULL);
INSERT INTO `chat_record` VALUES (50071634048,'第三版推荐Pyt',0,17,NULL,'2026-02-14 21:02:00',NULL);
INSERT INTO `chat_record` VALUES (137447374976,'物流配送时间多久',0,17,NULL,'2026-01-20 22:12:29',NULL);
INSERT INTO `chat_record` VALUES (144879681664,'物流配送时间多久',0,17,17,'2026-01-25 20:02:46','2026-01-25 20:04:01');
INSERT INTO `chat_record` VALUES (149552136320,'推荐一款性价比高',0,17,17,'2026-01-25 18:15:05','2026-01-25 19:22:17');
INSERT INTO `chat_record` VALUES (152270045312,'物流配送时间多久',0,17,NULL,'2026-01-18 20:41:35',NULL);
INSERT INTO `chat_record` VALUES (189741957248,'推荐一款性价比高',0,17,NULL,'2026-01-25 17:52:08',NULL);
INSERT INTO `chat_record` VALUES (226207236224,'推荐一款性价比高',0,17,NULL,'2026-01-20 21:47:30',NULL);
INSERT INTO `chat_record` VALUES (238865645696,'618有什么优惠',0,17,NULL,'2026-01-20 22:00:29',NULL);
INSERT INTO `chat_record` VALUES (251876376704,'第三版推荐Pyt',0,17,NULL,'2026-02-08 19:14:12',NULL);
INSERT INTO `chat_record` VALUES (300253479040,'帮我找笔记本电脑',0,17,NULL,'2026-01-20 22:39:52',NULL);
INSERT INTO `chat_record` VALUES (417794654336,'推荐一款性价比高',0,17,17,'2026-01-25 17:16:13','2026-01-25 17:23:30');
INSERT INTO `chat_record` VALUES (634271072384,'推荐一款性价比高',0,17,NULL,'2026-01-25 17:13:59',NULL);
INSERT INTO `chat_record` VALUES (685340917888,'帮我找笔记本电脑',0,17,17,'2026-01-25 17:05:17','2026-01-25 17:13:01');
INSERT INTO `chat_record` VALUES (1159121109120,'第三版推荐Pyt',0,17,NULL,'2026-02-08 19:16:00',NULL);
INSERT INTO `chat_record` VALUES (1294739734656,'618有什么优惠',0,17,NULL,'2026-01-18 20:43:51',NULL);
INSERT INTO `chat_record` VALUES (1529696256128,'哪个品牌的洗衣机',0,17,NULL,'2026-01-18 21:09:20',NULL);
INSERT INTO `chat_record` VALUES (1559509368960,'哪个品牌的洗衣机',0,17,17,'2026-01-25 20:07:31','2026-01-25 20:31:52');
INSERT INTO `chat_record` VALUES (1662689247360,'哪个品牌的洗衣机',0,17,NULL,'2026-01-20 21:50:22',NULL);
INSERT INTO `chat_record` VALUES (1962984636544,'帮我找笔记本电脑',0,17,NULL,'2026-01-20 22:16:07',NULL);
INSERT INTO `chat_record` VALUES (2069956165760,'哪个品牌的洗衣机',0,17,NULL,'2026-01-20 21:51:10',NULL);
INSERT INTO `chat_record` VALUES (2284385763456,'帮我找笔记本电脑',0,17,17,'2026-01-25 17:25:40','2026-01-25 17:28:16');
INSERT INTO `chat_record` VALUES (2312252719232,'哪个品牌的洗衣机',0,17,NULL,'2026-01-20 22:16:49',NULL);
INSERT INTO `chat_record` VALUES (2508906856576,'推荐一款性价比高',0,17,NULL,'2026-01-25 19:23:12',NULL);
INSERT INTO `chat_record` VALUES (2508906856577,'推荐一款性价比高',0,17,17,'2026-01-25 19:23:12','2026-01-25 19:26:02');
INSERT INTO `chat_record` VALUES (2555220361344,'618有什么优惠',0,17,17,'2026-01-25 20:42:24','2026-01-25 20:42:57');
INSERT INTO `chat_record` VALUES (2606827077760,'物流配送时间多久',0,17,NULL,'2026-01-25 16:53:29',NULL);
INSERT INTO `chat_record` VALUES (2637118341248,'帮我找笔记本电脑',0,17,NULL,'2026-01-20 21:52:18',NULL);
INSERT INTO `chat_record` VALUES (2724502470784,'哪个品牌的洗衣机',0,17,17,'2026-01-25 16:43:14','2026-01-25 16:46:40');
INSERT INTO `chat_record` VALUES (2750138056832,'帮我找笔记本电脑',0,17,NULL,'2026-01-18 20:46:45',NULL);
INSERT INTO `chat_record` VALUES (3152598302848,'第三版推荐Pyt',0,17,NULL,'2026-02-08 19:02:44',NULL);
INSERT INTO `chat_record` VALUES (3460175003776,'618有什么优惠',0,17,NULL,'2026-01-18 21:13:10',NULL);
INSERT INTO `chat_record` VALUES (3528827371648,'第三版Pytho',0,17,NULL,'2026-02-08 19:20:42',NULL);
INSERT INTO `chat_record` VALUES (3806565793920,'物流配送时间多久',0,17,NULL,'2026-01-20 22:19:47',NULL);
INSERT INTO `chat_record` VALUES (4220921086080,'哪个品牌的洗衣机',0,17,NULL,'2026-01-20 22:08:23',NULL);
INSERT INTO `chat_record` VALUES (4558034075776,'推荐一款性价比高',0,17,NULL,'2026-01-25 17:30:11',NULL);
INSERT INTO `chat_record` VALUES (4575893422208,'物流配送时间多久',0,17,NULL,'2026-01-20 22:09:06',NULL);
INSERT INTO `chat_record` VALUES (4871801569408,'推荐一款性价比高',0,17,17,'2026-01-25 16:57:59','2026-01-25 17:04:22');
INSERT INTO `chat_record` VALUES (4951166189696,'推荐一款性价比高',0,17,17,'2026-01-25 16:47:40','2026-01-25 16:50:09');
INSERT INTO `chat_record` VALUES (5003443994752,'第三版推荐Pyt',0,17,17,'2026-02-08 17:37:28','2026-02-08 18:59:28');
INSERT INTO `chat_record` VALUES (5553661182080,'第三版推荐Pyt',0,17,NULL,'2026-02-08 19:07:30',NULL);
INSERT INTO `chat_record` VALUES (7040944603264,'第三版推荐Pyt',0,17,NULL,'2026-02-08 19:10:28',NULL);
INSERT INTO `chat_record` VALUES (7651685597312,'推荐一款性价比高',0,17,NULL,'2026-01-20 22:27:25',NULL);
INSERT INTO `chat_record` VALUES (7668815134848,'哪个品牌的洗衣机',0,17,NULL,'2026-01-18 20:56:31',NULL);
INSERT INTO `chat_record` VALUES (8375379198080,'618有什么优惠',0,17,NULL,'2026-01-18 21:22:56',NULL);
INSERT INTO `chat_record` VALUES (8480706560128,'帮我找笔记本电脑',0,17,NULL,'2026-01-20 22:29:04',NULL);
INSERT INTO `chat_record` VALUES (8723321880704,'推荐一款性价比高',0,17,NULL,'2026-01-20 22:29:33',NULL);
INSERT INTO `chat_record` VALUES (8760860901504,'推荐一款性价比高',0,17,NULL,'2026-01-25 17:38:32',NULL);
INSERT INTO `chat_record` VALUES (9580260130944,'物流配送时间多久',0,17,NULL,'2026-01-18 21:25:19',NULL);
INSERT INTO `chat_record` VALUES (10617108856960,'哪个品牌的洗衣机',0,17,NULL,'2026-01-20 22:33:19',NULL);
INSERT INTO `chat_record` VALUES (11229468852352,'推荐一款性价比高',0,17,NULL,'2026-01-18 21:28:36',NULL);
INSERT INTO `chat_record` VALUES (13940146864256,'推荐一款性价比高',0,17,17,'2026-01-25 17:48:49','2026-01-25 17:49:31');
INSERT INTO `chat_record` VALUES (695458057977984,'哪个品牌的洗衣机',0,17,NULL,'2026-02-03 22:12:32',NULL);
INSERT INTO `chat_record` VALUES (120964147193053184,'我叫二牛请问你是',0,1,NULL,'2025-10-17 17:14:06',NULL);
INSERT INTO `chat_record` VALUES (122723365797494784,'我叫二牛请问你是',0,1,NULL,'2025-10-17 18:14:55',NULL);
INSERT INTO `chat_record` VALUES (123497421983449088,'我叫二牛请问你是',0,1,1,'2025-10-17 18:40:29','2025-10-18 15:13:02');
INSERT INTO `chat_record` VALUES (124412215657758720,'我叫二牛请问你是',0,1,1,'2025-10-17 17:52:59','2025-10-18 13:33:43');
INSERT INTO `chat_record` VALUES (126241803006377984,'我叫二牛请问你是',0,1,NULL,'2025-10-17 17:16:40',NULL);
INSERT INTO `chat_record` VALUES (126312171750555648,'我叫二牛请问你是',0,1,NULL,'2025-10-17 17:52:05',NULL);
INSERT INTO `chat_record` VALUES (126593646727266304,'我叫徐庶请问我是',0,1,NULL,'2025-10-17 14:23:39',NULL);
INSERT INTO `chat_record` VALUES (127578809145753600,'我叫二牛请问你是',0,1,NULL,'2025-10-17 17:23:17',NULL);
INSERT INTO `chat_record` VALUES (129408396494372864,NULL,0,NULL,NULL,NULL,NULL);
INSERT INTO `chat_record` VALUES (130956508866281472,'我叫徐庶请问我是',0,1,NULL,'2025-10-17 15:09:44',NULL);
INSERT INTO `chat_record` VALUES (131449090075525120,'我叫徐庶请问我是',0,1,NULL,'2025-10-17 14:47:18',NULL);
INSERT INTO `chat_record` VALUES (133278677424144384,'我叫二牛请问你是',0,1,NULL,'2025-10-17 17:10:30',NULL);
INSERT INTO `chat_record` VALUES (133982364865921024,'我叫徐庶请问我是',0,1,NULL,'2025-10-17 14:32:42',NULL);
INSERT INTO `chat_record` VALUES (137641539563159552,'请问你是谁我叫徐',0,1,NULL,'2025-10-17 15:39:03',NULL);
INSERT INTO `chat_record` VALUES (141793295469641728,'我叫徐庶请问我是',0,1,NULL,'2025-10-17 15:23:51',NULL);
INSERT INTO `chat_record` VALUES (141934032957997056,'我叫二牛请问你是',0,1,NULL,'2025-10-17 17:12:50',NULL);
INSERT INTO `chat_record` VALUES (143200670353195008,'二牛',0,1,1,'2025-10-20 14:11:02','2025-10-20 14:11:02');
INSERT INTO `chat_record` VALUES (176485086349230080,'我是谁',0,1,1,'2025-10-24 17:28:36','2025-10-24 17:31:19');
INSERT INTO `chat_record` VALUES (1193383808460652544,'推荐一款性价比高',0,17,NULL,'2026-01-18 20:17:31',NULL);
/*!40000 ALTER TABLE `chat_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `knowledge_repository`
--

DROP TABLE IF EXISTS `knowledge_repository`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `knowledge_repository` (
  `id` bigint NOT NULL,
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint DEFAULT NULL COMMENT '是否删除，0否，1是',
  `creator` bigint DEFAULT NULL COMMENT '创建者',
  `updater` bigint DEFAULT NULL COMMENT '更新者',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='聊天记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `knowledge_repository`
--

LOCK TABLES `knowledge_repository` WRITE;
/*!40000 ALTER TABLE `knowledge_repository` DISABLE KEYS */;
/*!40000 ALTER TABLE `knowledge_repository` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `similarity_threshold_strategy_configuration`
--

DROP TABLE IF EXISTS `similarity_threshold_strategy_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `similarity_threshold_strategy_configuration` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `similarity_threshold` double NOT NULL DEFAULT '0' COMMENT '相似度阈值，范围 [0.0, 1.0]',
  `creator` bigint unsigned DEFAULT NULL COMMENT '创建者ID',
  `updater` bigint unsigned DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '删除标记: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='相似度阈值策略配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `similarity_threshold_strategy_configuration`
--

LOCK TABLES `similarity_threshold_strategy_configuration` WRITE;
/*!40000 ALTER TABLE `similarity_threshold_strategy_configuration` DISABLE KEYS */;
INSERT INTO `similarity_threshold_strategy_configuration` VALUES (1,0.6,1,1,'2026-02-13 22:24:35','2026-02-21 00:12:06',0);
/*!40000 ALTER TABLE `similarity_threshold_strategy_configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gsy-ai-cloud'
--

--
-- Dumping routines for database 'gsy-ai-cloud'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-13 15:00:09
