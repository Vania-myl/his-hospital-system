-- HYJ 验收数据：非破坏性、可重复执行。前提：init.sql 已导入基础数据。
-- 自建病历号 HYJTEST0001，使用 window01 和医技项目编码。
-- 每类项目各生成一条已收费与一条未收费的“已开立”申请。
USE his;
SET NAMES utf8;

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
