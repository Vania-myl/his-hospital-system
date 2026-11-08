-- HYJ 验收数据：非破坏性、可重复执行。前提：demo-data.sql 已导入。
-- 使用已有病历号 BN202611006、窗口账号 window01 和医技项目编码。
-- 每类项目各生成一条已收费与一条未收费的“已开立”申请。
USE his;
SET NAMES utf8;

INSERT INTO check_request (register_id, medical_technology_id, check_info, check_position, creation_time, check_state)
SELECT r.id, t.id, 'HYJ-CHECK-PAID', '心脏', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'XDT'
WHERE r.case_number = 'BN202611006'
  AND NOT EXISTS (SELECT 1 FROM check_request x WHERE x.register_id = r.id AND x.check_info = 'HYJ-CHECK-PAID');


INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
SELECT x.register_id, '检查', x.id, '收费', t.tech_price, '现金', e.id, NOW()
FROM check_request x
JOIN medical_technology t ON t.id = x.medical_technology_id
JOIN register r ON r.id = x.register_id
JOIN employee e ON e.username = 'window01'
WHERE r.case_number = 'BN202611006' AND x.check_info = 'HYJ-CHECK-PAID'
  AND NOT EXISTS (SELECT 1 FROM fee_record f WHERE f.item_type = '检查' AND f.item_id = x.id AND f.fee_type = '收费');

INSERT INTO check_request (register_id, medical_technology_id, check_info, check_position, creation_time, check_state)
SELECT r.id, t.id, 'HYJ-CHECK-UNPAID', '心脏', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'XDT'
WHERE r.case_number = 'BN202611006'
  AND NOT EXISTS (SELECT 1 FROM check_request x WHERE x.register_id = r.id AND x.check_info = 'HYJ-CHECK-UNPAID');


INSERT INTO inspection_request (register_id, medical_technology_id, inspection_info, inspection_position, creation_time, inspection_state)
SELECT r.id, t.id, 'HYJ-LAB-PAID', '静脉血', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'XCG'
WHERE r.case_number = 'BN202611006'
  AND NOT EXISTS (SELECT 1 FROM inspection_request x WHERE x.register_id = r.id AND x.inspection_info = 'HYJ-LAB-PAID');


INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
SELECT x.register_id, '检验', x.id, '收费', t.tech_price, '现金', e.id, NOW()
FROM inspection_request x
JOIN medical_technology t ON t.id = x.medical_technology_id
JOIN register r ON r.id = x.register_id
JOIN employee e ON e.username = 'window01'
WHERE r.case_number = 'BN202611006' AND x.inspection_info = 'HYJ-LAB-PAID'
  AND NOT EXISTS (SELECT 1 FROM fee_record f WHERE f.item_type = '检验' AND f.item_id = x.id AND f.fee_type = '收费');

INSERT INTO inspection_request (register_id, medical_technology_id, inspection_info, inspection_position, creation_time, inspection_state)
SELECT r.id, t.id, 'HYJ-LAB-UNPAID', '静脉血', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'XCG'
WHERE r.case_number = 'BN202611006'
  AND NOT EXISTS (SELECT 1 FROM inspection_request x WHERE x.register_id = r.id AND x.inspection_info = 'HYJ-LAB-UNPAID');


INSERT INTO disposal_request (register_id, medical_technology_id, disposal_info, disposal_position, creation_time, disposal_state)
SELECT r.id, t.id, 'HYJ-TREAT-PAID', '手背静脉', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'JMSY'
WHERE r.case_number = 'BN202611006'
  AND NOT EXISTS (SELECT 1 FROM disposal_request x WHERE x.register_id = r.id AND x.disposal_info = 'HYJ-TREAT-PAID');


INSERT INTO fee_record (register_id, item_type, item_id, fee_type, fee_amount, fee_method, operator_id, create_time)
SELECT x.register_id, '处置', x.id, '收费', t.tech_price, '现金', e.id, NOW()
FROM disposal_request x
JOIN medical_technology t ON t.id = x.medical_technology_id
JOIN register r ON r.id = x.register_id
JOIN employee e ON e.username = 'window01'
WHERE r.case_number = 'BN202611006' AND x.disposal_info = 'HYJ-TREAT-PAID'
  AND NOT EXISTS (SELECT 1 FROM fee_record f WHERE f.item_type = '处置' AND f.item_id = x.id AND f.fee_type = '收费');

INSERT INTO disposal_request (register_id, medical_technology_id, disposal_info, disposal_position, creation_time, disposal_state)
SELECT r.id, t.id, 'HYJ-TREAT-UNPAID', '手背静脉', NOW(), '1'
FROM register r JOIN medical_technology t ON t.tech_code = 'JMSY'
WHERE r.case_number = 'BN202611006'
  AND NOT EXISTS (SELECT 1 FROM disposal_request x WHERE x.register_id = r.id AND x.disposal_info = 'HYJ-TREAT-UNPAID');
