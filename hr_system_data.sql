mysqldump: [Warning] Using a password on the command line interface can be insecure.
-- MySQL dump 10.13  Distrib 8.4.8, for Win64 (x86_64)
--
-- Host: localhost    Database: hr_system
-- ------------------------------------------------------
-- Server version	8.4.8

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
-- Current Database: `hr_system`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `hr_system` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `hr_system`;

--
-- Dumping data for table `hr_candidate`
--

/*!40000 ALTER TABLE `hr_candidate` DISABLE KEYS */;
INSERT INTO `hr_candidate` (`id`, `requirement_id`, `supplier_id`, `name`, `phone`, `email`, `position_name`, `expected_rate`, `resume_path`, `interview_date`, `interview_result`, `interview_remark`, `status`, `create_time`, `update_time`) VALUES (1,NULL,1,'徐正本','51018960','xuzb@cmhk.com','架构师',0.00,NULL,'2026-03-12','PENDING','',1,'2026-03-11 23:19:17','2026-03-11 23:19:17');
/*!40000 ALTER TABLE `hr_candidate` ENABLE KEYS */;

--
-- Dumping data for table `hr_contract`
--

/*!40000 ALTER TABLE `hr_contract` DISABLE KEYS */;
INSERT INTO `hr_contract` (`id`, `contract_code`, `contract_name`, `contract_type`, `supplier_id`, `personnel_id`, `project_id`, `start_date`, `end_date`, `amount`, `status`, `sign_date`, `remark`, `create_time`, `update_time`) VALUES (1,'HT2024001','华软科技外包服务合同','SUPPLIER',1,NULL,1,'2025-01-01','2026-12-31',2000000.00,1,'2025-01-01','年度框架合同','2026-03-12 13:37:20','2026-03-12 13:37:20'),(2,'HT2024002','智联外包服务合同','SUPPLIER',2,NULL,2,'2026-01-01','2026-06-30',800000.00,1,'2024-01-01','APP重构项目外包','2028-03-12 13:37:20','2026-03-13 17:36:53'),(3,'HT2024003','博达信息服务合同','SUPPLIER',3,NULL,3,'2026-02-01','2026-08-31',500000.00,1,'2024-02-01','数据平台建设','2028-03-12 13:37:20','2026-03-13 17:36:53'),(4,'HT2024004','张伟劳动合同','PERSONNEL',1,1,1,'2026-01-15','2027-01-14',316800.00,1,'2024-01-15','Java高级工程师','2028-03-12 13:37:20','2026-03-13 17:36:53'),(5,'HT2024005','李娜劳动合同','PERSONNEL',1,2,1,'2026-01-20','2027-01-19',304200.00,1,'2024-01-20','Java高级工程师','2028-03-12 13:37:20','2026-03-13 17:36:53'),(6,'HT2024006','王磊劳动合同','PERSONNEL',1,3,2,'2026-02-01','2026-07-31',118800.00,1,'2024-02-01','前端开发工程师','2028-03-12 13:37:20','2026-03-13 17:36:53'),(7,'HT2024007','赵敏劳动合同','PERSONNEL',2,4,2,'2026-02-10','2026-08-09',116160.00,1,'2024-02-10','前端开发工程师','2028-03-12 13:37:20','2026-03-13 17:36:53'),(8,'HT2024008','吴芳劳动合同','PERSONNEL',3,8,4,'2026-02-20','2028-03-07',28000.00,2,'2024-02-20','测试工程师-即将到期','2028-03-12 13:37:20','2026-03-13 17:36:53'),(9,'HT2024009','郑凯劳动合同','PERSONNEL',1,9,4,'2026-02-22','2028-03-27',27200.00,1,'2024-02-22','测试工程师-即将到期','2028-03-12 13:37:20','2026-03-13 17:36:53'),(10,'asdfasdf','ffff','SUPPLIER',2,NULL,NULL,'2026-03-10',NULL,110.00,1,'2026-03-20','','2026-03-12 17:24:29','2026-03-12 17:24:29');
/*!40000 ALTER TABLE `hr_contract` ENABLE KEYS */;

--
-- Dumping data for table `hr_contract_attachment`
--

/*!40000 ALTER TABLE `hr_contract_attachment` DISABLE KEYS */;
/*!40000 ALTER TABLE `hr_contract_attachment` ENABLE KEYS */;

--
-- Dumping data for table `hr_department`
--

/*!40000 ALTER TABLE `hr_department` DISABLE KEYS */;
INSERT INTO `hr_department` (`id`, `dept_name`, `parent_id`, `manager_id`, `level`, `sort_order`, `status`, `create_time`, `update_time`, `deleted`) VALUES (1,'总经理',NULL,2,1,1,1,'2026-03-13 11:37:54','2026-03-13 11:37:54',0),(2,'技术副总',1,3,2,1,1,'2026-03-13 11:37:54','2026-03-13 11:37:54',0),(3,'人力副总',1,4,2,2,1,'2026-03-13 11:37:54','2026-03-13 11:37:54',0),(4,'财务副总',1,5,2,3,1,'2026-03-13 11:37:54','2026-03-13 11:37:54',0),(5,'研发部',2,6,3,1,1,'2026-03-13 11:37:54','2026-03-13 11:37:54',0),(6,'测试部',2,7,3,2,1,'2026-03-13 11:37:54','2026-03-13 11:37:54',0),(7,'人力资源部',3,8,3,1,1,'2026-03-13 11:37:54','2026-03-13 11:37:54',0),(8,'财务部',4,9,3,1,1,'2026-03-13 11:37:54','2026-03-13 11:37:54',0);
/*!40000 ALTER TABLE `hr_department` ENABLE KEYS */;

--
-- Dumping data for table `hr_evaluation`
--

/*!40000 ALTER TABLE `hr_evaluation` DISABLE KEYS */;
INSERT INTO `hr_evaluation` (`id`, `personnel_id`, `project_id`, `evaluator`, `evaluation_month`, `score`, `work_quality`, `communication`, `punctuality`, `remark`, `create_time`, `update_time`) VALUES (1,1,1,'李明','2024-01',92.5,95.0,90.0,92.0,'表现优秀，技术能力强','2026-03-12 13:37:20','2026-03-12 13:37:20'),(2,2,1,'李明','2024-01',88.0,90.0,85.0,89.0,'工作认真负责','2026-03-12 13:37:20','2026-03-12 13:37:20'),(3,3,2,'王强','2024-02',85.0,88.0,82.0,85.0,'前端技术扎实','2026-03-12 13:37:20','2026-03-12 13:37:20'),(4,11,1,'李明','2024-01',90.0,92.0,88.0,90.0,'中级工程师表现良好','2026-03-12 13:37:20','2026-03-12 13:37:20');
/*!40000 ALTER TABLE `hr_evaluation` ENABLE KEYS */;

