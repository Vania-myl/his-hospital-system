-- ============================================================
-- 东软云HIS —— 数据脚本（基础数据 + 演示业务数据）
-- 前提：已执行 init.sql（仅建表）
-- 说明：
--   1) 追加方式插入，重复执行会因唯一键冲突报错
--   2) 如需重来：先执行 init.sql 重建库表，再执行本脚本
--   3) 覆盖 4 个临床科室，业务链：挂号→病历→检查/检验/处置→处方→收费→发药
-- ============================================================
USE his;
SET NAMES utf8;

-- ============================================================
-- 一、基础数据
-- ============================================================

-- 科室
INSERT INTO department (dept_code, dept_name, dept_type) VALUES
('SJNK', '神经内科', '临床'),
('HXNK', '呼吸内科', '临床'),
('EK',   '儿科',     '临床'),
('GK',   '骨科',     '临床'),
('JYK',  '检验科',   '医技'),
('FSK',  '放射科',   '医技'),
('YF',   '药房',     '医技');

-- 挂号级别
INSERT INTO regist_level (regist_code, regist_name, regist_fee, regist_quota, sequence_no) VALUES
('PT', '普通号', 10.00, 50,  1),
('ZJ', '专家号', 20.00, 30,  2),
('JZ', '急诊号', 30.00, 100, 3);

-- 结算类别
INSERT INTO settle_category (settle_code, settle_name, sequence_no) VALUES
('ZF',  '自费',   1),
('YB',  '医保',   2),
('XNH', '新农合', 3);

-- 排班规则
INSERT INTO scheduling (rule_name, week_rule) VALUES
('正常白班', '1,2,3,4,5'),
('轮班制',   '1,2,3,4,5,6,7');

-- 员工（密码示例明文 123456，实际开发用 BCrypt 哈希）
INSERT INTO employee (username, password, realname, role, department_id, regist_level_id, scheduling_id) VALUES
('admin',     '123456', '管理员',   'admin',    NULL, NULL, NULL),
('window01',  '123456', '收费员小王', 'window',   NULL, NULL, NULL),
('doctor01',  '123456', '张医生',   'doctor',   1,    1,    1),
('doctor02',  '123456', '李医生',   'doctor',   3,    1,    1),
('check01',   '123456', '检查医生', 'check',    6,    NULL, NULL),
('lab01',     '123456', '检验医生', 'lab',      5,    NULL, NULL),
('treat01',   '123456', '处置医生', 'treat',    4,    NULL, NULL),
('pharmacy01','123456', '药房人员', 'pharmacy', 7,    NULL, NULL);

-- 医技项目
INSERT INTO medical_technology (tech_code, tech_name, tech_format, tech_price, tech_type, price_type, department_id) VALUES
('XCG',  '血常规',   '项', 20.00, '检验', '检验费', 5),
('NCG',  '尿常规',   '项', 15.00, '检验', '检验费', 5),
('XGX',  '胸部X光',  '次', 80.00, '检查', '检查费', 6),
('XDT',  '心电图',   '次', 60.00, '检查', '检查费', 6),
('JMSY', '静脉输液', '次', 25.00, '处置', '处置费', 4),
('SKHY', '伤口换药', '次', 30.00, '处置', '处置费', 4);

-- 疾病
INSERT INTO disease (disease_code, disease_name, disease_icd, disease_category) VALUES
('GXY', '高血压',       'I10',  '心血管'),
('TNB', '糖尿病',       'E11.9','内分泌'),
('SHDG', '上呼吸道感染', 'J06.9','呼吸');

-- 药品
INSERT INTO drug_info (drug_code, drug_name, drug_format, drug_unit, manufacturer, drug_dosage, drug_type, drug_price, mnemonic_code, creation_date) VALUES
('YP001', '阿莫西林胶囊',     '0.5g',   '盒', '华北制药', '胶囊剂', '西药', 12.50, 'AMXLJN', '2026-10-01'),
('YP002', '布洛芬缓释胶囊',   '0.3g',   '盒', '中美史克', '胶囊剂', '西药', 18.00, 'BLFHSJN', '2026-10-01'),
('YP003', '连花清瘟胶囊',     '0.35g',  '盒', '以岭药业', '胶囊剂', '中成药', 15.80, 'LHQWJN', '2026-10-01');

-- ============================================================
-- 二、演示业务数据
-- ============================================================

