-- MySQL dump 10.13  Distrib 8.4.3, for Win64 (x86_64)
--
-- Host: localhost    Database: gsy-review-cloud
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
-- Table structure for table `like_review`
--

DROP TABLE IF EXISTS `like_review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `like_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `review_id` bigint DEFAULT NULL COMMENT '评论ID',
  `tenant_id` bigint DEFAULT NULL COMMENT '租户ID',
  `creator` bigint DEFAULT NULL COMMENT '创建者，目前使用 SysUser 的 id 编号',
  `updater` bigint DEFAULT NULL COMMENT '更新者，目前使用 SysUser 的 id 编号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint DEFAULT '0' COMMENT '是否删除，删除标记,0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_review_id` (`review_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_del_flag` (`del_flag`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论点赞表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `like_review`
--

LOCK TABLES `like_review` WRITE;
/*!40000 ALTER TABLE `like_review` DISABLE KEYS */;
/*!40000 ALTER TABLE `like_review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review_product`
--

DROP TABLE IF EXISTS `review_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review_product` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单id/主键ID',
  `parent_id` bigint DEFAULT NULL COMMENT '父级id',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '评论内容',
  `image` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '图片',
  `product_id` bigint DEFAULT NULL COMMENT '商品id',
  `tenant_id` bigint DEFAULT NULL COMMENT '租户id',
  `creator` bigint DEFAULT NULL COMMENT '创建者，目前使用 SysUser 的 id 编号',
  `updater` bigint DEFAULT NULL COMMENT '更新者，目前使用 SysUser 的 id 编号',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint DEFAULT '0' COMMENT '是否删除，删除标记,0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE,
  KEY `idx_product_id` (`product_id`) USING BTREE,
  KEY `idx_del_flag` (`del_flag`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品评论表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review_product`
--

LOCK TABLES `review_product` WRITE;
/*!40000 ALTER TABLE `review_product` DISABLE KEYS */;
INSERT INTO `review_product` VALUES (1,0,'content','image',1,1,1,NULL,'2026-05-21 20:45:31',NULL,NULL);
/*!40000 ALTER TABLE `review_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gsy-review-cloud'
--

--
-- Dumping routines for database 'gsy-review-cloud'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-13 15:00:10
