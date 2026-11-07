-- ============================================================
-- 仓储管理系统 · 建表语句
-- 适用：MySQL 8.0+
--
-- 用法：
--   1. 命令行：mysql -u root -p < init.sql
--   2. 或在 Navicat / IDEA Database 里打开本文件整体执行
--   3. 需要演示数据，再执行同目录下的 demo-data.sql
--
-- 说明：项目只用下面 5 张表。如果你的 tlias 库里还有 dept、emp、
--       student 等表，那是别的练习留下的，与本项目无关。
-- ============================================================

CREATE DATABASE IF NOT EXISTS tlias DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE tlias;

-- ------------------------------------------------------------
-- 账号表
-- role: 1 只读 / 2 可写 / 3 管理员 / 4 超级管理员
-- is_delete: 1 有效 / 0 已删除（注意与常规相反，代码里按此约定）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `login` (
  `id`        int unsigned NOT NULL AUTO_INCREMENT,
  `username`  varchar(10)  DEFAULT NULL,
  `password`  varchar(100) DEFAULT NULL COMMENT 'BCrypt 加密后的密码',
  `role`      int          DEFAULT NULL,
  `is_delete` int          DEFAULT '1',
  `version`   int          DEFAULT '0',
  PRIMARY KEY (`id`),
  CONSTRAINT `login_chk_1` CHECK ((`role` in (1, 2, 3, 4)))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

-- ------------------------------------------------------------
-- 仓库表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `warehouse` (
  `warehouse_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT '仓库ID',
  `warehouse_name` char(10)     NOT NULL,
  `address`        varchar(200) DEFAULT NULL COMMENT '仓库地址',
  `status`         int          DEFAULT '1' COMMENT '1 正常 / 0 已删除',
  `create_time`    datetime     DEFAULT CURRENT_TIMESTAMP,
  `update_time`    datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version`        int          DEFAULT '0' COMMENT '乐观锁版本号',
  PRIMARY KEY (`warehouse_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '仓库表';

-- ------------------------------------------------------------
-- 商品表
-- goods_code 唯一索引：从数据库层面兜住重复编码
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `goods` (
  `goods_id`    bigint       NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `goods_name`  varchar(100) NOT NULL COMMENT '商品名称',
  `goods_code`  varchar(50)  DEFAULT NULL COMMENT '商品编码',
  `unit`        varchar(20)  DEFAULT NULL COMMENT '单位：个/箱/千克',
  `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime     DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_delete`   int          DEFAULT '0' COMMENT '0 正常 / 1 已删除',
  `version`     bigint       DEFAULT '0' COMMENT '乐观锁版本号',
  PRIMARY KEY (`goods_id`),
  UNIQUE KEY `goods_code` (`goods_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表';

-- ------------------------------------------------------------
-- 库存表（仓库 × 商品）
-- uk_ware_goods 唯一索引：同一仓库同一商品只能有一条库存，
-- 是并发下防止重复建库存的最后一道防线
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `stock` (
  `stock_id`     bigint   NOT NULL AUTO_INCREMENT,
  `warehouse_id` bigint   NOT NULL COMMENT '仓库id',
  `goods_id`     bigint   NOT NULL COMMENT '商品id',
  `stock_num`    int      NOT NULL DEFAULT '0' COMMENT '当前库存数量',
  `safe_num`     int      DEFAULT '0' COMMENT '安全库存阈值',
  `update_time`  datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `version`      int      DEFAULT '0' COMMENT '乐观锁版本号',
  PRIMARY KEY (`stock_id`),
  UNIQUE KEY `uk_ware_goods` (`warehouse_id`, `goods_id`) COMMENT '同一个商品在同一个仓库只能一条库存记录'
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '库存表';

-- ------------------------------------------------------------
-- 出入库流水表
-- operate_type: 1 入库 / 2 出库 / 3 调拨
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `stock_record` (
  `record_id`    bigint  NOT NULL AUTO_INCREMENT,
  `warehouse_id` bigint  NOT NULL,
  `goods_id`     bigint  NOT NULL,
  `operate_type` tinyint NOT NULL COMMENT '1入库 2出库 3调拨',
  `num`          int     NOT NULL COMMENT '变动数量，入库正数，出库负数',
  `operator_id`  bigint  NOT NULL COMMENT '操作人用户id',
  `create_time`  datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`record_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '出入库流水记录';