-- 补充两名医生（呼吸内科 / 骨科），使 4 个临床科室都有接诊医生
INSERT INTO employee (username, password, realname, role, department_id, regist_level_id, scheduling_id) VALUES
('doctor03', '123456', '王医生', 'doctor', 2, 1, 1);
SET @doc3 = LAST_INSERT_ID();
INSERT INTO employee (username, password, realname, role, department_id, regist_level_id, scheduling_id) VALUES
('doctor04', '123456', '赵医生', 'doctor', 4, 1, 1);
SET @doc4 = LAST_INSERT_ID();

-- 基础数据 id 约定（本脚本内稳定）：
--   科室：1 神经内科 2 呼吸内科 3 儿科 4 骨科
--   号别：1 普通号(10) 2 专家号(20) 3 急诊号(30)
--   结算：1 自费 2 医保
--   医生：3 张医生(神经内科) 4 李医生(儿科)；@doc3 王医生(呼吸内科) @doc4 赵医生(骨科)
--   收费员：2 window01
--   医技：1 血常规(20) 2 尿常规(15) 3 胸部X光(80) 4 心电图(60) 5 静脉输液(25) 6 伤口换药(30)
--   疾病：1 高血压 3 上呼吸道感染
--   药品：1 阿莫西林(12.5) 2 布洛芬(18) 3 连花清瘟(15.8)

-- ============================================================
-- P1 张伟 男55 神经内科·专家号·医保 —— 看诊结束，完整流程（心电图 + 血常规 + 尿常规）
-- ============================================================
INSERT INTO register (case_number, real_name, gender, card_number, birthdate, age, age_type, home_address, visit_date, noon, department_id, employee_id, regist_level_id, settle_category_id, is_book, regist_method, regist_money, visit_state)
VALUES ('BN202611001', '张伟', '男', '110101197103154012', '1971-03-15', 55, '年', '北京市朝阳区', '2026-10-22 09:15:00', '上午', 1, 3, 2, 2, '否', '医保卡', 20.00, 3);
SET @rid = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '挂号费', NULL, '收费', 20.00, '医保', 2, '2026-10-22 09:15:00');
INSERT INTO medical_record (register_id, readme, present, present_treat, history, allergy, physique, proposal, careful, diagnosis, cure)
VALUES (@rid, '反复头晕头痛1月余', '近1月反复头晕，晨起明显，伴颈部僵硬', '未系统治疗', '高血压病史3年', '无', '血压160/95mmHg，心律齐', '完善心电图、血尿常规', '低盐低脂饮食', '高血压', '口服降压治疗，定期复查血压');
SET @mr = LAST_INSERT_ID();
INSERT INTO medical_record_disease (medical_record_id, disease_id) VALUES (@mr, 1);
INSERT INTO check_request (register_id, medical_technology_id, check_info, check_position, creation_time, check_employee_id, check_time, check_result, check_state)
VALUES (@rid, 4, '评估心脏结构与心律', '心脏', '2026-10-22 09:30:00', 5, '2026-10-22 10:05:00', '窦性心律，ST段无异常', '3');
SET @cr = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '检查', @cr, '收费', 60.00, '医保', 2, '2026-10-22 09:30:00');
INSERT INTO inspection_request (register_id, medical_technology_id, inspection_info, inspection_position, creation_time, inspection_employee_id, inspection_time, inspection_result, inspection_state)
VALUES (@rid, 1, '血细胞分析', '静脉血', '2026-10-22 09:32:00', 6, '2026-10-22 10:10:00', '各项指标基本正常', '3');
SET @ir = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '检验', @ir, '收费', 20.00, '医保', 2, '2026-10-22 09:32:00');
INSERT INTO inspection_request (register_id, medical_technology_id, inspection_info, inspection_position, creation_time, inspection_employee_id, inspection_time, inspection_result, inspection_state)
VALUES (@rid, 2, '尿常规分析', '晨尿', '2026-10-22 09:32:00', 6, '2026-10-22 10:10:00', '尿蛋白（-）', '3');
SET @ir = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '检验', @ir, '收费', 15.00, '医保', 2, '2026-10-22 09:32:00');

