-- MySQL dump 10.13  Distrib 8.4.3, for Win64 (x86_64)
--
-- Host: localhost    Database: gsy-behavior-cloud
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
-- Table structure for table `user_browse`
--

DROP TABLE IF EXISTS `user_browse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_browse` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `creator` bigint DEFAULT NULL COMMENT '创建者，目前使用 SysUser 的 id 编号',
  `updater` bigint DEFAULT NULL COMMENT '更新者，目前使用 SysUser 的 id 编号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint DEFAULT '0' COMMENT '是否删除，删除标记,0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_browse`
--

LOCK TABLES `user_browse` WRITE;
/*!40000 ALTER TABLE `user_browse` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_browse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_click`
--

DROP TABLE IF EXISTS `user_click`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_click` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `creator` bigint DEFAULT NULL COMMENT '创建者，目前使用 SysUser 的 id 编号',
  `updater` bigint DEFAULT NULL COMMENT '更新者，目前使用 SysUser 的 id 编号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint DEFAULT '0' COMMENT '是否删除，删除标记,0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_click`
--

LOCK TABLES `user_click` WRITE;
/*!40000 ALTER TABLE `user_click` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_click` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_collect`
--

DROP TABLE IF EXISTS `user_collect`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_collect` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `creator` bigint DEFAULT NULL COMMENT '创建者，目前使用 SysUser 的 id 编号',
  `updater` bigint DEFAULT NULL COMMENT '更新者，目前使用 SysUser 的 id 编号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint DEFAULT '0' COMMENT '是否删除，删除标记,0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_collect`
--

LOCK TABLES `user_collect` WRITE;
/*!40000 ALTER TABLE `user_collect` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_collect` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_search`
--

DROP TABLE IF EXISTS `user_search`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_search` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `creator` bigint DEFAULT NULL COMMENT '创建者，目前使用 SysUser 的 id 编号',
  `updater` bigint DEFAULT NULL COMMENT '更新者，目前使用 SysUser 的 id 编号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint DEFAULT '0' COMMENT '是否删除，删除标记,0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_search`
--

LOCK TABLES `user_search` WRITE;
/*!40000 ALTER TABLE `user_search` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_search` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gsy-behavior-cloud'
--

--
-- Dumping routines for database 'gsy-behavior-cloud'
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
