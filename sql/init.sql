-- ============================================================
-- 东软云HIS医院管理系统 —— 数据库初始化脚本
-- 数据库：his
-- 目标：MySQL 5.5.x（InnoDB / utf8）
-- 说明：
--   1) 表间仅逻辑外键（xxx_id），不建物理 FOREIGN KEY（见契约 2.6）
--   2) 字符集用 utf8（3 字节）：MySQL 5.5 索引上限 767 字节，
--      utf8mb4 会令 VARCHAR(255) 索引超长，故统一 utf8
--   3) 主键 id 自增；基础数据表带 delmark 软删除标记
-- ============================================================

DROP DATABASE IF EXISTS his;
CREATE DATABASE his DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE his;

-- ------------------------------------------------------------
-- 1. 科室表 department
-- ------------------------------------------------------------
CREATE TABLE department (
  id          INT(9)       NOT NULL AUTO_INCREMENT COMMENT '主键',
  dept_code   VARCHAR(64)  NOT NULL COMMENT '科室编码，如 SJNK',
  dept_name   VARCHAR(64)  NOT NULL COMMENT '科室名称，如 神经内科',
  dept_type   VARCHAR(64)  DEFAULT NULL COMMENT '科室类型',
  delmark     INT(1)       NOT NULL DEFAULT 1 COMMENT '1-正常 0-已删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_dept_code (dept_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='科室表';

-- ------------------------------------------------------------
-- 2. 医院员工表 employee
-- ------------------------------------------------------------
CREATE TABLE employee (
  id              INT(9)       NOT NULL AUTO_INCREMENT COMMENT '主键',
  username        VARCHAR(64)  NOT NULL COMMENT '登录账号（唯一）',
  password        VARCHAR(64)  NOT NULL COMMENT '登录密码（示例为明文，实际用 BCrypt 哈希）',
  realname        VARCHAR(64)  NOT NULL COMMENT '员工真实姓名',
  role            VARCHAR(32)  NOT NULL COMMENT '用户角色 window/doctor/check/lab/pharmacy/treat/admin',
  department_id   INT(9)       DEFAULT NULL COMMENT '所在科室 -> department(id)',
  regist_level_id INT(9)       DEFAULT NULL COMMENT '默认挂号级别 -> regist_level(id)',
  scheduling_id   INT(9)       DEFAULT NULL COMMENT '排班规则 -> scheduling(id)',
  delmark         INT(1)       NOT NULL DEFAULT 1 COMMENT '1-生效 0-已删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username),
  KEY idx_department_id (department_id),
  KEY idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='医院员工表';

-- ------------------------------------------------------------
-- 3. 挂号级别表 regist_level
-- ------------------------------------------------------------
CREATE TABLE regist_level (
  id          INT(9)        NOT NULL AUTO_INCREMENT COMMENT '主键',
  regist_code VARCHAR(64)   NOT NULL COMMENT '号别编码',
  regist_name VARCHAR(64)   NOT NULL COMMENT '号别名称',
  regist_fee  DECIMAL(8,2)  NOT NULL DEFAULT 0.00 COMMENT '挂号费',
  regist_quota INT(5)       NOT NULL DEFAULT 0 COMMENT '挂号限额',
  sequence_no INT(3)        NOT NULL DEFAULT 0 COMMENT '显示顺序号',
  delmark     INT(1)        NOT NULL DEFAULT 1 COMMENT '1-正常 0-已删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_regist_code (regist_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='挂号级别表';

-- ------------------------------------------------------------
-- 4. 结算类别表 settle_category
-- ------------------------------------------------------------
CREATE TABLE settle_category (
  id          INT(9)       NOT NULL AUTO_INCREMENT COMMENT '主键',
  settle_code VARCHAR(64)  NOT NULL COMMENT '类别编码',
  settle_name VARCHAR(64)  NOT NULL COMMENT '类别名称',
  sequence_no INT(3)       NOT NULL DEFAULT 0 COMMENT '显示顺序号',
  delmark     INT(1)       NOT NULL DEFAULT 1 COMMENT '1-正常 0-已删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_settle_code (settle_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='结算类别表';

-- ------------------------------------------------------------
-- 5. 排班表 scheduling
-- ------------------------------------------------------------
CREATE TABLE scheduling (
  id        INT(9)       NOT NULL AUTO_INCREMENT COMMENT '主键',
  rule_name VARCHAR(64)  NOT NULL COMMENT '排班规则名称',
  week_rule VARCHAR(14)  NOT NULL COMMENT '星期规则',
  delmark   INT(1)       NOT NULL DEFAULT 1 COMMENT '1-正常 0-已删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='排班表';

-- ------------------------------------------------------------
-- 6. 患者历次挂号信息表 register
-- ------------------------------------------------------------
CREATE TABLE register (
  id                INT(9)        NOT NULL AUTO_INCREMENT COMMENT '主键',
  case_number       VARCHAR(64)   NOT NULL COMMENT '病历号（患者唯一标识）',
  real_name         VARCHAR(64)   NOT NULL COMMENT '患者姓名',
  gender            VARCHAR(6)    NOT NULL COMMENT '性别 男/女',
  card_number       VARCHAR(18)   DEFAULT NULL COMMENT '身份证号',
  birthdate         DATE          DEFAULT NULL COMMENT '出生日期',
  age               INT(3)        DEFAULT NULL COMMENT '年龄',
  age_type          VARCHAR(6)    DEFAULT NULL COMMENT '年龄类型 年/天',
  home_address      VARCHAR(64)   DEFAULT NULL COMMENT '家庭住址',
  visit_date        DATETIME      NOT NULL COMMENT '本次看诊日期',
  noon              VARCHAR(6)    NOT NULL COMMENT '午别 上午/下午',
  department_id     INT(9)        NOT NULL COMMENT '挂号科室 -> department(id)',
  employee_id       INT(9)        NOT NULL COMMENT '接诊医生 -> employee(id)',
  regist_level_id   INT(9)        NOT NULL COMMENT '挂号级别 -> regist_level(id)',
  settle_category_id INT(9)       NOT NULL COMMENT '结算类别 -> settle_category(id)',
  is_book           CHAR(2)       NOT NULL DEFAULT '否' COMMENT '病历本要否 是/否',
  regist_method     VARCHAR(10)   NOT NULL COMMENT '收费方式 现金/银行卡/医保卡/微信/支付宝',
  regist_money      DECIMAL(8,2)  NOT NULL DEFAULT 0.00 COMMENT '挂号金额',
  visit_state       INT(1)        NOT NULL DEFAULT 1 COMMENT '1-已挂号 2-医生接诊 3-看诊结束 4-已退号',
  PRIMARY KEY (id),
  KEY idx_case_number (case_number),
  KEY idx_real_name (real_name),
  KEY idx_employee_id (employee_id),
  KEY idx_visit_state (visit_state),
  KEY idx_visit_date (visit_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='患者历次挂号信息表';

-- ------------------------------------------------------------
-- 7. 医技项目表 medical_technology
-- ------------------------------------------------------------
CREATE TABLE medical_technology (
  id            INT(9)        NOT NULL AUTO_INCREMENT COMMENT '主键',
  tech_code     VARCHAR(64)   NOT NULL COMMENT '项目编码',
  tech_name     VARCHAR(64)   NOT NULL COMMENT '项目名称',
  tech_format   VARCHAR(64)   DEFAULT NULL COMMENT '规格',
  tech_price    DECIMAL(8,2)  NOT NULL DEFAULT 0.00 COMMENT '单价',
  tech_type     VARCHAR(64)   NOT NULL COMMENT '类型 检查/检验/处置',
  price_type    VARCHAR(64)   DEFAULT NULL COMMENT '费用分类',
  department_id INT(9)        DEFAULT NULL COMMENT '执行科室 -> department(id)',
  PRIMARY KEY (id),
  UNIQUE KEY uk_tech_code (tech_code),
  KEY idx_tech_name (tech_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='医技项目表';

-- ------------------------------------------------------------
-- 8. 检查申请表 check_request
-- ------------------------------------------------------------
CREATE TABLE check_request (
  id                      INT(9)       NOT NULL AUTO_INCREMENT COMMENT '主键',
  register_id             INT(9)       NOT NULL COMMENT '挂号 -> register(id)',
  medical_technology_id   INT(9)       NOT NULL COMMENT '医技项目 -> medical_technology(id)',
  check_info              VARCHAR(512) DEFAULT NULL COMMENT '检查目的要求',
  check_position          VARCHAR(255) DEFAULT NULL COMMENT '检查部位',
  creation_time           DATETIME     NOT NULL COMMENT '开立时间',
  check_employee_id       INT(9)       DEFAULT NULL COMMENT '检查人员 -> employee(id)',
  inputcheck_employee_id  INT(9)       DEFAULT NULL COMMENT '结果录入人 -> employee(id)',
  check_time              DATETIME     DEFAULT NULL COMMENT '检查时间',
  check_result            VARCHAR(512) DEFAULT NULL COMMENT '检查结果',
  check_state             VARCHAR(64)  NOT NULL DEFAULT '1' COMMENT '1-已开立 2-已执行 3-已完成 4-已撤销',
  check_remark            VARCHAR(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_register_id (register_id),
  KEY idx_medical_technology_id (medical_technology_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='检查申请表';

-- ------------------------------------------------------------
-- 9. 检验申请表 inspection_request
-- ------------------------------------------------------------
CREATE TABLE inspection_request (
  id                          INT(9)       NOT NULL AUTO_INCREMENT COMMENT '主键',
  register_id                 INT(9)       NOT NULL COMMENT '挂号 -> register(id)',
  medical_technology_id       INT(9)       NOT NULL COMMENT '医技项目 -> medical_technology(id)',
  inspection_info             VARCHAR(512) DEFAULT NULL COMMENT '检验目的要求',
  inspection_position         VARCHAR(255) DEFAULT NULL COMMENT '检验部位',
  creation_time               DATETIME     NOT NULL COMMENT '开立时间',
  inspection_employee_id      INT(9)       DEFAULT NULL COMMENT '检验人员 -> employee(id)',
  inputinspection_employee_id INT(9)       DEFAULT NULL COMMENT '结果录入人 -> employee(id)',
  inspection_time             DATETIME     DEFAULT NULL COMMENT '检验时间',
  inspection_result           VARCHAR(512) DEFAULT NULL COMMENT '检验结果',
  inspection_state            VARCHAR(64)  NOT NULL DEFAULT '1' COMMENT '1-已开立 2-已执行 3-已完成 4-已撤销',
  inspection_remark           VARCHAR(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_register_id (register_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='检验申请表';

-- ------------------------------------------------------------
-- 10. 处置申请表 disposal_request
-- ------------------------------------------------------------
CREATE TABLE disposal_request (
  id                        INT(9)       NOT NULL AUTO_INCREMENT COMMENT '主键',
  register_id               INT(9)       NOT NULL COMMENT '挂号 -> register(id)',
  medical_technology_id     INT(9)       NOT NULL COMMENT '医技项目 -> medical_technology(id)',
  disposal_info             VARCHAR(512) DEFAULT NULL COMMENT '处置目的要求',
  disposal_position         VARCHAR(255) DEFAULT NULL COMMENT '处置部位',
  creation_time             DATETIME     NOT NULL COMMENT '开立时间',
  disposal_employee_id      INT(9)       DEFAULT NULL COMMENT '处置人员 -> employee(id)',
  inputdisposal_employee_id INT(9)       DEFAULT NULL COMMENT '结果录入人 -> employee(id)',
  disposal_time             DATETIME     DEFAULT NULL COMMENT '处置时间',
  disposal_result           VARCHAR(512) DEFAULT NULL COMMENT '处置结果',
  disposal_state            VARCHAR(64)  NOT NULL DEFAULT '1' COMMENT '1-已开立 2-已执行 3-已完成 4-已撤销',
  disposal_remark           VARCHAR(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_register_id (register_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='处置申请表';

-- ------------------------------------------------------------
-- 11. 患者病历表 medical_record
-- ------------------------------------------------------------
CREATE TABLE medical_record (
  id            INT(9)       NOT NULL AUTO_INCREMENT COMMENT '主键',
  register_id   INT(9)       NOT NULL COMMENT '挂号 -> register(id)（唯一，一次挂号一份病历）',
  readme        VARCHAR(512) DEFAULT NULL COMMENT '主诉',
  present       VARCHAR(512) DEFAULT NULL COMMENT '现病史',
  present_treat VARCHAR(512) DEFAULT NULL COMMENT '现病治疗情况',
  history       VARCHAR(512) DEFAULT NULL COMMENT '既往史',
  allergy       VARCHAR(512) DEFAULT NULL COMMENT '过敏史',
  physique      VARCHAR(512) DEFAULT NULL COMMENT '体格检查',
  proposal      VARCHAR(512) DEFAULT NULL COMMENT '检查/检验建议',
  careful       VARCHAR(512) DEFAULT NULL COMMENT '注意事项',
  diagnosis     VARCHAR(512) DEFAULT NULL COMMENT '诊断结果',
  cure          VARCHAR(512) DEFAULT NULL COMMENT '处理意见',
  PRIMARY KEY (id),
  UNIQUE KEY uk_register_id (register_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='患者病历表';

-- ------------------------------------------------------------
-- 12. 疾病表 disease
-- ------------------------------------------------------------
CREATE TABLE disease (
  id              INT(9)       NOT NULL AUTO_INCREMENT COMMENT '主键',
  disease_code    VARCHAR(64)  NOT NULL COMMENT '疾病助记编码',
  disease_name    VARCHAR(255) NOT NULL COMMENT '疾病名称',
  disease_icd     VARCHAR(64)  DEFAULT NULL COMMENT '国际 ICD 编码',
  disease_category VARCHAR(64) DEFAULT NULL COMMENT '疾病所属分类',
  PRIMARY KEY (id),
  KEY idx_disease_name (disease_name),
  KEY idx_disease_icd (disease_icd)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='疾病表';

-- ------------------------------------------------------------
-- 13. 病历-疾病关联表 medical_record_disease
-- ------------------------------------------------------------
CREATE TABLE medical_record_disease (
  medical_record_id INT(9) NOT NULL COMMENT '病历 -> medical_record(id)',
  disease_id        INT(9) NOT NULL COMMENT '疾病 -> disease(id)',
  PRIMARY KEY (medical_record_id, disease_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='病历-疾病关联表';

-- ------------------------------------------------------------
-- 14. 药品信息表 drug_info
-- ------------------------------------------------------------
CREATE TABLE drug_info (
  id            INT(9)        NOT NULL AUTO_INCREMENT COMMENT '主键',
  drug_code     VARCHAR(255)  NOT NULL COMMENT '药品编码',
  drug_name     VARCHAR(255)  NOT NULL COMMENT '药品名称',
  drug_format   VARCHAR(255)  DEFAULT NULL COMMENT '药品规格',
  drug_unit     VARCHAR(16)   DEFAULT NULL COMMENT '包装单位',
  manufacturer  VARCHAR(255)  DEFAULT NULL COMMENT '生产厂家',
  drug_dosage   VARCHAR(64)   DEFAULT NULL COMMENT '药剂类型',
  drug_type     VARCHAR(64)   DEFAULT NULL COMMENT '药品类型',
  drug_price    DECIMAL(8,2)  NOT NULL DEFAULT 0.00 COMMENT '药品单价',
  mnemonic_code VARCHAR(255)  DEFAULT NULL COMMENT '拼音助记码',
  creation_date DATE          DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (id),
  UNIQUE KEY uk_drug_code (drug_code),
  KEY idx_drug_name (drug_name),
  KEY idx_mnemonic_code (mnemonic_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='药品信息表';

-- ------------------------------------------------------------
-- 15. 处方表 prescription
-- ------------------------------------------------------------
CREATE TABLE prescription (
  id            INT(9)       NOT NULL AUTO_INCREMENT COMMENT '主键',
  register_id   INT(9)       NOT NULL COMMENT '挂号 -> register(id)',
  drug_id       INT(9)       NOT NULL COMMENT '药品 -> drug_info(id)',
  drug_usage    VARCHAR(255) DEFAULT NULL COMMENT '用法用量频次',
  drug_number   VARCHAR(255) DEFAULT NULL COMMENT '药品数量',
  creation_time DATETIME     NOT NULL COMMENT '开立时间',
  drug_state    VARCHAR(64)  NOT NULL DEFAULT '1' COMMENT '1-已开立 2-已发药 3-已退药 4-已撤销',
  PRIMARY KEY (id),
  KEY idx_register_id (register_id),
  KEY idx_drug_id (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='处方表';

-- ------------------------------------------------------------
-- 16. 费用流水表 fee_record
-- ------------------------------------------------------------
CREATE TABLE fee_record (
  id               INT(9)        NOT NULL AUTO_INCREMENT COMMENT '主键',
  register_id      INT(9)        NOT NULL COMMENT '挂号 -> register(id)',
  item_type        VARCHAR(32)   NOT NULL COMMENT '项目类型 挂号费/检查/检验/处置/处方',
  item_id          INT(9)        DEFAULT NULL COMMENT '对应业务表记录 id',
  fee_type         VARCHAR(8)    NOT NULL COMMENT '流水类型 收费/退费',
  fee_amount       DECIMAL(8,2)  NOT NULL DEFAULT 0.00 COMMENT '流水金额',
  fee_method       VARCHAR(16)   NOT NULL COMMENT '收费方式 现金/银行卡/医保/微信/支付宝',
  refund_target_id INT(9)        DEFAULT NULL COMMENT '退费指向被退的收费记录 id',
  operator_id      INT(9)        DEFAULT NULL COMMENT '操作人 -> employee(id)',
  remark           VARCHAR(255)  DEFAULT NULL COMMENT '备注',
  create_time      DATETIME      NOT NULL COMMENT '收费/退费时间',
  PRIMARY KEY (id),
  KEY idx_register_id (register_id),
  KEY idx_item (item_type, item_id),
  KEY idx_refund_target_id (refund_target_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='费用流水表';

-- ============================================================
-- 初始数据（示例，可按需修改）
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
