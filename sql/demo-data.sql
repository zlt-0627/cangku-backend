-- ============================================================
-- 仓储管理系统 · 演示数据
--
-- 用途：截图前先把库填上像样的数据
-- 用法：连上 tlias 库，整体执行一次（IDEA 的 Database 面板、
--       Navicat、MySQL Workbench 都行）
-- 注意：会先清空 4 张业务表，如果里面有你自己要留的数据，先备份
-- ============================================================

SET NAMES utf8mb4;

DELETE FROM stock_record;
DELETE FROM stock;
DELETE FROM goods;
DELETE FROM warehouse;

-- ---------- 仓库 ----------
INSERT INTO warehouse (warehouse_name, address, status, create_time, update_time, version) VALUES
('广州中心仓', '广州市白云区太和镇兴太三路', 1, NOW(), NOW(), 0),
('深圳分仓',   '深圳市宝安区福永街道桥头村', 1, NOW(), NOW(), 0),
('佛山周转仓', '佛山市南海区狮山镇科技路',   1, NOW(), NOW(), 0);

-- ---------- 商品 ----------
INSERT INTO goods (goods_name, goods_code, unit, remark, create_time, update_time, is_delete, version) VALUES
('农夫山泉 550ml', 'SKU-1001', '箱', '常备饮用水',       NOW(), NOW(), 0, 0),
('怡宝 555ml',     'SKU-1002', '箱', '常备饮用水',       NOW(), NOW(), 0, 0),
('打印纸 A4',      'SKU-1003', '包', '办公耗材',         NOW(), NOW(), 0, 0),
('签字笔 0.5mm',   'SKU-1004', '盒', '办公耗材',         NOW(), NOW(), 0, 0),
('螺丝钉 M6',      'SKU-1005', '盒', '五金件',           NOW(), NOW(), 0, 0),
('一次性手套',     'SKU-1006', '箱', '劳保用品',         NOW(), NOW(), 0, 0),
('纸箱 40x30x30',  'SKU-1007', '个', '包装耗材',         NOW(), NOW(), 0, 0),
('封箱胶带',       'SKU-1008', '卷', '包装耗材',         NOW(), NOW(), 0, 0);

-- ---------- 库存（仓库 × 商品）----------
-- safe_num 故意给得比 stock_num 大，因为代码里的校验是「入库后不能超过安全值」
INSERT INTO stock (warehouse_id, goods_id, stock_num, safe_num, version, update_time) VALUES
((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '广州中心仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1001'), 320, 3000, 0, NOW()),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '广州中心仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1003'), 860, 5000, 0, NOW()),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '广州中心仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1007'), 1200, 8000, 0, NOW()),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '深圳分仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1001'), 150, 2000, 0, NOW()),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '深圳分仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1005'), 48, 1000, 0, NOW()),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '深圳分仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1008'), 640, 4000, 0, NOW()),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '佛山周转仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1002'), 275, 2500, 0, NOW()),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '佛山周转仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1006'), 96, 800, 0, NOW());

-- ---------- 出入库流水（可选：也可以在前端点「入库」自动生成）----------
INSERT INTO stock_record (warehouse_id, goods_id, operate_type, num, operator_id, create_time) VALUES
((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '广州中心仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1001'),
 1, 200, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '广州中心仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1003'),
 1, 500, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '深圳分仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1001'),
 2, 50,  1, DATE_SUB(NOW(), INTERVAL 1 DAY)),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '佛山周转仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1002'),
 1, 300, 1, DATE_SUB(NOW(), INTERVAL 6 HOUR)),

((SELECT warehouse_id FROM warehouse WHERE warehouse_name = '广州中心仓'),
 (SELECT goods_id    FROM goods     WHERE goods_code    = 'SKU-1007'),
 2, 120, 1, DATE_SUB(NOW(), INTERVAL 2 HOUR));

-- 执行完可以检查一下
SELECT '仓库' AS 表, COUNT(*) AS 条数 FROM warehouse
UNION ALL SELECT '商品', COUNT(*) FROM goods
UNION ALL SELECT '库存', COUNT(*) FROM stock
UNION ALL SELECT '流水', COUNT(*) FROM stock_record;

-- ---------- 登录账号 ----------
-- 密码明文分别是 admin / 123456（BCrypt 加密后入库）
-- 已有同名账号时会跳过，不会覆盖你自己建的账号
INSERT INTO login (username, password, role, is_delete, version)
SELECT 'admin', '$2a$10$Vk1Ra6osYumypVKw7/AYte5LlEE78XNIMVS9rWQg2/FcFuzDQXWgi', 3, 1, 0 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM login WHERE username = 'admin');

INSERT INTO login (username, password, role, is_delete, version)
SELECT 'operator', '$2a$10$Vk1Ra6osYumypVKw7/AYte5LlEE78XNIMVS9rWQg2/FcFuzDQXWgi', 2, 1, 0 FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM login WHERE username = 'operator');

-- 说明：本项目的 is_delete 约定与常规相反 —— 1 = 有效，0 = 已删除
