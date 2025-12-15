-- 1. 礼品券表 (GiftCode)
CREATE TABLE `gift_code` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `code` VARCHAR(64) NOT NULL UNIQUE COMMENT '核销码/二维码内容',
    `status` TINYINT DEFAULT 0 COMMENT '状态: 0-未使用, 1-已核销, 2-已过期',
    `expire_time` DATETIME COMMENT '过期时间',
    `goods_name` VARCHAR(100) COMMENT '商品名称',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `redeem_time` DATETIME COMMENT '核销时间',
    `merchant_id` BIGINT COMMENT '核销商家ID'
);

-- 2. 核销记录表 (VerificationRecord) - 对应功能23、24
CREATE TABLE `verification_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `merchant_id` BIGINT NOT NULL COMMENT '操作核销的商家ID',
    `code` VARCHAR(64) NOT NULL COMMENT '核销的码',
    `goods_name` VARCHAR(100) COMMENT '商品名称',
    `visitor_id` VARCHAR(64) COMMENT '游客标识',
    `verify_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '核销时间',
    INDEX `idx_merchant_time` (`merchant_id`, `verify_time`) -- 方便统计查询
);

-- 3. 商家表 (Merchant)
CREATE TABLE `merchant` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '登录用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '登录密码',
    `merchant_name` VARCHAR(100) NOT NULL COMMENT '商家名称',
    `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 插入测试数据
INSERT INTO `gift_code` (`code`, `status`, `expire_time`, `goods_name`) VALUES
('REDEEM123456', 0, DATE_ADD(NOW(), INTERVAL 30 DAY), '景区门票优惠券'),
('REDEEM234567', 0, DATE_ADD(NOW(), INTERVAL 30 DAY), '纪念品折扣券'),
('REDEEM345678', 0, DATE_ADD(NOW(), INTERVAL 30 DAY), '餐饮代金券'),
('REDEEM456789', 1, DATE_ADD(NOW(), INTERVAL 30 DAY), '景区门票优惠券'),
('REDEEM567890', 2, DATE_SUB(NOW(), INTERVAL 1 DAY), '纪念品折扣券'),
('REDEEM678901', 0, DATE_ADD(NOW(), INTERVAL 30 DAY), '特色小吃券');

-- 插入商家测试数据 (密码: 123456, 已加密)
INSERT INTO `merchant` (`username`, `password`, `merchant_name`) VALUES
('merchant1', '$2a$10$w7K2Yj2d2X2Z2B2A2S2D2F2G2H2J2K2L2M2N2O2P2Q2R2T2U2V2W2X2Y2Z2A2B2C2D2E2F', '测试商家1'),
('merchant2', '$2a$10$w7K2Yj2d2X2Z2B2A2S2D2F2G2H2J2K2L2M2N2O2P2Q2R2T2U2V2W2X2Y2Z2A2B2C2D2E2F', '测试商家2');

-- 模拟已核销的记录
INSERT INTO `verification_record` (`merchant_id`, `code`, `goods_name`, `visitor_id`) VALUES
(1, 'REDEEM456789', '景区门票优惠券', 'VISITOR_REDEEM45'),
(2, 'REDEEM999999', '餐饮代金券', 'VISITOR_REDEEM99');