-- ============================================================
-- P2 李娜 女32 呼吸内科·普通号·医保 —— 看诊结束（胸部X光 + 处方2种，已发药）
-- ============================================================
INSERT INTO register (case_number, real_name, gender, card_number, birthdate, age, age_type, home_address, visit_date, noon, department_id, employee_id, regist_level_id, settle_category_id, is_book, regist_method, regist_money, visit_state)
VALUES ('BN202611002', '李娜', '女', '120101199403214566', '1994-03-21', 32, '年', '上海市浦东新区', '2026-10-25 14:30:00', '下午', 2, @doc3, 1, 2, '否', '医保卡', 10.00, 3);
SET @rid = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '挂号费', NULL, '收费', 10.00, '医保', 2, '2026-10-25 14:30:00');
INSERT INTO medical_record (register_id, readme, present, history, allergy, physique, diagnosis, cure)
VALUES (@rid, '咳嗽咳痰3天，伴发热1天', '3天前受凉后咳嗽，黄痰，体温最高38.5℃', '体健', '无', '双肺呼吸音粗，可闻及湿啰音', '上呼吸道感染', '抗感染、对症退热');
SET @mr = LAST_INSERT_ID();
INSERT INTO medical_record_disease (medical_record_id, disease_id) VALUES (@mr, 3);
INSERT INTO check_request (register_id, medical_technology_id, check_info, check_position, creation_time, check_employee_id, check_time, check_result, check_state)
VALUES (@rid, 3, '评估肺部感染情况', '胸部', '2026-10-25 14:50:00', 5, '2026-10-25 15:20:00', '双肺纹理增粗，未见实变', '3');
SET @cr = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '检查', @cr, '收费', 80.00, '医保', 2, '2026-10-25 14:50:00');
INSERT INTO prescription (register_id, drug_id, drug_usage, drug_number, creation_time, drug_state)
VALUES (@rid, 3, '口服，每次4粒，每日3次', '2盒', '2026-10-25 15:30:00', '2');
SET @pr = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '处方', @pr, '收费', 31.60, '医保', 2, '2026-10-25 15:30:00');
INSERT INTO prescription (register_id, drug_id, drug_usage, drug_number, creation_time, drug_state)
VALUES (@rid, 1, '口服，每次2粒，每日3次', '2盒', '2026-10-25 15:31:00', '2');
SET @pr = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '处方', @pr, '收费', 25.00, '医保', 2, '2026-10-25 15:31:00');

-- ============================================================
-- P3 王小明 男6 儿科·普通号·自费 —— 看诊结束（血常规 + 静脉输液 + 处方，已发药）
-- ============================================================
INSERT INTO register (case_number, real_name, gender, card_number, birthdate, age, age_type, home_address, visit_date, noon, department_id, employee_id, regist_level_id, settle_category_id, is_book, regist_method, regist_money, visit_state)
VALUES ('BN202611003', '王小明', '男', '310101202008071234', '2020-08-07', 6, '年', '上海市静安区', '2026-10-28 10:05:00', '上午', 3, 4, 1, 1, '否', '现金', 10.00, 3);
SET @rid = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '挂号费', NULL, '收费', 10.00, '现金', 2, '2026-10-28 10:05:00');
INSERT INTO medical_record (register_id, readme, present, allergy, diagnosis, cure)
VALUES (@rid, '发热2天，最高39℃', '2天前无明显诱因发热，伴咽痛，无咳嗽', '青霉素过敏', '上呼吸道感染', '退热、补液');
SET @mr = LAST_INSERT_ID();
INSERT INTO medical_record_disease (medical_record_id, disease_id) VALUES (@mr, 3);
INSERT INTO inspection_request (register_id, medical_technology_id, inspection_info, inspection_position, creation_time, inspection_employee_id, inspection_time, inspection_result, inspection_state)
VALUES (@rid, 1, '血细胞分析', '末梢血', '2026-10-28 10:20:00', 6, '2026-10-28 11:00:00', '白细胞升高', '3');
SET @ir = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '检验', @ir, '收费', 20.00, '现金', 2, '2026-10-28 10:20:00');
INSERT INTO disposal_request (register_id, medical_technology_id, disposal_info, disposal_position, creation_time, disposal_employee_id, disposal_time, disposal_result, disposal_state)
VALUES (@rid, 5, '补液退热', '手背静脉', '2026-10-28 10:30:00', 7, '2026-10-28 12:00:00', '输液完毕，无不良反应', '3');
SET @dr = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '处置', @dr, '收费', 25.00, '现金', 2, '2026-10-28 10:30:00');
INSERT INTO prescription (register_id, drug_id, drug_usage, drug_number, creation_time, drug_state)
VALUES (@rid, 2, '口服，发热时服用，每次1粒', '1盒', '2026-10-28 10:40:00', '2');
SET @pr = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '处方', @pr, '收费', 18.00, '现金', 2, '2026-10-28 10:40:00');

