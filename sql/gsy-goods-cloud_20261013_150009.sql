-- MySQL dump 10.13  Distrib 8.4.3, for Win64 (x86_64)
--
-- Host: localhost    Database: gsy-goods-cloud
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
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类主键ID',
  `name` varchar(255) NOT NULL COMMENT '分类名称',
  `level` int DEFAULT NULL COMMENT '分类层级（如 1=一级分类，2=二级分类）',
  `code` varchar(255) DEFAULT NULL COMMENT '分类编码（用于快速检索或编码管理）',
  `sort_order` int DEFAULT NULL COMMENT '排序字段（越小越靠前）',
  `icon_url` varchar(1024) DEFAULT NULL COMMENT '分类图标（URL）',
  `status` tinyint DEFAULT '0' COMMENT '状态,是否启用（1=启用，0=禁用）',
  `recommended` tinyint DEFAULT NULL COMMENT '是否为推荐分类（如首页展示）,(0 = 否，1表示是)',
  `creator` bigint NOT NULL COMMENT '创建者',
  `updater` bigint DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `del_flag` tinyint DEFAULT '0' COMMENT '是否删除，删除标记,0-未删除，1-已删除',
  `description` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `category`
--

LOCK TABLES `category` WRITE;
/*!40000 ALTER TABLE `category` DISABLE KEYS */;
INSERT INTO `category` VALUES (6,'电脑',1,'65470359175296',0,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/00376a22-7a74-43e6-a591-9cce0b72868a.png',0,0,17,NULL,'2026-02-06 18:22:15',NULL,0,'苹果电脑分类');
INSERT INTO `category` VALUES (7,'教材',1,'65769966698624',0,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/d5cb772f-0614-4ccd-9b71-9a71ef18e3c3.png',0,0,17,NULL,'2026-02-06 18:25:56',NULL,0,'编程分类');
INSERT INTO `category` VALUES (8,'手机',1,'45247606325376',0,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/38f2177d-a379-400b-a388-ea8ea88a342c.png',0,0,17,NULL,'2026-02-06 18:27:39',NULL,0,'苹果手机');
INSERT INTO `category` VALUES (9,'电视',1,'131105210531968',0,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/02cc3a20-d962-4256-8de2-7747d5692e31.png',0,0,17,NULL,'2026-02-06 18:29:45',NULL,0,'家电');
INSERT INTO `category` VALUES (10,'家具',1,'4961349959808',0,'',0,0,17,NULL,'2026-02-10 23:48:49',NULL,0,'');
INSERT INTO `category` VALUES (11,'化妆品',1,'33054689099904',0,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/f20fb191-1e03-49a9-9284-a20f5aa2d329.png',0,1,17,NULL,'2026-02-11 23:06:03',NULL,0,'');
INSERT INTO `category` VALUES (12,'机器人',1,'108965769805952',1,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/6ee9972b-f337-4641-bc79-8439adaea4c4.png',0,1,17,NULL,'2026-02-19 23:02:55',NULL,0,'人工智能');
INSERT INTO `category` VALUES (13,'金银首饰',1,'29770112860288',0,'',0,0,17,NULL,'2026-05-10 19:23:48',NULL,0,'');
INSERT INTO `category` VALUES (14,'食品',1,'22538822385792',0,'',0,0,17,17,'2026-05-22 23:11:04','2026-05-22 23:14:56',0,'');
INSERT INTO `category` VALUES (15,'酒类',1,'21547901288576',0,'',0,0,17,NULL,'2026-05-22 23:12:58',NULL,0,'');
/*!40000 ALTER TABLE `category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product` (
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '商品描述',
  `image` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '商品图片 URL',
  `video` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频 URL',
  `brand` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '品牌',
  `status` tinyint DEFAULT NULL COMMENT '状态，0-下架，1-上架，2-审核中，3-审核未通过',
  `level` smallint NOT NULL COMMENT '商品等级',
  `creator` bigint DEFAULT NULL COMMENT '创建者ID',
  `updater` bigint DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0-未删除，1-已删除',
  `publish_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
  `offline_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下架时间',
  `id` bigint NOT NULL AUTO_INCREMENT,
  `min_price` decimal(12,6) DEFAULT NULL COMMENT '最低价',
  `max_price` decimal(12,6) DEFAULT NULL COMMENT '最高价',
  `place_of_origin` text COLLATE utf8mb4_unicode_ci COMMENT '产地',
  `tenant_id` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product`
--

LOCK TABLES `product` WRITE;
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` VALUES ('Python编程从入门到实践 第三版 ',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/c9888b6b-45e7-4ade-a91e-c767e1045107.png','','滇西大',1,3,17,NULL,'2026-02-06 20:11:31','2026-02-19 20:00:05',0,'2026-02-06 20:11:31','2027-02-01 00:00:00',23,61.400000,211.560000,'美国',1);
INSERT INTO `product` VALUES ('Java 从入门到起飞 第四版','全3册 JAVA从入门到精通第4版 +JAVA WEB王者归来+JAVA编程思想','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/23bef58d-2f25-4000-b68d-5b4f476c2546.png','','滇西大',1,1,17,NULL,'2026-02-09 20:38:47','2026-02-19 19:59:52',0,'2026-02-09 00:00:00','2028-02-26 00:00:00',24,79.000000,399.990000,'云南',1);
INSERT INTO `product` VALUES ('Apple AI笔记本 2025款','PLUS 180天只换不修 · 7天价保 · 免费上门退换\n一年质保 · 京东发货&售后服务 · 高价回收','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/734a1ed1-6075-44d5-b3bb-32d83137ede7.png','','Apple',1,0,17,NULL,'2026-02-09 21:26:42','2026-02-10 22:07:00',0,'2026-02-09 00:00:00','2027-02-01 00:00:00',25,9499.000000,12436.510000,'US',1);
INSERT INTO `product` VALUES ('源氏木语精选实木床中古风大板床',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/10b937d0-78b7-4bea-b652-789111db075f.png','','源氏',1,0,17,NULL,'2026-02-10 23:47:28','2026-02-11 22:03:09',0,'2026-02-10 00:00:00','2026-08-29 00:00:00',26,1499.000000,1499.000000,'云南',1);
INSERT INTO `product` VALUES ('雅诗兰黛美白胶原水乳','护肤品情人节礼物老婆','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/c037dd18-0382-4041-9467-741017dbde46.png','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/8b75f98c-dc53-4a31-a28b-db0060aa97d7.mp4','雅诗兰黛',1,0,17,NULL,'2026-02-11 23:08:51','2026-02-13 01:12:22',0,'2026-02-11 00:00:05','2027-05-15 00:00:00',27,1650.000000,2440.000000,'广东省广州市黄浦区001室',1);
INSERT INTO `product` VALUES ('欧莱雅欧莱雅（LOREAL）黑松露40-50岁女人护肤品套装化 欧莱雅黑金松露【全家福】','黑松露40-50岁女人护肤品套装化 欧莱雅黑金松露【全家福】','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/494d3216-fa0e-471d-810d-967e71970899.png','','欧莱雅欧莱雅（LOREAL）',1,0,17,NULL,'2026-02-11 23:21:55','2026-02-23 23:15:13',0,'2026-02-11 00:00:00','2029-02-28 00:00:00',28,348.000000,3822.000000,'广东 | 广州',1);
INSERT INTO `product` VALUES ('DOBOT【春晚同款】越疆 ATOM人形机器人',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/cfd69b37-b8ae-4bba-9c41-6b30146243af.png','','China',1,0,17,NULL,'2026-02-19 23:31:26','2026-02-23 23:15:06',0,'2026-02-19 00:00:00','2027-04-30 00:00:00',29,8999.000000,598000.000000,'中国上海',1);
INSERT INTO `product` VALUES ('中国黄金（CHINA GOLD）',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/96b317b8-0bb4-41e8-a289-d6f48dcd111e.png','','中国黄金（CHINA GOLD）',1,0,3,NULL,'2026-05-10 19:35:07','2026-05-11 15:32:46',0,'2026-05-10 00:00:00','2027-05-10 00:06:00',30,2329.000000,5149.000000,'云南',3);
INSERT INTO `product` VALUES ('周大生黄金手镯繁花似锦足金手镯女金镯子七夕情人节送女友老婆生日礼物',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/90e70631-bbe3-4ccb-93f0-61a6104d7cf2.png','','周大生',1,0,3,NULL,'2026-05-10 19:56:57','2026-05-11 15:32:40',0,'2026-05-10 00:00:08','2026-05-10 00:00:00',31,17569.000000,17569.000000,'云南',3);
INSERT INTO `product` VALUES ('中国黄金银手镯','足银999纯银镯子女款玉手链生日情人节520礼物送女朋友s925','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/7d0c2b71-e7f1-470a-91db-613f5229aeaf.png','','中国黄金',1,0,3,NULL,'2026-05-11 15:40:48','2026-05-14 21:29:18',0,'2026-05-11 00:00:00','2028-05-06 00:00:00',32,377.000000,422.000000,'山东',3);
INSERT INTO `product` VALUES ('华为手机智选80pro新品5G旗舰',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/f598dbb6-8344-4187-acb4-f14cd0b21997.png','','华为',1,0,2,NULL,'2026-05-16 20:27:56','2026-05-16 20:37:58',0,'2026-05-16 00:00:00','2027-05-22 00:00:00',33,2820.000000,3031.000000,'中国深圳',4);
INSERT INTO `product` VALUES ('HUAWEI Mate 80',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/c83b7056-ad5c-40d0-a67d-5467aba5567e.png','','HUAWEI',1,0,2,NULL,'2026-05-16 20:46:03','2026-05-16 20:50:00',0,'2026-05-16 00:00:00','2026-05-16 20:46:02',34,7999.000000,11999.000000,'中国',4);
INSERT INTO `product` VALUES ('HUAWEI Pura 90',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/973052d2-8678-48e1-8186-3256d3c2c0f9.png','','HUAWEI',0,0,2,NULL,'2026-05-16 20:53:32','2026-05-16 20:57:46',0,'2026-05-16 00:00:00','2026-05-16 20:53:32',35,6499.000000,7499.000000,'中国',4);
INSERT INTO `product` VALUES ('Apple/苹果 iPhone 17',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/4c796276-e9f5-41a0-87e0-15d512af8513.png','','Apple',1,0,35,NULL,'2026-05-16 21:24:33','2026-05-16 21:39:01',0,'2026-05-16 00:00:00','2026-05-16 21:24:32',36,5999.000000,7999.000000,'美国',5);
INSERT INTO `product` VALUES ('vivoiQOO',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/ab81de47-7a40-4f8f-b32b-1ca414d5da12.png','',NULL,0,0,35,35,'2026-05-22 22:32:14','2026-05-22 22:36:36',0,'2026-05-22 00:00:00','2026-05-22 22:32:14',38,3749.000000,3749.000000,'云南',5);
INSERT INTO `product` VALUES ('三只松鼠围裙阿姨咸鸭蛋',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/00224ae2-3af4-4d8b-9c05-de0b01c16973.png','','三只松鼠',1,0,37,37,'2026-05-22 23:18:14','2026-05-22 23:20:45',0,'2026-05-22 00:00:00','2026-05-22 23:18:13',39,43.910000,43.910000,'云南',7);
INSERT INTO `product` VALUES ('三只松鼠',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/96871165-0d15-4a2a-96c6-b9f8a1fd90b8.png','','三只松鼠',1,0,37,37,'2026-05-22 23:20:38','2026-05-22 23:32:33',0,'2026-05-22 00:00:00','2026-05-22 23:20:38',40,71.160000,71.160000,'云南',7);
INSERT INTO `product` VALUES ('三只松鼠手撕风干牛肉干',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/5ed7f5f2-2b19-4814-abb6-1792ef7273d9.png','','三只松鼠',1,0,37,37,'2026-05-22 23:24:54','2026-05-22 23:32:27',0,'2026-05-22 00:00:00','2026-05-22 23:24:53',41,37.910000,37.910000,'云南',7);
INSERT INTO `product` VALUES ('稻可道 臻选东北大米',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/28ef0e62-5849-4d08-bfbb-3fff506eb863.png','','稻可道 臻选',1,0,37,37,'2026-05-22 23:29:07','2026-05-22 23:32:21',0,'2026-05-22 00:00:00','2026-05-22 23:29:06',42,125.000000,125.000000,'黑龙江',7);
INSERT INTO `product` VALUES ('茅台',NULL,'https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/858c4589-26dd-4b83-a649-a514d7c3b1dc.png','','茅台',0,0,37,37,'2026-05-22 23:59:14','2026-05-23 00:00:16',0,'2026-05-22 00:00:00','2026-05-22 23:59:14',43,1662.010000,1662.010000,'贵州',7);
/*!40000 ALTER TABLE `product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_carousal`
--

DROP TABLE IF EXISTS `product_carousal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_carousal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '轮播标题',
  `image` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '图片路径',
  `link` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '轮播链接',
  `sort` int NOT NULL DEFAULT '0' COMMENT '轮播顺序',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '是否显示：0-不显示，1-显示',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` int DEFAULT '0',
  `creator` bigint DEFAULT (now()),
  `updater` bigint DEFAULT (now()),
  `type` int NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品轮播图配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_carousal`
--

LOCK TABLES `product_carousal` WRITE;
/*!40000 ALTER TABLE `product_carousal` DISABLE KEYS */;
INSERT INTO `product_carousal` VALUES (3,'换新 iPhone，立享分期免息','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/843f0c54-1217-4771-b8d8-a5e2cf1c2e5b.jpg','http://localhost:9000/main/home',1,1,'以旧换新高额补贴，0利息分期，轻松拥有新一代科技旗舰','2026-02-19 15:37:39','2026-02-19 15:42:02',0,17,NULL,1);
INSERT INTO `product_carousal` VALUES (4,'捕捉光影之美','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/2a36a511-106c-44d0-825a-5d8062b7fe92.jpg','http://localhost:9000/main/home',2,1,'优秀夜景表现与宽广动态范围，记录真实色彩','2026-02-19 15:49:15','2026-02-19 15:49:15',0,17,NULL,1);
INSERT INTO `product_carousal` VALUES (5,'限时特惠，直降千元','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/f2fa8fda-7583-47f4-80d3-4d1b1112f099.jpg','http://localhost:9000/main/home',3,1,'登录即享专属优惠，爆款机型抢购中','2026-02-19 15:51:32','2026-02-19 15:51:32',0,17,NULL,1);
INSERT INTO `product_carousal` VALUES (6,'全能家庭小助手','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/586ea98d-bf80-408f-bc4e-53483aed5a5f.jpg','http://localhost:9000/main/home',4,1,'陪伴互动、智能提醒，贴心守护每一天','2026-02-19 15:52:50','2026-02-19 15:52:50',0,17,NULL,1);
INSERT INTO `product_carousal` VALUES (7,'简约设计，质感表达','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/1d152f8a-978c-4d1e-9512-b45ad9854a06.jpg','http://localhost:9000/main/home',2,1,'低调高级配色，打造不费力的时尚感','2026-02-19 16:18:24','2026-02-19 16:02:19',0,NULL,17,1);
INSERT INTO `product_carousal` VALUES (8,'超越想象的速度与影像','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/c88fab07-0afd-4b4d-876b-ea1915e20c65.jpg','http://localhost:9000/main/home',0,1,'搭载全新芯片与专业级摄像系统，性能更强、续航更久，让每一次拍摄与操作都流畅非凡','2026-02-19 16:00:13','2026-02-19 16:00:18',0,NULL,17,2);
INSERT INTO `product_carousal` VALUES (9,'轻盈舒适 自然随行','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/8640a007-6cae-4ef3-b9b3-fd72b0f01d9c.jpg','http://localhost:9000/main/home',3,1,'贴合剪裁与透气材质，让每一步都自在随心，尽显优雅与活力','2026-02-19 16:18:22','2026-02-19 16:18:06',0,NULL,17,2);
INSERT INTO `product_carousal` VALUES (10,'轻薄便携 强悍实力','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/6bef5579-34c4-485d-99de-124abd304a90.jpg','http://localhost:9000/main/home',3,1,'机身轻巧却性能澎湃，随时随地开启高效工作与娱乐体验','2026-02-19 16:18:57','2026-02-19 16:18:47',0,NULL,17,2);
INSERT INTO `product_carousal` VALUES (11,'成长路上的智能伙伴','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/1e5ce85b-7047-4e3d-9441-e40e5fa39cb8.jpg','http://localhost:9000/main/home',4,1,'故事、音乐、学习互动，陪伴孩子快乐成长','2026-02-19 16:59:46',NULL,0,17,20260219165946,2);
INSERT INTO `product_carousal` VALUES (12,'简约设计，质感表达','https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/5dd388db-ebd9-452d-9d86-658a1c5cc3d5.jpg','http://localhost:9000/main/home',5,1,'低调高级配色，打造不费力的时尚感','2026-02-19 17:01:06',NULL,0,17,20260219170106,2);
/*!40000 ALTER TABLE `product_carousal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `product_id` bigint DEFAULT NULL,
  `category_id` bigint DEFAULT NULL,
  `creator` bigint DEFAULT NULL,
  `updater` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `del_flag` bigint DEFAULT NULL,
  `tenant_id` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_category`
--

LOCK TABLES `product_category` WRITE;
/*!40000 ALTER TABLE `product_category` DISABLE KEYS */;
INSERT INTO `product_category` VALUES (24,23,7,17,NULL,'2026-02-06 20:11:31',NULL,0,1);
INSERT INTO `product_category` VALUES (25,24,7,17,NULL,'2026-02-09 20:38:47',NULL,0,1);
INSERT INTO `product_category` VALUES (26,25,6,17,NULL,'2026-02-09 21:26:42',NULL,0,1);
INSERT INTO `product_category` VALUES (27,26,10,17,17,'2026-02-10 23:49:17',NULL,0,1);
INSERT INTO `product_category` VALUES (28,27,11,17,NULL,'2026-02-11 23:08:51',NULL,0,1);
INSERT INTO `product_category` VALUES (29,28,11,17,NULL,'2026-02-11 23:21:55',NULL,0,1);
INSERT INTO `product_category` VALUES (30,29,12,17,NULL,'2026-02-19 23:31:26',NULL,0,1);
INSERT INTO `product_category` VALUES (31,30,13,3,NULL,'2026-05-10 19:35:07',NULL,0,3);
INSERT INTO `product_category` VALUES (32,31,13,3,NULL,'2026-05-10 19:56:57',NULL,0,3);
INSERT INTO `product_category` VALUES (33,32,13,3,NULL,'2026-05-11 15:40:48',NULL,0,3);
INSERT INTO `product_category` VALUES (34,33,8,2,NULL,'2026-05-16 20:27:56',NULL,0,4);
INSERT INTO `product_category` VALUES (35,34,8,2,NULL,'2026-05-16 20:46:03',NULL,0,4);
INSERT INTO `product_category` VALUES (36,35,8,2,NULL,'2026-05-16 20:53:32',NULL,0,4);
INSERT INTO `product_category` VALUES (37,36,8,35,NULL,'2026-05-16 21:24:33',NULL,0,5);
INSERT INTO `product_category` VALUES (39,38,8,35,NULL,'2026-05-22 22:32:14',NULL,NULL,5);
INSERT INTO `product_category` VALUES (40,39,14,37,NULL,'2026-05-22 23:18:14',NULL,NULL,7);
INSERT INTO `product_category` VALUES (41,40,14,37,NULL,'2026-05-22 23:20:38',NULL,NULL,7);
INSERT INTO `product_category` VALUES (42,41,14,37,NULL,'2026-05-22 23:24:54',NULL,NULL,7);
INSERT INTO `product_category` VALUES (43,42,14,37,NULL,'2026-05-22 23:29:07',NULL,NULL,7);
INSERT INTO `product_category` VALUES (44,43,15,37,NULL,'2026-05-22 23:59:14',NULL,NULL,7);
/*!40000 ALTER TABLE `product_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_tag`
--

DROP TABLE IF EXISTS `product_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `product_id` bigint NOT NULL COMMENT '商品ID，关联 product 表',
  `tag_id` bigint NOT NULL COMMENT '标签ID，关联 tag 表',
  `creator` bigint DEFAULT NULL COMMENT '创建者ID',
  `updater` bigint DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0=未删除，1=已删除',
  `tenant_id` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_tag` (`product_id`,`tag_id`),
  KEY `idx_tag_id` (`tag_id`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品标签关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_tag`
--

LOCK TABLES `product_tag` WRITE;
/*!40000 ALTER TABLE `product_tag` DISABLE KEYS */;
INSERT INTO `product_tag` VALUES (1,23,8,17,NULL,'2026-02-10 23:25:33','2026-04-06 17:52:33',0,1);
INSERT INTO `product_tag` VALUES (2,25,4,17,NULL,'2026-02-10 23:36:57','2026-04-06 17:52:33',0,1);
INSERT INTO `product_tag` VALUES (3,24,3,17,NULL,'2026-02-10 23:37:06','2026-04-06 17:52:33',0,1);
INSERT INTO `product_tag` VALUES (4,24,7,17,NULL,'2026-02-10 23:37:06','2026-04-06 17:52:33',0,1);
INSERT INTO `product_tag` VALUES (5,26,7,17,NULL,'2026-02-10 23:47:28','2026-04-06 17:52:33',0,1);
INSERT INTO `product_tag` VALUES (6,27,5,17,NULL,'2026-02-11 23:08:51','2026-04-06 17:52:33',0,1);
INSERT INTO `product_tag` VALUES (7,28,7,17,17,'2026-02-13 01:13:44','2026-04-06 17:52:33',0,1);
INSERT INTO `product_tag` VALUES (8,29,9,17,NULL,'2026-02-19 23:31:26','2026-04-06 17:52:33',0,1);
INSERT INTO `product_tag` VALUES (9,30,10,3,NULL,'2026-05-10 19:35:07','2026-05-10 19:35:07',0,3);
INSERT INTO `product_tag` VALUES (10,31,11,3,NULL,'2026-05-10 19:56:57','2026-05-10 19:56:56',0,3);
INSERT INTO `product_tag` VALUES (11,32,10,3,NULL,'2026-05-11 15:40:48','2026-05-11 15:40:48',0,3);
INSERT INTO `product_tag` VALUES (12,33,13,2,NULL,'2026-05-16 20:27:56','2026-05-16 20:27:55',0,4);
INSERT INTO `product_tag` VALUES (13,34,13,2,NULL,'2026-05-16 20:46:03','2026-05-16 20:46:02',0,4);
INSERT INTO `product_tag` VALUES (14,34,12,2,NULL,'2026-05-16 20:46:03','2026-05-16 20:46:02',0,4);
INSERT INTO `product_tag` VALUES (15,35,12,2,NULL,'2026-05-16 20:53:32','2026-05-16 20:53:32',0,4);
INSERT INTO `product_tag` VALUES (16,36,14,35,NULL,'2026-05-16 21:24:33','2026-05-16 21:24:32',0,5);
INSERT INTO `product_tag` VALUES (18,38,14,35,NULL,'2026-05-22 22:32:14','2026-05-22 22:32:14',0,5);
INSERT INTO `product_tag` VALUES (19,39,15,37,NULL,'2026-05-22 23:18:14','2026-05-22 23:18:13',0,7);
INSERT INTO `product_tag` VALUES (20,40,15,37,NULL,'2026-05-22 23:20:38','2026-05-22 23:20:38',0,7);
INSERT INTO `product_tag` VALUES (21,41,15,37,NULL,'2026-05-22 23:24:54','2026-05-22 23:24:53',0,7);
INSERT INTO `product_tag` VALUES (22,42,15,37,NULL,'2026-05-22 23:29:07','2026-05-22 23:29:06',0,7);
INSERT INTO `product_tag` VALUES (23,43,15,37,NULL,'2026-05-22 23:59:14','2026-05-22 23:59:14',0,7);
/*!40000 ALTER TABLE `product_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sku`
--

DROP TABLE IF EXISTS `sku`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sku` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'SKU 主键ID',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'SKU 名称（通常为 SPU 名 + 规格描述，如 iPhone 15 黑色 128GB）',
  `sku_code` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '商品内部唯一编码，用于系统识别',
  `price` decimal(19,2) NOT NULL COMMENT '销售价',
  `cost_price` decimal(19,2) DEFAULT NULL COMMENT '成本价（可选）',
  `pic_list` longtext COLLATE utf8mb4_unicode_ci COMMENT '图片',
  `stock` int NOT NULL DEFAULT '0' COMMENT '库存数量',
  `status` smallint NOT NULL DEFAULT '1' COMMENT '状态：0=下架，1=上架',
  `weight` decimal(10,6) DEFAULT '1.000000' COMMENT '权重（用于推荐、排序，如热销权重）',
  `creator` bigint DEFAULT NULL COMMENT '创建者ID',
  `updater` bigint DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0=未删除，1=已删除',
  `sort` int DEFAULT NULL COMMENT '排序值',
  `publish_time` datetime DEFAULT NULL COMMENT '上架时间',
  `offline_time` datetime DEFAULT NULL COMMENT '下架时间',
  `detail_content` longtext COLLATE utf8mb4_unicode_ci COMMENT '商品详情',
  `sales_volume` int NOT NULL DEFAULT '0' COMMENT '销售量',
  `tenant_id` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存单元表（SKU）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sku`
--

LOCK TABLES `sku` WRITE;
/*!40000 ALTER TABLE `sku` DISABLE KEYS */;
INSERT INTO `sku` VALUES (3,23,'Python编程从入门到实践 第三版【入门书】','4512047726720',65.80,20.54,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/5f4fd8d6-de65-4cf9-8859-6798b1854f12.png\"]',21,0,1.000000,17,NULL,'2026-02-06 23:12:52','2026-02-06 23:13:26',0,89,'2026-02-06 00:00:00','2026-02-06 00:00:00',NULL,1,1);
INSERT INTO `sku` VALUES (4,23,'python编程三剑客【入门到进阶】','36688466313344',152.20,25.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/ca1fef11-9f55-4a98-b3e5-2d63bd26d5dc.png\"]',93,0,1.000000,17,NULL,'2026-02-08 23:33:28','2026-02-14 00:53:27',0,2,'2026-02-08 00:00:00','2027-02-27 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (5,23,'python编程四剑客[菜鸟变大师】','616267534860416',211.56,99.99,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/f4341123-c7ec-4c60-a4d2-6e03de1aa888.png\"]',4,0,1.000000,17,NULL,'2026-02-09 20:18:42','2026-02-14 00:55:43',0,2,'2026-02-09 00:00:00','2027-02-27 00:00:00','应孩子的要求购买的这套书。孩子说编排有逻辑，内容很新，也很全面，适合自己在家学习。书的质量也还不错，排版合理，字体大小合适清晰。',0,1);
INSERT INTO `sku` VALUES (6,23,'Python编程快速上手 （2025第3版）','623161905283200',61.40,19.90,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/d6026371-7a8d-4fdd-9cde-27c7461137a9.png\"]',222,0,2.000000,17,NULL,'2026-02-09 20:32:24','2026-02-14 00:55:57',0,2,'2026-02-09 00:00:00','2027-02-10 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (7,24,'Java从入门到精通','3843685384320',399.99,199.99,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/be2144e5-19ca-4576-b6dc-fa3b1ee78fac.png\"]',346,0,1.000000,17,NULL,'2026-02-09 20:51:12','2026-02-14 00:56:09',0,1,'2026-02-09 00:00:00','2029-02-28 00:00:00',NULL,1,1);
INSERT INTO `sku` VALUES (8,24,'Java Linux MySQL从入门到实战','4938927210624',199.00,89.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/615069bd-7e9e-42e6-a79b-54e0f1d6c494.png\"]',875,0,1.000000,17,NULL,'2026-02-09 20:53:23','2026-02-14 00:56:22',0,2,'2026-02-09 00:00:00','2032-02-29 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (9,24,'Spring Cloud 微服务开发','11454409375872',79.00,9.90,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/3e6de5ed-8128-40fa-9292-ae3f9ba000c9.png\"]',888,0,3.000000,17,NULL,'2026-02-09 21:05:29','2026-02-14 00:56:31',0,3,'2026-02-09 00:00:00','2029-04-13 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (10,25,'Apple AI笔记本 2025款 MacBookAir13英寸M4(10+10核)16G 512G 银色电脑MW0X3CH/A【企业专享】','22950006784128',9499.00,3999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/589d8a50-92a4-473a-8e66-f61fd1bf7800.png\"]',122,0,1.000000,17,NULL,'2026-02-09 21:29:10','2026-02-09 21:29:10',0,1,'2026-02-09 00:00:00','2028-04-22 00:00:00',NULL,2,1);
INSERT INTO `sku` VALUES (11,25,'Apple AI笔记本 2025款 MacBookAir13英寸M4(10+10核)24G 512G 银色电脑MC654CH/A【企业专享】','24200169422976',10999.00,5999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/3760727a-9dd8-4acd-af1a-084d0785ab43.png\"]',999,0,2.000000,17,NULL,'2026-02-09 21:31:39','2026-02-09 21:31:39',0,2,'2026-02-09 00:00:00','2027-02-11 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (12,25,'Apple AI笔记本 2025款MacBookAir13英寸M4(10+10核)24G 1T 银色电脑Z1GS0001L【企业专享】','24803436167296',12436.51,6999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/0050a1f6-27d5-4607-a336-bd1a1ff0df97.png\"]',888,0,3.000000,17,NULL,'2026-02-09 21:32:51','2026-02-09 21:32:50',0,3,'2026-02-09 00:00:00','2027-03-27 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (13,26,'低铺1.2*2m(不含垫)','7165087613056c',1499.00,555.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/80ec43d5-2f84-46ec-a2aa-42ea3e06e379.png\"]',24,0,1.000000,17,NULL,'2026-02-10 23:53:12','2026-02-10 23:53:12',0,NULL,'2026-02-10 00:00:00','2026-08-29 00:00:00',NULL,4,1);
INSERT INTO `sku` VALUES (14,27,'[NO.1水乳】胶原水+白胶原乳','36554181476480N',1650.00,988.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/8bf6cba4-1f04-4f93-9a64-0540f24616de.png\"]',36,0,2.000000,17,NULL,'2026-02-11 23:13:00','2026-02-11 23:13:10',0,1,'2026-02-11 00:00:00','2028-04-30 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (15,27,'【胶原CP】乳霜款75ml+晚霜75ml','37504669483136cp',2440.00,1499.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/63f6a8b0-6c8f-421f-9216-310a0ee2ae83.png\"]',99,0,3.000000,17,NULL,'2026-02-11 23:14:53','2026-02-11 23:14:53',0,NULL,'2026-02-11 00:00:00','2028-02-26 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (16,28,'欧莱雅黑金松露中样水乳','16610987376768',348.00,128.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/4b98b079-6450-4496-954f-ee82c9fb1b11.png\"]',78,0,3.000000,17,NULL,'2026-02-11 23:59:50','2026-02-19 23:37:35',0,NULL,'2026-02-11 00:00:00','2028-02-08 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (17,28,'欧莱雅黑金松露水乳霜五件套','17654823813248',1498.00,888.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/dce315a9-6385-461a-aa38-cda56b771f1b.png\"]',333,0,4.000000,17,NULL,'2026-02-12 00:01:55','2026-02-12 00:01:54',0,NULL,'2026-03-19 00:00:00','2028-02-26 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (18,28,'水乳精华面霜眼霜10件套+面膜','19971446964352',3745.00,754.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/19f7b621-b445-4f41-a0f1-7ebcd15c9af1.png\"]',77,0,5.000000,17,NULL,'2026-02-12 00:06:31','2026-02-12 00:06:30',0,3,'2026-02-26 00:00:00','2028-04-13 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (19,28,'欧莱雅黑金松露【全家福】','21126356959360',3822.00,1259.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/fbf71f1d-49ac-4f54-9898-532fef40ee94.png\"]',555,0,7.000000,17,NULL,'2026-02-12 00:08:48','2026-02-12 00:08:48',0,1,'2026-02-12 00:00:00','2027-02-26 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (20,29,'X1点足','126337587118208',8999.00,4567.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/bf29666a-5a43-4f62-8c78-1caa0f7aeaa0.png\"]',32,0,1.000000,17,NULL,'2026-02-19 23:37:26','2026-02-19 23:37:26',0,NULL,'2026-02-19 00:00:00','2026-02-19 00:00:00',NULL,3,1);
INSERT INTO `sku` VALUES (21,29,'X1点足长续航','130085189353600',9999.00,5999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/89090607-f62f-4236-9334-9a95f155c82a.png\"]',99,0,4.000000,17,NULL,'2026-02-19 23:44:53','2026-02-19 23:44:52',0,2,'2026-02-19 00:00:00','2032-02-28 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (22,29,'X1-W轮足','130889581363328',10999.00,6999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/1f7c8146-6022-406a-8907-a99b3f5dbf43.png\"]',999,0,5.000000,17,NULL,'2026-02-19 23:46:29','2026-02-19 23:46:28',0,3,'2026-02-19 00:00:00','2028-02-19 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (23,29,'ATOM人形机器人','131883002265728',598000.00,10999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/4d6fc8f1-388a-4b1d-9004-bb5c657a8be6.png\"]',999,0,5.000000,17,NULL,'2026-02-19 23:48:27','2026-02-19 23:48:27',0,4,'2026-02-19 00:05:25','2028-02-04 00:00:00',NULL,0,1);
INSERT INTO `sku` VALUES (24,30,'时来运转黄金项链男款足金平安扣吊坠520情人节礼物送男友老公 [京速达]足金时来运转项链','41510590513280',2329.00,1000.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/4e437a19-033a-4d99-8896-7324651f8fcb.png\"]',228,0,3.000000,3,NULL,'2026-05-10 19:47:08','2026-05-11 14:34:51',0,1,'2026-05-10 00:00:00','2027-05-10 00:03:00',NULL,0,3);
INSERT INTO `sku` VALUES (25,30,'八宝罗盘黄金项链女足金莲花吊坠520情人节礼物送女朋友老婆生日 足金八宝莲花金线项链-黑','42089035366528',2499.00,999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/4b54411d-e206-4729-9372-f0f343ddd0c4.png\"]',87,0,2.000000,3,NULL,'2026-05-10 19:48:17','2026-05-10 19:48:16',0,NULL,'2026-05-10 00:00:00','2028-05-24 00:00:00',NULL,0,3);
INSERT INTO `sku` VALUES (26,30,'无事牌黄金项链男护身符足金吊坠520情人节礼物送男友老公结婚 足金平安无事牌项链-升级版','43121371021440',5149.00,1999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/4cda7426-6489-4e0e-992e-5c73c184dbac.png\"]',110,0,3.000000,3,NULL,'2026-05-10 19:50:20','2026-05-10 19:50:19',0,NULL,'2026-05-10 00:00:00','2027-05-29 00:00:00',NULL,0,3);
INSERT INTO `sku` VALUES (27,31,'【58圈口】足金手镯 约9.55g','47301171118208',17569.00,5000.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/345b1492-d584-4fe7-8ee6-cee0b1bfc35b.png\"]',36,0,5.000000,3,NULL,'2026-05-10 19:58:38','2026-05-10 19:58:38',0,3,'2026-05-10 00:00:00','2029-05-25 00:00:00',NULL,3,3);
INSERT INTO `sku` VALUES (28,31,'【56圈口】足金手镯 约9.60g','47933336617088',17569.00,6999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/7493190b-7ab2-4721-8681-c8a8a7cb7d78.png\"]',54,0,5.000000,3,NULL,'2026-05-10 19:59:53','2026-05-10 19:59:53',0,NULL,'2026-05-06 00:00:00','2028-05-17 00:00:00',NULL,0,3);
INSERT INTO `sku` VALUES (29,31,'【60圈口】足金手镯 约9.55g','49763537944704',17569.00,NULL,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/753f841a-adc3-4e1b-ad51-46a9b6ab825f.png\"]',36,0,3.000000,3,NULL,'2026-05-10 20:03:32','2026-05-10 20:03:31',0,NULL,'2026-05-06 00:00:00','2027-05-14 00:00:00',NULL,0,3);
INSERT INTO `sku` VALUES (30,31,'【58圈口】足金手镯 约9.55g+心动礼盒','50323913736320',17569.00,6999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/4393a409-9628-408e-8487-420679b684a7.png\"]',32,0,2.000000,3,NULL,'2026-05-10 20:04:38','2026-05-10 20:04:38',0,4,'2026-05-10 00:00:00','2030-05-24 00:00:00',NULL,0,3);
INSERT INTO `sku` VALUES (31,32,'玉见桃花半镯','18710051979392',377.00,100.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/cf2d6e6f-6dff-48cb-9249-63d6a006d9c1.png\"]',99,0,3.000000,3,NULL,'2026-05-11 15:43:32','2026-05-11 15:43:31',0,NULL,'2026-05-11 00:00:00','2027-05-07 00:00:00','玉见桃花【中金礼盒】足银和田玉',0,3);
INSERT INTO `sku` VALUES (32,32,'【多幸运】锦鲤玉半镯','20195179528320',422.00,129.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/cb72683a-988e-4181-93a4-3d271e2c0d1a.png\"]',56,0,5.000000,3,NULL,'2026-05-11 15:46:29','2026-05-11 15:46:28',0,NULL,'2026-05-11 00:00:00','2027-05-28 00:00:00','【幸运锦鲤】繁花足银手镯-品牌礼盒',0,3);
INSERT INTO `sku` VALUES (33,33,'星空黑12+512GB','51279166472320',3031.00,999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/ebedb288-14d3-4a3c-aac3-09f5f6fcee4a.png\"]',67,0,1.000000,2,NULL,'2026-05-16 20:31:43','2026-05-16 20:31:42',0,NULL,'2026-05-16 00:00:00','2029-05-31 00:00:00',NULL,0,4);
INSERT INTO `sku` VALUES (34,33,'星海蓝12+512GB','51975655817344',3031.00,999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/9230631e-cf1e-4d88-bb53-594acc678e48.png\"]',0,0,1.000000,2,NULL,'2026-05-16 20:33:06','2026-05-16 20:33:05',0,NULL,'2026-05-16 00:00:00',NULL,NULL,0,4);
INSERT INTO `sku` VALUES (35,33,'星空黑12+256GB','52442490241152',2820.00,999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/4cfb5a05-b3e3-4295-b4b0-f02bd0661f60.png\"]',67,0,1.000000,2,NULL,'2026-05-16 20:34:01','2026-05-16 20:34:01',0,NULL,'2026-05-16 00:00:00',NULL,NULL,0,4);
INSERT INTO `sku` VALUES (36,33,'晨光白12+256GB','53279354552448',2820.00,999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/dc4343b7-8715-455c-b7d1-876459d94465.png\"]',87,0,2.000000,2,NULL,'2026-05-16 20:35:41','2026-05-16 20:35:41',0,NULL,'2026-05-16 00:00:00',NULL,NULL,0,4);
INSERT INTO `sku` VALUES (37,34,'Mate 80 Pro Max','59475490799744',7999.00,1599.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/34b3bb8a-d754-4004-81c4-7e0469f92c7c.png\"]',699,0,5.000000,2,NULL,'2026-05-16 20:48:00','2026-05-16 20:47:59',0,NULL,'2026-05-16 00:00:00',NULL,NULL,0,4);
INSERT INTO `sku` VALUES (38,34,'Mate 80 RS 非凡大师','59868899737728',11999.00,5999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/92d6fa6d-c218-4005-8068-3c7e601ddf82.png\"]',578,0,6.000000,2,NULL,'2026-05-16 20:48:47','2026-05-16 20:48:46',0,NULL,'2026-05-16 00:00:00',NULL,NULL,0,4);
INSERT INTO `sku` VALUES (39,34,'Mate80ProMax风驰版','60377425543296',8299.00,999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/8429e92e-bf05-4628-8e70-573634f790ea.png\"]',0,0,1.000000,2,NULL,'2026-05-16 20:49:47','2026-05-16 20:49:47',0,NULL,'2026-05-16 00:00:00',NULL,NULL,0,4);
INSERT INTO `sku` VALUES (40,35,'Pura 90 Pro','63892772388992',6499.00,4299.00,'[]',0,0,2.000000,2,NULL,'2026-05-16 20:56:46','2026-05-16 20:56:46',0,NULL,'2026-05-16 00:00:00',NULL,NULL,0,4);
INSERT INTO `sku` VALUES (41,35,'Pro Max 16GB+512GB','64399201042560',7499.00,4599.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/16281e6f-fa94-4904-85e5-aea4fa9886ea.png\"]',678,0,2.000000,2,NULL,'2026-05-16 20:57:47','2026-05-16 20:57:46',0,NULL,'2026-05-16 00:00:00',NULL,NULL,0,4);
INSERT INTO `sku` VALUES (42,36,'iPhone 17','78355898597504',5999.00,999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/10d634ad-cbfe-4e2e-ae9e-8c11322e81bf.png\"]',69,0,5.000000,35,NULL,'2026-05-16 21:25:30','2026-05-16 21:25:30',0,NULL,'2026-05-16 00:00:00',NULL,NULL,2,5);
INSERT INTO `sku` VALUES (43,36,'iPhone 17 Pro','79139931455616',7999.00,999.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/3b4d9593-190c-4478-9cd8-5f1299b6e5c1.png\"]',0,0,1.000000,35,NULL,'2026-05-16 21:27:04','2026-05-16 21:27:03',0,NULL,'2026-05-16 00:00:00',NULL,NULL,0,5);
INSERT INTO `sku` VALUES (45,38,'【极致电竞机皇】 iQOO15 Ultra','3244025741440',3749.00,1599.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/9f00fc01-fe0a-49f6-a515-dd42886d2a3b.png\"]',2,0,2.000000,35,NULL,'2026-05-22 22:36:36','2026-05-22 22:36:35',0,NULL,'2026-05-22 00:00:00',NULL,NULL,0,5);
INSERT INTO `sku` VALUES (46,39,'咸鸭蛋礼盒20枚','24830850138240',43.91,20.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/f7bac2c5-1cd8-4250-a851-503381b3e545.png\"]',11,0,2.000000,37,NULL,'2026-05-22 23:19:29','2026-05-22 23:19:29',0,NULL,'2026-05-22 00:00:00',NULL,NULL,1,7);
INSERT INTO `sku` VALUES (47,40,'【袋装】坚果礼盒','25946014908544',71.16,23.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/e3297a5d-724f-4de2-9f49-fa781a2cc537.png\"]',34,0,2.000000,37,NULL,'2026-05-22 23:21:42','2026-05-22 23:21:42',0,NULL,'2026-05-22 00:00:00',NULL,NULL,0,7);
INSERT INTO `sku` VALUES (48,41,'【5成干】风干牛肉250g/约22袋','28199136297088',37.91,23.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/3789d818-a846-4c5c-be03-81728474ed8c.png\"]',23,0,2.000000,37,NULL,'2026-05-22 23:26:11','2026-05-22 23:26:10',0,NULL,'2026-05-22 00:00:00',NULL,NULL,0,7);
INSERT INTO `sku` VALUES (49,42,'东北大米25kg家庭装','30240336281728',125.00,70.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/87ca850d-6657-4137-afb5-a381eb2f45ff.png\"]',25,0,5.000000,37,NULL,'2026-05-22 23:30:14','2026-05-22 23:30:14',0,NULL,'2026-05-22 00:00:00',NULL,NULL,0,7);
INSERT INTO `sku` VALUES (50,43,'53度500mL1瓶 飞天茅台','45355257331840',1662.01,300.00,'[\"https://gsy-ai-recommend-cloud.oss-cn-beijing.aliyuncs.com/7b92c156-6c27-41bf-aea2-81168abd785c.png\"]',34,0,1.000000,37,NULL,'2026-05-23 00:00:16','2026-05-23 00:00:16',0,NULL,'2026-05-23 00:00:00',NULL,NULL,0,7);
/*!40000 ALTER TABLE `sku` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sku_warehouse`
--

DROP TABLE IF EXISTS `sku_warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sku_warehouse` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `sku_id` bigint NOT NULL COMMENT '商品ID，关联 product 表',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID，关联 warehouse 表',
  `creator` bigint DEFAULT NULL COMMENT '创建者ID',
  `updater` bigint DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0=未删除，1=已删除',
  `tenant_id` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_warehouse` (`sku_id`,`warehouse_id`),
  KEY `idx_warehouse_id` (`warehouse_id`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品仓库关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sku_warehouse`
--

LOCK TABLES `sku_warehouse` WRITE;
/*!40000 ALTER TABLE `sku_warehouse` DISABLE KEYS */;
INSERT INTO `sku_warehouse` VALUES (41,24,20,NULL,NULL,'2026-05-11 14:34:37','2026-05-11 14:34:37',0,1);
INSERT INTO `sku_warehouse` VALUES (42,31,20,NULL,NULL,'2026-05-11 15:43:31','2026-05-11 15:43:31',0,1);
INSERT INTO `sku_warehouse` VALUES (43,32,20,NULL,NULL,'2026-05-11 15:46:28','2026-05-11 15:46:28',0,1);
INSERT INTO `sku_warehouse` VALUES (44,33,23,NULL,NULL,'2026-05-16 20:31:42','2026-05-16 20:31:42',0,1);
INSERT INTO `sku_warehouse` VALUES (45,33,22,NULL,NULL,'2026-05-16 20:31:42','2026-05-16 20:31:42',0,1);
INSERT INTO `sku_warehouse` VALUES (46,33,21,NULL,NULL,'2026-05-16 20:31:42','2026-05-16 20:31:42',0,1);
INSERT INTO `sku_warehouse` VALUES (47,34,21,NULL,NULL,'2026-05-16 20:33:05','2026-05-16 20:33:05',0,1);
INSERT INTO `sku_warehouse` VALUES (48,35,22,NULL,NULL,'2026-05-16 20:34:01','2026-05-16 20:34:01',0,1);
INSERT INTO `sku_warehouse` VALUES (49,36,23,NULL,NULL,'2026-05-16 20:35:41','2026-05-16 20:35:41',0,1);
INSERT INTO `sku_warehouse` VALUES (50,37,23,NULL,NULL,'2026-05-16 20:47:59','2026-05-16 20:47:59',0,1);
INSERT INTO `sku_warehouse` VALUES (51,37,22,NULL,NULL,'2026-05-16 20:47:59','2026-05-16 20:47:59',0,1);
INSERT INTO `sku_warehouse` VALUES (52,37,21,NULL,NULL,'2026-05-16 20:47:59','2026-05-16 20:47:59',0,1);
INSERT INTO `sku_warehouse` VALUES (53,38,23,NULL,NULL,'2026-05-16 20:48:46','2026-05-16 20:48:46',0,1);
INSERT INTO `sku_warehouse` VALUES (54,39,22,NULL,NULL,'2026-05-16 20:49:47','2026-05-16 20:49:47',0,1);
INSERT INTO `sku_warehouse` VALUES (55,39,21,NULL,NULL,'2026-05-16 20:49:47','2026-05-16 20:49:47',0,1);
INSERT INTO `sku_warehouse` VALUES (56,39,23,NULL,NULL,'2026-05-16 20:49:47','2026-05-16 20:49:47',0,1);
INSERT INTO `sku_warehouse` VALUES (57,40,23,NULL,NULL,'2026-05-16 20:56:46','2026-05-16 20:56:46',0,1);
INSERT INTO `sku_warehouse` VALUES (58,41,23,NULL,NULL,'2026-05-16 20:57:46','2026-05-16 20:57:46',0,1);
INSERT INTO `sku_warehouse` VALUES (59,42,24,NULL,NULL,'2026-05-16 21:25:30','2026-05-16 21:25:30',0,1);
INSERT INTO `sku_warehouse` VALUES (60,43,24,NULL,NULL,'2026-05-16 21:27:03','2026-05-16 21:27:03',0,1);
INSERT INTO `sku_warehouse` VALUES (62,45,24,NULL,NULL,'2026-05-22 22:36:35','2026-05-22 22:36:35',0,1);
INSERT INTO `sku_warehouse` VALUES (63,46,25,NULL,NULL,'2026-05-22 23:19:29','2026-05-22 23:19:29',0,1);
INSERT INTO `sku_warehouse` VALUES (64,47,25,NULL,NULL,'2026-05-22 23:21:42','2026-05-22 23:21:42',0,1);
INSERT INTO `sku_warehouse` VALUES (65,48,25,NULL,NULL,'2026-05-22 23:26:10','2026-05-22 23:26:10',0,1);
INSERT INTO `sku_warehouse` VALUES (66,49,25,NULL,NULL,'2026-05-22 23:30:14','2026-05-22 23:30:14',0,1);
INSERT INTO `sku_warehouse` VALUES (67,50,25,NULL,NULL,'2026-05-23 00:00:16','2026-05-23 00:00:16',0,1);
/*!40000 ALTER TABLE `sku_warehouse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag`
--

DROP TABLE IF EXISTS `tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签主键ID',
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签名称（中文名，如“热销”、“新品”、“环保”）',
  `code` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标签编码（英文或拼音，用于系统识别，如 hot_sale, new_arrival）',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签描述，用于说明标签用途或规则',
  `color` char(7) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标签颜色（前端展示用，格式：#FF9900）',
  `status` smallint NOT NULL DEFAULT '1' COMMENT '状态：1=启用，0=禁用',
  `weight` decimal(10,6) DEFAULT '1.000000' COMMENT '标签权重（用于推荐优先级或排序，如 1.5=高优先级）',
  `creator` bigint DEFAULT NULL COMMENT '创建者ID',
  `updater` bigint DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0=未删除，1=已删除',
  `sort` int DEFAULT '1',
  `composition` text COLLATE utf8mb4_unicode_ci COMMENT '材料组成',
  `place_of_origin` text COLLATE utf8mb4_unicode_ci COMMENT '产地',
  `product_weight` decimal(12,4) DEFAULT '0.0000' COMMENT '重量',
  `production_date` datetime DEFAULT NULL COMMENT '生产日期',
  `shelf_life` int DEFAULT NULL COMMENT '保质期，单位：天',
  `tenant_id` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`),
  KEY `idx_weight` (`weight`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签字典表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag`
--

LOCK TABLES `tag` WRITE;
/*!40000 ALTER TABLE `tag` DISABLE KEYS */;
INSERT INTO `tag` VALUES (3,'火爆全网','001',NULL,NULL,0,1.000000,17,NULL,'2026-02-06 23:03:35','2026-02-06 23:03:35',0,1,NULL,NULL,0.0000,NULL,NULL,1);
INSERT INTO `tag` VALUES (4,'炸裂全网','zrkb',NULL,NULL,0,1.000000,17,NULL,'2026-02-09 21:24:00','2026-02-09 21:24:00',0,1,NULL,NULL,0.0000,NULL,NULL,1);
INSERT INTO `tag` VALUES (5,'疯狂热销','SALE001917738913920',NULL,NULL,0,1.000000,17,NULL,'2026-02-10 20:57:16','2026-02-10 20:57:17',0,1,NULL,NULL,0.0000,NULL,NULL,1);
INSERT INTO `tag` VALUES (6,'限时抢购','LIMITED240761471104',NULL,NULL,0,1.000000,17,NULL,'2026-02-10 21:49:24','2026-02-10 21:49:24',0,1,NULL,NULL,0.0000,NULL,NULL,1);
INSERT INTO `tag` VALUES (7,'年度必买','BESTBUY392461058176',NULL,NULL,0,1.000000,17,NULL,'2026-02-10 22:02:34','2026-02-10 22:02:33',0,1,NULL,NULL,0.0000,NULL,NULL,1);
INSERT INTO `tag` VALUES (8,'全网首发','NEWARRIVAL696187388032',NULL,NULL,0,1.000000,17,NULL,'2026-02-10 22:03:09','2026-02-10 22:03:10',0,1,NULL,NULL,0.0000,NULL,NULL,1);
INSERT INTO `tag` VALUES (9,'抢先体验','EARLYACCESS947132596352',NULL,NULL,0,1.000000,17,NULL,'2026-02-10 22:03:39','2026-02-10 22:03:40',0,1,NULL,NULL,0.0000,NULL,NULL,1);
INSERT INTO `tag` VALUES (10,'中国黄金-时来运转','34816867205248',NULL,'#f3432',0,1.000000,3,NULL,'2026-05-10 19:33:49','2026-05-10 19:33:50',0,2,'黄金','云南',1.0000,'2026-05-10 00:00:00',NULL,3);
INSERT INTO `tag` VALUES (11,'周大生-黄金手镯繁花似锦','45385405988992',NULL,NULL,0,1.000000,3,NULL,'2026-05-10 19:54:49','2026-05-10 19:54:50',0,1,'99足金','中国',9.5500,'2026-05-10 00:00:00',NULL,3);
INSERT INTO `tag` VALUES (12,'续航超久','47395693953152',NULL,NULL,0,1.000000,2,NULL,'2026-05-16 20:23:59','2026-05-16 20:24:00',0,1,NULL,NULL,0.0000,NULL,NULL,4);
INSERT INTO `tag` VALUES (13,'超大内存','47736170774656',NULL,NULL,0,1.000000,2,NULL,'2026-05-16 20:24:40','2026-05-16 20:24:40',0,1,NULL,NULL,0.0000,NULL,NULL,4);
INSERT INTO `tag` VALUES (14,'超低优惠','77301836447872',NULL,NULL,0,1.000000,35,NULL,'2026-05-16 21:23:24','2026-05-16 21:23:25',0,1,NULL,NULL,0.0000,NULL,NULL,5);
INSERT INTO `tag` VALUES (15,'一件包邮','19973988712576',NULL,NULL,0,1.000000,37,NULL,'2026-05-22 23:09:50','2026-05-22 23:09:50',0,1,NULL,NULL,0.0000,NULL,NULL,7);
/*!40000 ALTER TABLE `tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `warehouse`
--

DROP TABLE IF EXISTS `warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `warehouse` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '仓库名称（如：北京仓、上海保税仓、华南中心仓）',
  `address` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '仓库详细地址（省市区+街道）',
  `capacity` int DEFAULT NULL COMMENT '仓库容量（单位：件，可存储商品总数量）',
  `status` smallint NOT NULL DEFAULT '0' COMMENT '仓库状态：0=禁用，1=启用',
  `admin_id` bigint DEFAULT NULL COMMENT '仓库管理员ID（关联用户表 user.id）',
  `creator` bigint DEFAULT NULL COMMENT '创建者ID',
  `updater` bigint DEFAULT NULL COMMENT '更新者ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `del_flag` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：0=未删除，1=已删除',
  `tenant_id` bigint NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_status` (`status`),
  KEY `idx_admin_id` (`admin_id`),
  KEY `idx_del_flag` (`del_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓库信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `warehouse`
--

LOCK TABLES `warehouse` WRITE;
/*!40000 ALTER TABLE `warehouse` DISABLE KEYS */;
INSERT INTO `warehouse` VALUES (1,'上海仓','上海市浦东新区3号',1000,0,1,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (2,'京享仓','北京市朝阳区2号',1200,0,102,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (3,'闪送仓','广州市天河区3号',1500,0,103,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (4,'蜂巢仓','深圳市南山区4号',800,0,104,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (5,'安达仓','杭州市西湖区5号',900,0,105,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (6,'星品仓','南京市鼓楼区6号',1100,0,106,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (7,'易购仓','成都市锦江区7号',1300,0,107,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (8,'乐购汇仓','武汉市武昌区8号',950,0,108,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (9,'顺丰源仓','西安市雁塔区9号',1000,0,109,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (10,'快递通仓','沈阳市和平区10号',1400,0,110,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (11,'瑞丰仓','重庆市渝中区11号',1250,0,111,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (12,'汇通仓','苏州市工业园区12号',1100,0,112,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (13,'腾达仓','宁波市海曙区13号',1150,0,113,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (14,'天翼仓','厦门市思明区14号',1050,0,114,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (15,'丰源仓','郑州市金水区15号',1200,0,115,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (16,'速达仓','长沙市岳麓区16号',1000,0,116,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (17,'嘉禾仓','青岛市市南区17号',950,0,117,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (18,'乐汇仓','大连市中山区18号',1100,0,118,NULL,NULL,'2025-10-23 13:20:30','2026-02-01 01:26:10',0,1);
INSERT INTO `warehouse` VALUES (20,'大理中心仓库','云南省大理市',19999,0,NULL,3,NULL,'2026-05-11 14:34:13','2026-05-11 14:34:13',0,3);
INSERT INTO `warehouse` VALUES (21,'大理市中心仓库','云南省',99999,0,NULL,2,NULL,'2026-05-16 20:17:01','2026-05-16 20:17:00',0,4);
INSERT INTO `warehouse` VALUES (22,'曲靖市中心仓库','云南省曲靖市宣威市',99999,0,NULL,2,NULL,'2026-05-16 20:17:45','2026-05-16 20:17:45',0,4);
INSERT INTO `warehouse` VALUES (23,'昆明市分拣仓','云南省昆明市盘龙区',99999,0,NULL,2,NULL,'2026-05-16 20:18:19','2026-05-16 20:18:18',0,4);
INSERT INTO `warehouse` VALUES (24,'成都仓','四川成都',88888,0,NULL,35,NULL,'2026-05-16 21:21:20','2026-05-16 21:21:19',0,5);
INSERT INTO `warehouse` VALUES (25,'曲靖仓','云南',34,0,NULL,37,NULL,'2026-05-22 23:09:05','2026-05-22 23:09:04',0,7);
/*!40000 ALTER TABLE `warehouse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'gsy-goods-cloud'
--

--
-- Dumping routines for database 'gsy-goods-cloud'
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
