-- 已有 his 库升级，只执行一次；不清空原表和数据。先备份本地数据库。
USE his;
ALTER TABLE medical_record
 ADD COLUMN version INT NOT NULL DEFAULT 0,
 ADD COLUMN diagnosis_state INT NOT NULL DEFAULT 0,
 ADD COLUMN created_by INT DEFAULT NULL,
 ADD COLUMN created_at DATETIME DEFAULT NULL,
 ADD COLUMN updated_by INT DEFAULT NULL,
 ADD COLUMN updated_at DATETIME DEFAULT NULL;
ALTER TABLE drug_info
 ADD COLUMN delmark INT NOT NULL DEFAULT 1,
 ADD COLUMN stock_quantity INT NOT NULL DEFAULT 0;
ALTER TABLE prescription ADD COLUMN unit_price DECIMAL(8,2) DEFAULT NULL;
UPDATE prescription p JOIN drug_info d ON p.drug_id=d.id SET p.unit_price=d.drug_price;
-- 历史确诊需由医生核对后重新保存，升级脚本不自动虚构确诊状态。
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