--
-- Dumping data for table `hr_message`
--

/*!40000 ALTER TABLE `hr_message` DISABLE KEYS */;
INSERT INTO `hr_message` (`id`, `user_id`, `title`, `content`, `type`, `is_read`, `create_time`) VALUES (1,1,'合同到期提醒','您有2个合同即将在30天内到期，请及时处理','REMINDER',0,'2026-03-12 13:37:20'),(2,1,'结算审批通知','结算单SET202401002已提交审批','NOTICE',1,'2026-03-12 13:37:20'),(3,2,'新人员入职','孙华、周杰将于3月1日入职','NOTICE',0,'2026-03-12 13:37:20');
/*!40000 ALTER TABLE `hr_message` ENABLE KEYS */;

--
-- Dumping data for table `hr_operation_log`
--

/*!40000 ALTER TABLE `hr_operation_log` DISABLE KEYS */;
INSERT INTO `hr_operation_log` (`id`, `user_id`, `username`, `module`, `action`, `target_type`, `target_id`, `detail`, `ip`, `create_time`) VALUES (1,1,'admin','人员管理','新增','人员',6,'新增人员: 孙华','127.0.0.1','2026-03-12 13:37:20'),(2,1,'admin','人员管理','新增','人员',7,'新增人员: 周杰','127.0.0.1','2026-03-12 13:37:20'),(3,1,'admin','合同管理','新增','合同',1,'新增合同: HT2024001','127.0.0.1','2026-03-12 13:37:20'),(4,2,'hr001','工时管理','审批','工时',1,'审批通过工时记录','127.0.0.1','2026-03-12 13:37:20'),(5,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=123456); ','127.0.0.1','2026-03-12 14:25:24'),(6,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456); ','127.0.0.1','2026-03-12 14:34:10'),(7,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456); ','127.0.0.1','2026-03-12 14:34:11'),(8,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456); ','127.0.0.1','2026-03-12 14:34:12'),(9,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456); ','127.0.0.1','2026-03-12 14:34:22'),(10,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456123); ','127.0.0.1','2026-03-12 14:34:29'),(11,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin23); ','127.0.0.1','2026-03-12 14:34:40'),(12,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 14:34:45'),(13,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 14:34:48'),(14,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 14:34:50'),(15,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-12 14:35:10'),(16,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=123456); ','127.0.0.1','2026-03-12 14:36:18'),(17,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=123456); ','127.0.0.1','2026-03-12 14:37:00'),(18,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=123456); ','127.0.0.1','2026-03-12 15:14:56'),(19,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-12 15:15:52'),(20,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 15:15:59'),(21,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 15:16:18'),(22,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=123456); ','127.0.0.1','2026-03-12 15:16:56'),(23,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=123456); ','127.0.0.1','2026-03-12 15:17:28'),(24,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 15:18:00'),(25,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 15:18:33'),(26,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456); ','127.0.0.1','2026-03-12 15:18:40'),(27,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456); ','127.0.0.1','2026-03-12 15:19:08'),(28,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=123456); ','127.0.0.1','2026-03-12 15:19:21'),(29,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 16:02:00'),(30,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456); ','127.0.0.1','2026-03-12 16:02:05'),(31,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 17:17:03'),(32,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 17:17:25'),(33,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 17:17:42'),(34,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 17:18:50'),(35,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456); ','127.0.0.1','2026-03-12 17:18:57'),(36,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=ceo, password=admin123); ','127.0.0.1','2026-03-12 17:19:00'),(37,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=ceo, password=admin123); ','127.0.0.1','2026-03-12 17:19:01'),(38,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-12 17:19:03'),(39,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 17:19:05'),(40,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 17:21:02'),(41,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 17:21:52'),(42,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 17:32:01'),(43,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 17:32:26'),(44,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_hr, password=admin123); ','127.0.0.1','2026-03-12 19:12:31'),(45,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=ceo, password=admin123); ','127.0.0.1','2026-03-12 19:12:33'),(46,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 19:12:34'),(47,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-12 19:13:01'),(48,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 19:14:33'),(49,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 19:14:48'),(50,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 19:15:03'),(51,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 19:17:12'),(52,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 19:24:24'),(53,22,'系统管理员','人员管理','编辑人员','人员',NULL,'13; HrPersonnel(id=13, personnelCode=PER202401013, name=杨帆, idCard=310101200203033456, phone=13900139013, email=yangfan@example.com, positionName=运维工程师, positionLevel=P6, dailyRate=900, supplierId=2, projectId=5, requirementId=null, status=LEAVE, entryDate=2024-01-05, contractStartDate=2024-01-05, contractEndDate=2026-07-02, zentaoUserId=null, zentaoAccount=yangfan, createTime=2026-03-11T18:01:24, updateTime=2026-03-11T18:01:24, deleted=0); ','127.0.0.1','2026-03-12 21:55:03'),(54,22,'系统管理员','人员管理','人员入场','人员',NULL,'6; ','127.0.0.1','2026-03-12 21:56:01'),(55,22,'系统管理员','人员管理','人员入场','人员',NULL,'7; ','127.0.0.1','2026-03-12 21:56:05'),(56,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_hr, password=admin123); ','127.0.0.1','2026-03-12 21:59:07'),(57,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=dept_dev, password=admin123); ','127.0.0.1','2026-03-12 21:59:08'),(58,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=ceo, password=admin123); ','127.0.0.1','2026-03-12 21:59:10'),(59,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-12 21:59:11'),(60,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-12 21:59:12'),(61,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=dept_dev, password=admin123); ','127.0.0.1','2026-03-12 22:16:01'),(62,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 22:16:30'),(63,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=dept_dev, password=admin123); ','127.0.0.1','2026-03-12 22:52:18'),(64,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-12 22:52:20'),(65,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 22:52:20'),(66,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 22:52:25'),(67,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-12 22:52:29'),(68,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-12 22:52:38'),(69,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=pm_erp, password=admin123); ','127.0.0.1','2026-03-12 22:52:39'),(70,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=dept_dev, password=admin123); ','127.0.0.1','2026-03-12 22:52:40'),(71,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-12 22:52:40'),(72,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=admin123); ','127.0.0.1','2026-03-12 22:52:41'),(73,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 22:52:42'),(74,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 22:53:17'),(75,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=admin123); ','127.0.0.1','2026-03-12 22:53:19'),(76,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-12 22:53:22'),(77,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-12 23:00:19'),(78,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=dept_dev, password=admin123); ','127.0.0.1','2026-03-12 23:00:20'),(79,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=pm_erp, password=admin123); ','127.0.0.1','2026-03-12 23:00:21'),(80,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-12 23:00:21'),(81,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 23:01:41'),(82,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=admin123); ','127.0.0.1','2026-03-12 23:01:42'),(83,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-12 23:01:42'),(84,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=dept_dev, password=admin123); ','127.0.0.1','2026-03-12 23:01:43'),(85,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=pm_erp, password=admin123); ','127.0.0.1','2026-03-12 23:01:43'),(86,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-12 23:01:44'),(87,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-12 23:02:33'),(88,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-12 23:06:46'),(89,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 10:04:53'),(90,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-13 10:05:00'),(91,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=123456); ','127.0.0.1','2026-03-13 10:05:24'),(92,8,'孙丽娜','人员管理','人员调配','人员',NULL,'5; 4; ','127.0.0.1','2026-03-13 10:05:48'),(93,8,'孙丽娜','人员管理','人员离场','人员',NULL,'7; ','127.0.0.1','2026-03-13 10:05:51'),(94,8,'孙丽娜','人员管理','人员入场','人员',NULL,'15; ','127.0.0.1','2026-03-13 10:06:07'),(95,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 10:07:10'),(96,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456); ','127.0.0.1','2026-03-13 10:07:13'),(97,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 10:07:15'),(98,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=123456); ','127.0.0.1','2026-03-13 10:07:19'),(99,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 10:07:29'),(100,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-13 10:07:31'),(101,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=pm_erp, password=admin123); ','127.0.0.1','2026-03-13 10:07:32'),(102,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=admin123); ','127.0.0.1','2026-03-13 10:07:33'),(103,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 10:37:42'),(104,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=admin123); ','127.0.0.1','2026-03-13 10:37:43'),(105,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-13 10:37:44'),(106,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=dept_dev, password=admin123); ','127.0.0.1','2026-03-13 10:37:44'),(107,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=pm_erp, password=admin123); ','127.0.0.1','2026-03-13 10:37:44'),(108,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-13 10:37:45'),(109,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 10:49:15'),(110,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-13 10:49:16'),(111,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=dept_dev, password=admin123); ','127.0.0.1','2026-03-13 10:49:17'),(112,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=pm_erp, password=admin123); ','127.0.0.1','2026-03-13 10:49:18'),(113,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=hr001, password=admin123); ','127.0.0.1','2026-03-13 10:49:19'),(114,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 10:49:21'),(115,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 10:49:36'),(116,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 10:54:30'),(117,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 10:54:51'),(118,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 11:09:31'),(119,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=pm_erp, password=admin123); ','127.0.0.1','2026-03-13 11:09:52'),(120,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-13 11:10:00'),(121,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 11:40:10'),(122,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=gm001, password=admin123); ','127.0.0.1','2026-03-13 11:41:52'),(123,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=vp_tech, password=admin123); ','127.0.0.1','2026-03-13 11:41:53'),(124,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=dept_dev, password=admin123); ','127.0.0.1','2026-03-13 11:43:42'),(125,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 14:48:44'),(126,1,'系统管理员','人员管理','人员离场','人员',NULL,'2; ','127.0.0.1','2026-03-13 14:49:22'),(127,NULL,NULL,'系统管理','登录','用户',NULL,'LoginDTO(username=admin, password=admin123); ','127.0.0.1','2026-03-13 16:58:50');
/*!40000 ALTER TABLE `hr_operation_log` ENABLE KEYS */;

--
-- Dumping data for table `hr_payment`
--

/*!40000 ALTER TABLE `hr_payment` DISABLE KEYS */;
INSERT INTO `hr_payment` (`id`, `payment_code`, `supplier_id`, `settlement_id`, `amount`, `payment_date`, `payment_method`, `voucher_path`, `status`, `remark`, `create_time`, `update_time`) VALUES (1,'PAY202401001',1,1,158400.00,'2026-01-31','BANK',NULL,1,'1月份结算付款','2028-03-12 13:37:20','2026-03-13 17:36:53'),(2,'PAY202402001',1,NULL,500000.00,'2026-02-15','BANK',NULL,1,'预付款','2028-03-12 13:37:20','2026-03-13 17:36:53'),(3,'PAY202402002',2,NULL,200000.00,'2026-02-20','BANK',NULL,1,'项目启动款','2028-03-12 13:37:20','2026-03-13 17:36:53'),(4,'PAY202403001',3,NULL,100000.00,'2026-03-01','BANK',NULL,1,'首期付款','2028-03-12 13:37:20','2026-03-13 17:36:53');
/*!40000 ALTER TABLE `hr_payment` ENABLE KEYS */;

--
-- Dumping data for table `hr_personnel`
--

/*!40000 ALTER TABLE `hr_personnel` DISABLE KEYS */;
INSERT INTO `hr_personnel` (`id`, `personnel_code`, `name`, `id_card`, `phone`, `email`, `position_name`, `position_level`, `daily_rate`, `supplier_id`, `project_id`, `requirement_id`, `status`, `entry_date`, `contract_start_date`, `contract_end_date`, `zentao_user_id`, `zentao_account`, `create_time`, `update_time`, `deleted`) VALUES (1,'PER202401001','张伟','310101199001011234','13900139001','zhangwei@example.com','Java高级工程师','P7',1200.00,1,1,1,'ON_DUTY','2026-01-15','2026-01-15','2027-01-14',NULL,'zhangwei','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(2,'PER202401002','李娜','310101199102022345','13900139002','lina@example.com','Java高级工程师','P7',1150.00,1,1,1,'OFF_DUTY','2026-01-20','2026-01-20','2027-01-19',NULL,'lina','2028-03-11 18:01:24','2026-03-13 14:49:22',0),(3,'PER202401003','王磊','310101199203033456','13900139003','wanglei@example.com','前端开发工程师','P6',900.00,1,2,2,'ON_DUTY','2026-02-01','2026-02-01','2026-07-31',NULL,'wanglei','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(4,'PER202401004','赵敏','310101199304044567','13900139004','zhaomin@example.com','前端开发工程师','P6',880.00,2,2,2,'ON_DUTY','2026-02-10','2026-02-10','2026-08-09',NULL,'zhaomin','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(5,'PER202401005','钱进','310101199405055678','13900139005','qianjin@example.com','前端开发工程师','P5',750.00,2,4,2,'TRANSFER','2026-02-15','2026-02-15','2026-08-14',NULL,'qianjin','2028-03-11 18:01:24','2026-03-13 10:05:48',0),(6,'PER202401006','孙华','310101199506066789','13900139006','sunhua@example.com','数据分析师','P6',1000.00,2,3,3,'ON_DUTY','2026-03-12','2024-03-01','2024-10-31',NULL,'sunhua','2026-03-11 18:01:24','2026-03-12 21:56:01',0),(7,'PER202401007','周杰','310101199607077890','13900139007','zhoujie@example.com','数据分析师','P5',850.00,3,3,3,'OFF_DUTY','2026-03-12','2024-03-01','2024-10-31',NULL,'zhoujie','2026-03-11 18:01:24','2026-03-13 10:05:51',0),(8,'PER202401008','吴芳','310101199708088901','13900139008','wufang@example.com','测试工程师','P5',700.00,3,4,4,'ON_DUTY','2026-02-20','2026-02-20','2026-06-19',NULL,'wufang','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(9,'PER202401009','郑凯','310101199809099012','13900139009','zhengkai@example.com','测试工程师','P5',680.00,1,4,4,'ON_DUTY','2026-02-22','2026-02-22','2026-06-21',NULL,'zhengkai','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(10,'PER202401010','陈晨','310101199910101123','13900139010','chenchen@example.com','产品经理','P7',1500.00,1,1,5,'OFF_DUTY','2026-01-10','2026-01-10','2027-01-09',NULL,'chenchen','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(11,'PER202401011','林涛','310101200001011234','13900139011','lintao@example.com','Java中级工程师','P6',950.00,2,1,1,'ON_DUTY','2026-02-01','2026-02-01','2027-01-31',NULL,'lintao','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(12,'PER202401012','黄丽','310101200102022345','13900139012','huangli@example.com','UI设计师','P5',800.00,3,2,2,'ON_DUTY','2026-02-05','2026-02-05','2026-08-04',NULL,'huangli','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(13,'PER202401013','杨帆','310101200203033456','13900139013','yangfan@example.com','运维工程师','P6',900.00,2,5,NULL,'LEAVE','2026-01-05','2026-01-05','2028-07-02',NULL,'yangfan','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(14,'PER202401014','徐明','310101200304044567','13900139014','xuming@example.com','Java高级工程师','P7',1100.00,1,1,1,'TRANSFER','2026-01-20','2026-01-20','2027-01-19',NULL,'xuming','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(15,'PER202401015','马超','310101200405055678','13900139015','machao@example.com','前端开发工程师','P6',860.00,3,NULL,NULL,'ON_DUTY','2026-03-13','2024-03-01','2024-08-31',NULL,'machao','2026-03-11 18:01:24','2026-03-13 10:06:07',0);
/*!40000 ALTER TABLE `hr_personnel` ENABLE KEYS */;

--
-- Dumping data for table `hr_personnel_asset`
--

/*!40000 ALTER TABLE `hr_personnel_asset` DISABLE KEYS */;
INSERT INTO `hr_personnel_asset` (`id`, `personnel_id`, `asset_name`, `asset_code`, `receive_date`, `return_date`, `status`, `remark`, `create_time`, `update_time`) VALUES (1,13,'笔记本电脑','','2026-03-03',NULL,1,'','2026-03-12 21:55:49','2026-03-12 21:55:49');
/*!40000 ALTER TABLE `hr_personnel_asset` ENABLE KEYS */;

--
-- Dumping data for table `hr_personnel_salary`
--

/*!40000 ALTER TABLE `hr_personnel_salary` DISABLE KEYS */;
INSERT INTO `hr_personnel_salary` (`id`, `personnel_id`, `salary_type`, `amount`, `effective_date`, `reason`, `create_time`, `update_time`) VALUES (1,13,'MONTHLY',23990.00,'2026-03-15','转正调薪','2026-03-12 21:55:33','2026-03-12 21:55:33');
/*!40000 ALTER TABLE `hr_personnel_salary` ENABLE KEYS */;

--
-- Dumping data for table `hr_project`
--

/*!40000 ALTER TABLE `hr_project` DISABLE KEYS */;
INSERT INTO `hr_project` (`id`, `project_code`, `project_name`, `department`, `manager`, `party_a`, `party_b`, `budget`, `zentao_project_id`, `status`, `create_time`, `update_time`, `deleted`) VALUES (1,'PRJ2024001','核心交易系统升级','研发部','李明',NULL,NULL,500000.00,NULL,1,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(2,'PRJ2024002','移动端APP重构','研发部','王强',NULL,NULL,300000.00,NULL,1,'2026-03-11 18:01:24','2026-03-13 14:49:03',1),(3,'PRJ2024003','数据分析平台','数据部','钱进',NULL,NULL,400000.00,NULL,1,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(4,'PRJ2024004','客服系统优化','产品部','孙华',NULL,NULL,200000.00,NULL,1,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(5,'PRJ2024005','运维监控平台','运维部','陈刚',NULL,NULL,250000.00,NULL,0,'2026-03-11 18:01:24','2026-03-11 18:01:24',0);
/*!40000 ALTER TABLE `hr_project` ENABLE KEYS */;

--
-- Dumping data for table `hr_project_milestone`
--

/*!40000 ALTER TABLE `hr_project_milestone` DISABLE KEYS */;
/*!40000 ALTER TABLE `hr_project_milestone` ENABLE KEYS */;

--
-- Dumping data for table `hr_requirement`
--

/*!40000 ALTER TABLE `hr_requirement` DISABLE KEYS */;
INSERT INTO `hr_requirement` (`id`, `requirement_code`, `project_name`, `position_name`, `position_level`, `skills`, `expected_entry_date`, `service_months`, `daily_rate`, `monthly_budget`, `total_budget`, `demand_count`, `demand_reason`, `work_content`, `project_roi`, `department_id`, `project_id`, `status`, `reject_reason`, `create_time`, `update_time`, `deleted`, `workflow_instance_id`, `workflow_status`) VALUES (1,'RQ202401001','核心交易系统升级','Java高级工程师','P7','Java,Spring Boot,MySQL,分布式','2026-02-01',12,1200.00,26400.00,316800.00,2,'系统架构升级，需要资深Java开发','负责核心交易模块的设计与开发',NULL,1,1,4,NULL,'2028-03-11 18:01:24','2026-03-11 18:01:24',0,NULL,NULL),(2,'RQ202401002','移动端APP重构','前端开发工程师','P6','Vue3,TypeScript,移动端开发','2026-02-15',6,900.00,19800.00,118800.00,3,'APP重构项目启动','负责移动端页面开发',NULL,1,2,4,NULL,'2028-03-11 18:01:24','2026-03-11 18:01:24',0,NULL,NULL),(3,'RQ202401003','数据分析平台','数据分析师','P6','Python,SQL,数据分析','2026-03-01',8,1000.00,22000.00,176000.00,2,'数据平台建设需要专业人员','数据建模与分析报告',NULL,1,3,2,NULL,'2028-03-11 18:01:24','2026-03-11 18:01:24',0,NULL,NULL),(4,'RQ202401004','客服系统优化','测试工程师','P5','自动化测试,接口测试','2026-02-20',4,700.00,15400.00,61600.00,2,'系统优化需要测试支持','功能测试与自动化测试',NULL,1,4,1,NULL,'2028-03-11 18:01:24','2026-03-11 18:01:24',0,NULL,'APPROVED'),(5,'RQ202401005','核心交易系统升级','产品经理','P7','产品规划,需求分析,金融行业','2026-01-15',12,1500.00,33000.00,396000.00,1,'需要资深产品经理把控方向','产品规划与需求管理',NULL,1,1,1,NULL,'2028-03-11 18:01:24','2026-03-11 18:01:24',0,NULL,'APPROVED'),(6,'RQ202401001','核心交易系统升级','Java高级工程师','P7','Java,Spring Boot,MySQL,分布式','2026-02-01',12,1200.00,26400.00,316800.00,2,'系统架构升级，需要资深Java开发','负责核心交易模块的设计与开发',NULL,1,1,1,NULL,'2028-03-12 13:37:20','2026-03-12 13:37:20',0,NULL,'APPROVED'),(7,'RQ202401002','移动端APP重构','前端开发工程师','P6','Vue3,TypeScript,移动端开发','2026-02-15',6,900.00,19800.00,118800.00,3,'APP重构项目启动','负责移动端页面开发',NULL,1,2,2,NULL,'2028-03-12 13:37:20','2026-03-12 13:37:20',0,NULL,'REJECTED'),(8,'RQ202401003','数据分析平台','数据分析师','P6','Python,SQL,数据分析','2026-03-01',8,1000.00,22000.00,176000.00,2,'数据平台建设需要专业人员','数据建模与分析报告',NULL,1,3,2,NULL,'2028-03-12 13:37:20','2026-03-12 13:37:20',0,NULL,NULL),(9,'RQ202401004','客服系统优化','测试工程师','P5','自动化测试,接口测试','2026-02-20',4,700.00,15400.00,61600.00,2,'系统优化需要测试支持','功能测试与自动化测试',NULL,1,4,1,NULL,'2028-03-12 13:37:20','2026-03-12 13:37:20',0,NULL,NULL),(10,'RQ202401005','核心交易系统升级','产品经理','P7','产品规划,需求分析,金融行业','2026-01-15',12,1500.00,33000.00,396000.00,1,'需要资深产品经理把控方向','产品规划与需求管理',NULL,1,1,5,NULL,'2028-03-12 13:37:20','2026-03-12 13:37:20',0,NULL,NULL);
/*!40000 ALTER TABLE `hr_requirement` ENABLE KEYS */;

--
-- Dumping data for table `hr_settlement`
--

/*!40000 ALTER TABLE `hr_settlement` DISABLE KEYS */;
INSERT INTO `hr_settlement` (`id`, `settlement_code`, `settlement_month`, `settlement_year`, `supplier_id`, `project_id`, `total_amount`, `valid_days`, `deduction_amount`, `final_amount`, `status`, `confirm_remark`, `create_time`, `update_time`, `deleted`) VALUES (1,'SET202401001',1,2026,1,1,158400.00,22.00,0.00,158400.00,5,'已支付','2028-03-11 18:01:24','2026-03-11 18:01:24',0),(2,'SET202401002',1,2026,2,2,79200.00,22.00,2400.00,76800.00,3,NULL,'2028-03-11 18:01:24','2026-03-11 18:01:24',0),(3,'SET202401003',1,2026,3,4,30800.00,22.00,0.00,30800.00,1,NULL,'2028-03-11 18:01:24','2026-03-11 18:01:24',0),(4,'SET202401001',1,2026,1,1,158400.00,22.00,0.00,158400.00,5,'已支付','2028-03-12 13:37:20','2026-03-12 13:37:20',0),(5,'SET202401002',1,2026,2,2,79200.00,22.00,2400.00,76800.00,3,NULL,'2028-03-12 13:37:20','2026-03-12 13:37:20',0),(6,'SET202401003',1,2026,3,4,30800.00,22.00,0.00,30800.00,1,NULL,'2028-03-12 13:37:20','2026-03-12 13:37:20',0);
/*!40000 ALTER TABLE `hr_settlement` ENABLE KEYS */;

--
-- Dumping data for table `hr_supplier`
--

/*!40000 ALTER TABLE `hr_supplier` DISABLE KEYS */;
INSERT INTO `hr_supplier` (`id`, `supplier_name`, `contact_person`, `contact_phone`, `contact_email`, `level`, `delivery_rate`, `current_person_count`, `status`, `create_time`, `update_time`, `deleted`) VALUES (1,'华软科技有限公司1','张经理','13800138001','zhang@huaruan.com','A',95,45,1,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(2,'智联外包服务有限公司2','李经理','13800138002','li@zhilian.com','B',92,38,0,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(3,'博达信息技术有限公司3','王经理','13800138003','wang@boda.com','B',85,28,1,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(7,'aaa','aaaa','1234123123','xuzb@cmhk.com','B',0,0,1,'2026-03-13 10:06:24','2026-03-13 14:49:09',1);
/*!40000 ALTER TABLE `hr_supplier` ENABLE KEYS */;

--
-- Dumping data for table `hr_sync_log`
--

/*!40000 ALTER TABLE `hr_sync_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `hr_sync_log` ENABLE KEYS */;

--
-- Dumping data for table `hr_work_hours`
--

/*!40000 ALTER TABLE `hr_work_hours` DISABLE KEYS */;
INSERT INTO `hr_work_hours` (`id`, `personnel_id`, `project_id`, `work_date`, `hours`, `status`, `remark`, `create_time`, `update_time`, `deleted`) VALUES (1,1,1,'2024-01-15',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(2,1,1,'2024-01-16',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(3,1,1,'2024-01-17',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(4,1,1,'2024-01-18',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(5,1,1,'2024-01-19',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(6,2,1,'2024-01-20',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(7,2,1,'2024-01-21',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(8,2,1,'2024-01-22',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(9,2,1,'2024-01-23',8.00,'PENDING',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(10,3,2,'2024-01-15',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(11,3,2,'2024-01-16',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(12,3,2,'2024-01-17',4.00,'APPROVED','下午请假','2026-03-11 18:01:24','2026-03-11 18:01:24',0),(13,11,1,'2024-01-15',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(14,11,1,'2024-01-16',8.00,'APPROVED',NULL,'2026-03-11 18:01:24','2026-03-11 18:01:24',0),(15,1,1,'2024-01-15',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(16,1,1,'2024-01-16',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(17,1,1,'2024-01-17',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(18,1,1,'2024-01-18',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(19,1,1,'2024-01-19',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(20,2,1,'2024-01-20',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(21,2,1,'2024-01-21',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(22,2,1,'2024-01-22',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(23,2,1,'2024-01-23',8.00,'PENDING',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(24,3,2,'2024-01-15',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(25,3,2,'2024-01-16',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(26,3,2,'2024-01-17',4.00,'APPROVED','下午请假','2026-03-12 13:37:20','2026-03-12 13:37:20',0),(27,11,1,'2024-01-15',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0),(28,11,1,'2024-01-16',8.00,'APPROVED',NULL,'2026-03-12 13:37:20','2026-03-12 13:37:20',0);
/*!40000 ALTER TABLE `hr_work_hours` ENABLE KEYS */;

--
-- Dumping data for table `sys_dict_data`
--

/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` (`id`, `dict_type`, `dict_label`, `dict_value`, `status`, `sort_order`, `remark`, `create_time`, `update_time`) VALUES (1,'PROJECT_TYPE','内部服务项目','INTERNAL',1,1,NULL,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(2,'PROJECT_TYPE','兄弟单位服务项目','SIBLING',1,2,NULL,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(3,'PROJECT_TYPE','对外服务项目','EXTERNAL',1,3,NULL,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(4,'PROJECT_TYPE','对外采购项目','PURCHASE',1,4,NULL,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(5,'PROJECT_TYPE','人力外包项目','OUTSOURCING',1,5,NULL,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(6,'PARTY_TYPE','甲方','PARTY_A',1,1,NULL,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(7,'PARTY_TYPE','乙方','PARTY_B',1,2,NULL,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(8,'SUPPLIER_LEVEL','A级','A',1,1,NULL,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(9,'SUPPLIER_LEVEL','B级','B',1,2,NULL,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(10,'SUPPLIER_LEVEL','C级','C',1,3,NULL,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(11,'PARTY_TYPE','bingfang','3',1,3,'','2026-03-11 20:41:30','2026-03-11 20:41:30');
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;

--
-- Dumping data for table `sys_dict_type`
--

/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` (`id`, `dict_type`, `dict_name`, `status`, `sort_order`, `create_time`, `update_time`) VALUES (1,'PROJECT_TYPE','项目类别',1,1,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(2,'PARTY_TYPE','客商',1,2,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(3,'SUPPLIER_LEVEL','供应商等级',1,3,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(4,'PERSONNEL_STATUS','人员状态',1,4,'2026-03-11 20:02:36','2026-03-11 20:02:36'),(5,'REQUIREMENT_STATUS','需求状态',1,5,'2026-03-11 20:02:36','2026-03-11 20:02:36');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;

--
-- Dumping data for table `sys_menu`
--

/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` (`id`, `parent_id`, `menu_name`, `menu_code`, `path`, `icon`, `sort_order`, `menu_type`, `visible`, `create_time`, `update_time`) VALUES (1,0,'仪表盘','dashboard','/dashboard','DataAnalysis',1,1,1,'2026-03-11 18:01:24','2026-03-11 18:01:24'),(2,0,'需求管理','requirements','/requirements','Document',2,1,1,'2026-03-11 18:01:24','2026-03-11 18:01:24'),(3,0,'供应商管理','suppliers','/suppliers','OfficeBuilding',3,1,1,'2026-03-11 18:01:24','2026-03-11 18:01:24'),(4,0,'人员管理','personnel','/personnel','User',4,1,1,'2026-03-11 18:01:24','2026-03-11 18:01:24'),(5,0,'工时管理','workhours','/workhours','Clock',5,1,1,'2026-03-11 18:01:24','2026-03-11 18:01:24'),(6,0,'结算管理','settlements','/settlements','Money',6,1,1,'2026-03-11 18:01:24','2026-03-11 18:01:24'),(7,0,'报表中心','reports','/reports','PieChart',7,1,1,'2026-03-11 18:01:24','2026-03-11 18:01:24'),(8,0,'系统设置','settings','/settings','Setting',8,1,1,'2026-03-11 18:01:24','2026-03-11 18:01:24'),(9,8,'用户管理','users','/settings/users','User',1,1,1,'2026-03-11 18:01:24','2026-03-11 18:01:24'),(10,8,'数据同步','sync','/settings/sync','Refresh',2,1,1,'2026-03-11 18:01:24','2026-03-11 18:01:24');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;

--
-- Dumping data for table `sys_role_menu`
--

/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` (`id`, `role`, `menu_id`, `create_time`) VALUES (45,'GM',1,'2026-03-12 14:03:16'),(46,'GM',2,'2026-03-12 14:03:16'),(47,'GM',3,'2026-03-12 14:03:16'),(48,'GM',4,'2026-03-12 14:03:16'),(49,'GM',5,'2026-03-12 14:03:16'),(50,'GM',6,'2026-03-12 14:03:16'),(51,'GM',7,'2026-03-12 14:03:16'),(52,'GM',8,'2026-03-12 14:03:16'),(53,'GM',9,'2026-03-12 14:03:16'),(54,'GM',10,'2026-03-12 14:03:16'),(60,'VP',1,'2026-03-12 14:03:16'),(61,'VP',2,'2026-03-12 14:03:16'),(62,'VP',3,'2026-03-12 14:03:16'),(63,'VP',4,'2026-03-12 14:03:16'),(64,'VP',5,'2026-03-12 14:03:16'),(65,'VP',6,'2026-03-12 14:03:16'),(66,'VP',7,'2026-03-12 14:03:16'),(67,'DEPT_HEAD',1,'2026-03-12 14:03:16'),(68,'DEPT_HEAD',2,'2026-03-12 14:03:16'),(69,'DEPT_HEAD',4,'2026-03-12 14:03:16'),(70,'DEPT_HEAD',5,'2026-03-12 14:03:16'),(71,'DEPT_HEAD',6,'2026-03-12 14:03:16'),(72,'DEPT_HEAD',7,'2026-03-12 14:03:16'),(73,'PM',1,'2026-03-12 14:03:16'),(74,'PM',4,'2026-03-12 14:03:16'),(75,'PM',5,'2026-03-12 14:03:16'),(76,'PM',7,'2026-03-12 14:03:16'),(77,'HR',1,'2026-03-12 14:03:16'),(78,'HR',2,'2026-03-12 14:03:16'),(79,'HR',3,'2026-03-12 14:03:16'),(80,'HR',4,'2026-03-12 14:03:16'),(81,'HR',5,'2026-03-12 14:03:16'),(82,'HR',6,'2026-03-12 14:03:16'),(83,'HR',7,'2026-03-12 14:03:16');
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;

--
-- Dumping data for table `sys_user`
--

/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `role`, `status`, `create_time`, `update_time`, `deleted`, `department`, `position`, `level`, `manager_id`, `dept_id`, `is_manager`) VALUES (1,'admin','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','系统管理员','ADMIN',1,'2026-03-12 14:03:16','2026-03-12 14:03:16',0,NULL,'系统管理员',0,NULL,NULL,0),(2,'ceo','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','张伟华','ADMIN',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'公司','总经理',1,NULL,NULL,0),(3,'vp_tech','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','李明强','ADMIN',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'公司','技术副总',2,2,NULL,0),(4,'vp_hr','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','王晓红','ADMIN',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'公司','人力副总',2,2,NULL,0),(5,'vp_finance','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','赵建国','ADMIN',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'公司','财务副总',2,2,NULL,0),(6,'dept_dev','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','陈志远','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'研发部','研发总监',3,3,NULL,0),(7,'dept_test','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','刘芳','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'测试部','测试总监',3,3,NULL,0),(8,'hr001','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','孙丽娜','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'人力资源部','HR总监',3,4,NULL,0),(9,'dept_finance','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','周敏','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'财务部','财务总监',3,5,NULL,0),(10,'pm_erp','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','吴鹏','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'研发部','ERP项目经理',4,6,NULL,0),(11,'pm_app','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','郑涛','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'研发部','APP项目经理',4,6,NULL,0),(12,'pm_data','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','黄磊','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'研发部','数据平台经理',4,6,NULL,0),(13,'pm_test','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','林静','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'测试部','测试经理',4,7,NULL,0),(14,'dev_zhang','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','张明','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'研发部','高级开发工程师',4,10,NULL,0),(15,'dev_li','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','李华','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'研发部','开发工程师',4,10,NULL,0),(16,'dev_wang','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','王强','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'研发部','前端工程师',4,11,NULL,0),(17,'dev_zhao','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','赵敏','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'研发部','后端工程师',4,11,NULL,0),(18,'tester_chen','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','陈晓','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'测试部','测试工程师',4,13,NULL,0),(19,'hr_zhou','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','周婷','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'人力资源部','HR专员',4,8,NULL,0),(20,'hr_wu','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','吴佳','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'人力资源部','招聘专员',4,8,NULL,0),(21,'finance_liu','$2a$10$6zzoFCrnN2pcssmjXufDku0/StOiujWnQfElgtQvISEQxspYJrPTa','刘芳','HR',1,'2026-03-13 13:01:02','2026-03-13 13:01:02',0,'财务部','会计',4,9,NULL,0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;

--
-- Dumping data for table `sys_user_view_config`
--

/*!40000 ALTER TABLE `sys_user_view_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_user_view_config` ENABLE KEYS */;

--
-- Dumping data for table `wf_instance`
--

/*!40000 ALTER TABLE `wf_instance` DISABLE KEYS */;
INSERT INTO `wf_instance` (`id`, `workflow_id`, `business_type`, `business_id`, `title`, `initiator_id`, `current_node_id`, `status`, `create_time`, `update_time`) VALUES (19,1,'REQUIREMENT',1,'ERP系统开发人员需求',10,2,'PENDING','2026-03-13 17:38:06','2026-03-13 17:38:06'),(20,1,'REQUIREMENT',2,'APP前端开发需求',10,2,'PENDING','2026-03-13 17:38:06','2026-03-13 17:38:06'),(21,1,'REQUIREMENT',3,'数据分析平台需求',10,2,'PENDING','2026-03-13 17:38:06','2026-03-13 17:38:06'),(22,2,'ENTRY',1,'张明入场审批',6,2,'PENDING','2026-03-13 17:38:06','2026-03-13 17:38:06'),(23,2,'ENTRY',2,'李华入场审批',6,2,'PENDING','2026-03-13 17:38:06','2026-03-13 17:38:06'),(24,2,'ENTRY',3,'王强入场审批',6,2,'PENDING','2026-03-13 17:38:06','2026-03-13 17:38:06'),(25,3,'SETTLEMENT',1,'2026年1月结算审批',8,2,'PENDING','2026-03-13 17:38:06','2026-03-13 17:38:06'),(26,3,'SETTLEMENT',2,'2026年2月结算审批',8,2,'PENDING','2026-03-13 17:38:06','2026-03-13 17:38:06'),(27,4,'EXIT',14,'测试人员离场审批',6,2,'PENDING','2026-03-13 17:38:06','2026-03-13 17:38:06'),(28,1,'REQUIREMENT',4,'CRM系统开发需求',10,4,'APPROVED','2026-02-11 17:38:06','2026-02-16 17:38:06'),(29,1,'REQUIREMENT',5,'数据迁移需求',10,4,'APPROVED','2026-02-21 17:38:06','2026-02-26 17:38:06'),(30,1,'REQUIREMENT',6,'测试自动化需求',10,4,'APPROVED','2026-03-03 17:38:06','2026-03-08 17:38:06'),(31,1,'REQUIREMENT',7,'运维监控需求',10,3,'REJECTED','2026-02-26 17:38:06','2026-03-01 17:38:06'),(32,2,'ENTRY',4,'陈晓入场审批',6,4,'APPROVED','2026-01-27 17:38:06','2026-02-01 17:38:06'),(33,2,'ENTRY',5,'周婷入场审批',6,4,'APPROVED','2026-02-06 17:38:06','2026-02-11 17:38:06'),(34,2,'ENTRY',6,'吴佳入场审批',6,4,'APPROVED','2026-02-16 17:38:06','2026-02-21 17:38:06'),(35,3,'SETTEMENT',3,'2025年11月结算审批',8,4,'APPROVED','2026-01-12 17:38:06','2026-01-17 17:38:06'),(36,3,'SETTLEMENT',4,'2025年12月结算审批',8,4,'APPROVED','2026-02-11 17:38:06','2026-02-16 17:38:06');
/*!40000 ALTER TABLE `wf_instance` ENABLE KEYS */;

--
-- Dumping data for table `wf_node`
--

/*!40000 ALTER TABLE `wf_node` DISABLE KEYS */;
INSERT INTO `wf_node` (`id`, `workflow_id`, `node_code`, `node_name`, `node_type`, `approver_role`, `next_node_id`, `sort_order`, `create_time`) VALUES (1,1,'发','发起','APPROVAL','HR',NULL,0,'2026-03-11 23:43:15'),(2,1,'','33','CC','',NULL,1,'2026-03-11 23:43:42'),(3,1,'','334','APPROVAL','ADMIN',NULL,2,'2026-03-11 23:43:42'),(4,2,'START','开始','START',NULL,NULL,0,'2026-03-11 23:49:34'),(5,2,'DEPT_APPROVE','部门长审核','APPROVAL','DEPT_DIRECTOR',NULL,1,'2026-03-11 23:49:34'),(6,2,'HR_APPROVE','HR审核','APPROVAL','HR',NULL,2,'2026-03-11 23:49:34'),(7,2,'VP_APPROVE','副总审批','APPROVAL','VP',NULL,3,'2026-03-11 23:49:34'),(8,2,'END','结束','END',NULL,NULL,4,'2026-03-11 23:49:34'),(9,3,'START','开始','START',NULL,NULL,0,'2026-03-11 23:49:34'),(10,3,'FINANCE_APPROVE','财务审核','APPROVAL','FINANCE',NULL,1,'2026-03-11 23:49:34'),(11,3,'VP_APPROVE','副总审批','APPROVAL','VP',NULL,2,'2026-03-11 23:49:34'),(12,3,'CEO_APPROVE','总经理审批','APPROVAL','CEO',NULL,3,'2026-03-11 23:49:34'),(13,3,'END','结束','END',NULL,NULL,4,'2026-03-11 23:49:34'),(14,4,'START','开始','START',NULL,NULL,0,'2026-03-11 23:49:34'),(15,4,'DEPT_CONFIRM','部门确认','APPROVAL','DEPT_DIRECTOR',NULL,1,'2026-03-11 23:49:34'),(16,4,'FINANCE_APPROVE','财务审核','APPROVAL','FINANCE',NULL,2,'2026-03-11 23:49:34'),(17,4,'END','结束','END',NULL,NULL,3,'2026-03-11 23:49:34'),(18,5,'START','开始','START',NULL,NULL,0,'2026-03-11 23:49:34'),(19,5,'DEPT_APPROVE','部门长审核','APPROVAL','DEPT_DIRECTOR',NULL,1,'2026-03-11 23:49:34'),(20,5,'VP_APPROVE','副总审批','APPROVAL','VP',NULL,2,'2026-03-11 23:49:34'),(21,5,'END','结束','END',NULL,NULL,3,'2026-03-11 23:49:34');
/*!40000 ALTER TABLE `wf_node` ENABLE KEYS */;

--
-- Dumping data for table `wf_record`
--

/*!40000 ALTER TABLE `wf_record` DISABLE KEYS */;
INSERT INTO `wf_record` (`id`, `instance_id`, `node_id`, `operator_id`, `operator_name`, `action`, `comment`, `create_time`) VALUES (20,28,2,6,'陈志远','APPROVE','同意，项目需要此岗位','2026-02-13 17:38:06'),(21,28,3,3,'李明强','APPROVE','同意申请','2026-02-15 17:38:06'),(22,28,4,2,'张伟华','APPROVE','批准','2026-02-16 17:38:06'),(23,29,2,6,'陈志远','APPROVE','需求合理','2026-02-23 17:38:06'),(24,29,3,3,'李明强','APPROVE','同意','2026-02-25 17:38:06'),(25,29,4,2,'张伟华','APPROVE','批准执行','2026-02-26 17:38:06'),(26,31,2,6,'陈志远','REJECT','当前预算不足，暂缓','2026-03-01 17:38:06'),(27,32,2,6,'陈志远','APPROVE','面试通过','2026-01-29 17:38:06'),(28,32,3,3,'李明强','APPROVE','同意入场','2026-01-31 17:38:06'),(29,32,4,8,'孙丽娜','APPROVE','已完成入职手续','2026-02-01 17:38:06'),(30,33,2,6,'陈志远','APPROVE','符合岗位要求','2026-02-08 17:38:06'),(31,33,3,4,'王晓红','APPROVE','同意','2026-02-10 17:38:06'),(32,33,4,8,'孙丽娜','APPROVE','入职完成','2026-02-11 17:38:06'),(33,35,2,8,'孙丽娜','APPROVE','数据核对无误','2026-01-14 17:38:06'),(34,35,3,4,'王晓红','APPROVE','金额正确，同意','2026-01-16 17:38:06'),(35,35,4,2,'张伟华','APPROVE','批准付款','2026-01-17 17:38:06'),(36,36,2,8,'孙丽娜','APPROVE','已核对','2026-02-13 17:38:06'),(37,36,3,4,'王晓红','APPROVE','同意结算','2026-02-15 17:38:06'),(38,36,4,2,'张伟华','APPROVE','批准','2026-02-16 17:38:06');
/*!40000 ALTER TABLE `wf_record` ENABLE KEYS */;

--
-- Dumping data for table `wf_workflow`
--

/*!40000 ALTER TABLE `wf_workflow` DISABLE KEYS */;
INSERT INTO `wf_workflow` (`id`, `workflow_code`, `workflow_name`, `description`, `status`, `create_time`, `update_time`) VALUES (1,'aaa','asdfasdf流程','',1,'2026-03-11 23:42:50','2026-03-11 23:42:50'),(2,'REQUIREMENT_APPROVAL','需求审批流程','外包人力需求审批流程',1,'2026-03-11 23:49:02','2026-03-11 23:49:02'),(3,'PAYMENT_APPROVAL','付款审批流程','付款申请审批流程',1,'2026-03-11 23:49:02','2026-03-11 23:49:02'),(4,'SETTLEMENT_APPROVAL','结算审批流程','月度结算审批流程',1,'2026-03-11 23:49:02','2026-03-11 23:49:02'),(5,'CONTRACT_APPROVAL','合同审批流程','合同签订审批流程',1,'2026-03-11 23:49:02','2026-03-11 23:49:02');
/*!40000 ALTER TABLE `wf_workflow` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-13 17:50:18