-- ============================================================
-- P4 陈建国 男45 骨科·普通号·医保 —— 看诊结束（X光 + 伤口换药）
-- ============================================================
INSERT INTO register (case_number, real_name, gender, card_number, birthdate, age, age_type, home_address, visit_date, noon, department_id, employee_id, regist_level_id, settle_category_id, is_book, regist_method, regist_money, visit_state)
VALUES ('BN202611004', '陈建国', '男', '320101198102123311', '1981-02-12', 45, '年', '江苏省南京市', '2026-10-29 16:20:00', '下午', 4, @doc4, 1, 2, '否', '医保卡', 10.00, 3);
SET @rid = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '挂号费', NULL, '收费', 10.00, '医保', 2, '2026-10-29 16:20:00');
INSERT INTO medical_record (register_id, readme, present, diagnosis, cure)
VALUES (@rid, '右踝扭伤肿痛半天', '运动时右踝扭伤，局部肿胀压痛', '右踝关节扭伤', '制动、换药、对症处理');
SET @mr = LAST_INSERT_ID();
INSERT INTO check_request (register_id, medical_technology_id, check_info, check_position, creation_time, check_employee_id, check_time, check_result, check_state)
VALUES (@rid, 3, '排除骨折', '右踝', '2026-10-29 16:40:00', 5, '2026-10-29 17:00:00', '未见明显骨折征象', '3');
SET @cr = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '检查', @cr, '收费', 80.00, '医保', 2, '2026-10-29 16:40:00');
INSERT INTO disposal_request (register_id, medical_technology_id, disposal_info, disposal_position, creation_time, disposal_employee_id, disposal_time, disposal_result, disposal_state)
VALUES (@rid, 6, '清创换药', '右踝', '2026-10-29 17:10:00', 7, '2026-10-29 17:30:00', '换药完毕，创面清洁', '3');
SET @dr = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '处置', @dr, '收费', 30.00, '医保', 2, '2026-10-29 17:10:00');

-- ============================================================
-- P5 刘芳 女28 呼吸内科·普通号·自费 —— 医生接诊中（X光已执行未出结果，处方已开未发药）
-- ============================================================
INSERT INTO register (case_number, real_name, gender, card_number, birthdate, age, age_type, home_address, visit_date, noon, department_id, employee_id, regist_level_id, settle_category_id, is_book, regist_method, regist_money, visit_state)
VALUES ('BN202611005', '刘芳', '女', '440101199812056621', '1998-12-05', 28, '年', '广东省广州市', '2026-11-01 09:40:00', '上午', 2, @doc3, 1, 1, '否', '微信', 10.00, 2);
SET @rid = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '挂号费', NULL, '收费', 10.00, '微信', 2, '2026-11-01 09:40:00');
INSERT INTO medical_record (register_id, readme, present, diagnosis, cure)
VALUES (@rid, '咽痛咽干2天', '咽部疼痛，吞咽时加重，伴轻度乏力', '上呼吸道感染', '对症治疗，多饮水');
SET @mr = LAST_INSERT_ID();
INSERT INTO medical_record_disease (medical_record_id, disease_id) VALUES (@mr, 3);
INSERT INTO check_request (register_id, medical_technology_id, check_info, check_position, creation_time, check_employee_id, check_time, check_result, check_state)
VALUES (@rid, 3, '排除下呼吸道感染', '胸部', '2026-11-01 09:55:00', 5, '2026-11-01 10:30:00', NULL, '2');
SET @cr = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '检查', @cr, '收费', 80.00, '微信', 2, '2026-11-01 09:55:00');
INSERT INTO prescription (register_id, drug_id, drug_usage, drug_number, creation_time, drug_state)
VALUES (@rid, 3, '口服，每次4粒，每日3次', '1盒', '2026-11-01 10:10:00', '1');
SET @pr = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '处方', @pr, '收费', 15.80, '微信', 2, '2026-11-01 10:10:00');

