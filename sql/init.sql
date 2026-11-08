-- ============================================================
-- 东软云HIS医院管理系统 —— 数据库建表脚本（仅建表，不含数据）
-- 数据库：his
-- 目标：MySQL 5.5.x（InnoDB / utf8）
-- 说明：
--   1) 仅创建表结构；基础数据与演示数据见 demo-data.sql
--   2) 表间仅逻辑外键（xxx_id），不建物理 FOREIGN KEY（见契约 2.6）
--   3) 字符集用 utf8（3 字节）：MySQL 5.5 索引上限 767 字节，
--      utf8mb4 会令 VARCHAR(255) 索引超长，故统一 utf8
--   4) 主键 id 自增；基础数据表带 delmark 软删除标记
-- ============================================================

DROP DATABASE IF EXISTS his;
CREATE DATABASE his DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE his;
DROP TABLE IF EXISTS outpatient_queue;
DROP TABLE IF EXISTS medical_record_revision;
DROP TABLE IF EXISTS drug_transaction;

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
  version INT NOT NULL DEFAULT 0,
  diagnosis_state INT NOT NULL DEFAULT 0,
  created_by INT DEFAULT NULL,
  created_at DATETIME DEFAULT NULL,
  updated_by INT DEFAULT NULL,
  updated_at DATETIME DEFAULT NULL,
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
  delmark INT NOT NULL DEFAULT 1,
  stock_quantity INT NOT NULL DEFAULT 0,
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
  unit_price DECIMAL(8,2) DEFAULT NULL,
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

CREATE TABLE outpatient_queue (
  register_id INT NOT NULL PRIMARY KEY,
  queue_state VARCHAR(16) NOT NULL DEFAULT 'waiting',
  updated_by INT NOT NULL,
  updated_at DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE medical_record_revision (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  medical_record_id INT NOT NULL,
  version INT NOT NULL,
  content TEXT NOT NULL,
  operator_id INT NOT NULL,
  create_time DATETIME NOT NULL,
  UNIQUE KEY uk_record_version (medical_record_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE drug_transaction (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  drug_id INT NOT NULL,
  prescription_id INT DEFAULT NULL,
  transaction_type VARCHAR(16) NOT NULL,
  quantity INT NOT NULL,
  stock_after INT NOT NULL,
  reason VARCHAR(255) NOT NULL,
  operator_id INT NOT NULL,
  create_time DATETIME NOT NULL,
  KEY idx_prescription (prescription_id),
  KEY idx_drug (drug_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