-- ============================================================
-- P6 赵敏 女40 神经内科·专家号·医保 —— 刚挂号，尚未接诊
-- ============================================================
INSERT INTO register (case_number, real_name, gender, card_number, birthdate, age, age_type, home_address, visit_date, noon, department_id, employee_id, regist_level_id, settle_category_id, is_book, regist_method, regist_money, visit_state)
VALUES ('BN202611006', '赵敏', '女', '110101198609013325', '1986-09-01', 40, '年', '北京市海淀区', '2026-11-02 08:50:00', '上午', 1, 3, 2, 2, '否', '医保卡', 20.00, 1);
SET @rid = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '挂号费', NULL, '收费', 20.00, '医保', 2, '2026-11-02 08:50:00');

-- ============================================================
-- P7 孙强 男33 骨科·急诊号·医保 —— 已退号（挂号费已退）
-- ============================================================
INSERT INTO register (case_number, real_name, gender, card_number, birthdate, age, age_type, home_address, visit_date, noon, department_id, employee_id, regist_level_id, settle_category_id, is_book, regist_method, regist_money, visit_state)
VALUES ('BN202611007', '孙强', '男', '330101199304123345', '1993-04-12', 33, '年', '浙江省杭州市', '2026-10-30 11:10:00', '上午', 4, @doc4, 3, 2, '否', '支付宝', 30.00, 4);
SET @rid = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '挂号费', NULL, '收费', 30.00, '支付宝', 2, '2026-10-30 11:10:00');
SET @fee = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, refund_target_id, operator_id, create_time)
VALUES (@rid, '挂号费', NULL, '退费', 30.00, '支付宝', @fee, 2, '2026-10-30 11:35:00');

-- ============================================================
-- P8 周丽 女25 儿科·普通号·自费 —— 看诊结束（尿常规）
-- ============================================================
INSERT INTO register (case_number, real_name, gender, card_number, birthdate, age, age_type, home_address, visit_date, noon, department_id, employee_id, regist_level_id, settle_category_id, is_book, regist_method, regist_money, visit_state)
VALUES ('BN202611008', '周丽', '女', '510101200105087745', '2001-05-08', 25, '年', '四川省成都市', '2026-10-27 15:45:00', '下午', 3, 4, 1, 1, '否', '现金', 10.00, 3);
SET @rid = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '挂号费', NULL, '收费', 10.00, '现金', 2, '2026-10-27 15:45:00');
INSERT INTO medical_record (register_id, readme, present, diagnosis, cure)
VALUES (@rid, '腹痛腹泻1天', '1天前进食生冷后腹痛、腹泻，日3次', '急性胃肠炎', '补液、清淡饮食');
SET @mr = LAST_INSERT_ID();
INSERT INTO inspection_request (register_id, medical_technology_id, inspection_info, inspection_position, creation_time, inspection_employee_id, inspection_time, inspection_result, inspection_state)
VALUES (@rid, 2, '尿常规分析', '晨尿', '2026-10-27 16:00:00', 6, '2026-10-27 16:30:00', '镜检未见异常', '3');
SET @ir = LAST_INSERT_ID();
INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
VALUES (@rid, '检验', @ir, '收费', 15.00, '现金', 2, '2026-10-27 16:00:00');

-- ============================================================
-- 三、HYJ 医技验收数据（检查/检验/处置，幂等可重复执行）
-- 自建病历号 HYJTEST0001，每类项目各一条已收费 + 一条未收费的“已开立”申请
-- ============================================================

-- 单独创建一条测试挂号，不依赖组员本地的演示数据。
INSERT INTO register
    (case_number, real_name, gender, visit_date, noon, department_id, employee_id,
     regist_level_id, settle_category_id, is_book, regist_method, regist_money, visit_state)
SELECT 'HYJTEST0001', 'HYJ测试患者', '女', NOW(), '上午', doctor.department_id, doctor.id,
       level.id, category.id, '否', '现金', level.regist_fee, 2
FROM employee doctor
JOIN regist_level level ON level.regist_code = 'PT'
JOIN settle_category category ON category.settle_code = 'ZF'
WHERE doctor.username = 'doctor01'
  AND NOT EXISTS (SELECT 1 FROM register r WHERE r.case_number = 'HYJTEST0001');

INSERT INTO check_request (register_id, medical_technology_id, check_info, check_position, creation_time, check_state)
SELECT r.id, t.id, 'HYJ-CHECK-PAID', '心脏', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'XDT'
WHERE r.case_number = 'HYJTEST0001'
  AND NOT EXISTS (SELECT 1 FROM check_request x WHERE x.register_id = r.id AND x.check_info = 'HYJ-CHECK-PAID');

INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
SELECT x.register_id, '检查', x.id, '收费', t.tech_price, '现金', e.id, NOW()
FROM check_request x
JOIN medical_technology t ON t.id = x.medical_technology_id
JOIN register r ON r.id = x.register_id
JOIN employee e ON e.username = 'window01'
WHERE r.case_number = 'HYJTEST0001' AND x.check_info = 'HYJ-CHECK-PAID'
  AND NOT EXISTS (SELECT 1 FROM fee_record f WHERE f.item_type = '检查' AND f.item_id = x.id AND f.fee_type = '收费');

INSERT INTO check_request (register_id, medical_technology_id, check_info, check_position, creation_time, check_state)
SELECT r.id, t.id, 'HYJ-CHECK-UNPAID', '心脏', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'XDT'
WHERE r.case_number = 'HYJTEST0001'
  AND NOT EXISTS (SELECT 1 FROM check_request x WHERE x.register_id = r.id AND x.check_info = 'HYJ-CHECK-UNPAID');

INSERT INTO inspection_request (register_id, medical_technology_id, inspection_info, inspection_position, creation_time, inspection_state)
SELECT r.id, t.id, 'HYJ-LAB-PAID', '静脉血', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'XCG'
WHERE r.case_number = 'HYJTEST0001'
  AND NOT EXISTS (SELECT 1 FROM inspection_request x WHERE x.register_id = r.id AND x.inspection_info = 'HYJ-LAB-PAID');

INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
SELECT x.register_id, '检验', x.id, '收费', t.tech_price, '现金', e.id, NOW()
FROM inspection_request x
JOIN medical_technology t ON t.id = x.medical_technology_id
JOIN register r ON r.id = x.register_id
JOIN employee e ON e.username = 'window01'
WHERE r.case_number = 'HYJTEST0001' AND x.inspection_info = 'HYJ-LAB-PAID'
  AND NOT EXISTS (SELECT 1 FROM fee_record f WHERE f.item_type = '检验' AND f.item_id = x.id AND f.fee_type = '收费');

INSERT INTO inspection_request (register_id, medical_technology_id, inspection_info, inspection_position, creation_time, inspection_state)
SELECT r.id, t.id, 'HYJ-LAB-UNPAID', '静脉血', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'XCG'
WHERE r.case_number = 'HYJTEST0001'
  AND NOT EXISTS (SELECT 1 FROM inspection_request x WHERE x.register_id = r.id AND x.inspection_info = 'HYJ-LAB-UNPAID');

INSERT INTO disposal_request (register_id, medical_technology_id, disposal_info, disposal_position, creation_time, disposal_state)
SELECT r.id, t.id, 'HYJ-TREAT-PAID', '手背静脉', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'JMSY'
WHERE r.case_number = 'HYJTEST0001'
  AND NOT EXISTS (SELECT 1 FROM disposal_request x WHERE x.register_id = r.id AND x.disposal_info = 'HYJ-TREAT-PAID');

INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
SELECT x.register_id, '处置', x.id, '收费', t.tech_price, '现金', e.id, NOW()
FROM disposal_request x
JOIN medical_technology t ON t.id = x.medical_technology_id
JOIN register r ON r.id = x.register_id
JOIN employee e ON e.username = 'window01'
WHERE r.case_number = 'HYJTEST0001' AND x.disposal_info = 'HYJ-TREAT-PAID'
  AND NOT EXISTS (SELECT 1 FROM fee_record f WHERE f.item_type = '处置' AND f.item_id = x.id AND f.fee_type = '收费');

INSERT INTO disposal_request (register_id, medical_technology_id, disposal_info, disposal_position, creation_time, disposal_state)
SELECT r.id, t.id, 'HYJ-TREAT-UNPAID', '手背静脉', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'JMSY'
WHERE r.case_number = 'HYJTEST0001'
  AND NOT EXISTS (SELECT 1 FROM disposal_request x WHERE x.register_id = r.id AND x.disposal_info = 'HYJ-TREAT-UNPAID');
