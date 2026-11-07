# WWJ 门诊医生与药房实现约定

本次以开发契约 4.4、4.8 为接入基线。原接口保留；已存在的疾病、药品、医技字典及费用接口直接复用，禁止重复注册 Controller。

## 数据补充

- medical_record 增加 version、diagnosis_state、created_by、created_at、updated_by、updated_at，保存版本与确诊状态。
- drug_info 增加 delmark 与 stock_quantity，支持停用和总量库存。停用不删除历史数据。
- prescription 增加 unit_price 快照。收费与历史费用显示优先读取快照，兼容旧数据回退到药品现价。
- 新增 outpatient_queue 保存叫号和过号状态；medical_record_revision 保存病历修订快照；drug_transaction 保存发药、退药、入库、报损及盘点调整。
- sql/init.sql 同步新建库结构；已有本地库仅执行一次 sql/upgrade_wwj.sql，不执行清库脚本。

## 边界补充

register 仍由挂号模块写入。新增挂号域 VisitLifecycleService 为门诊提供本人挂号的接诊与结束方法，仅允许 visit_state 1→2、2→3。门诊 Mapper 不写 register。为避免接诊与退号竞态，原退号方法改为锁定后校验。

收费流水仍只由窗口写入。门诊开方、药房发退药不写 fee_record。收费、退费与药房操作按业务项目行加锁串行处理；处方发药前按 register_id、item_type、item_id 检查未被退费冲销的收费记录及金额。

医技申请开立与读取由门诊负责，HYJ 的执行登记与结果录入接口不变。门诊不修改医技报告。药品删除实际为停用；未收费处方引用的药品禁止改价，保证已开立的待收费费用口径一致。

## 新增接口

| 方法 | 路径 | 用途 |
| --- | --- | --- |
| PUT | /api/outpatient/visits/{id}/call | 叫号，仅更新独立候诊状态 |
| PUT | /api/outpatient/visits/{id}/skip | 过号，允许再次叫号 |
| PUT | /api/outpatient/visits/{id}/start | 挂号域开始接诊 |
| PUT | /api/outpatient/visits/{id}/finish | 确诊后结束接诊 |
| GET | /api/outpatient/visits/{id}/fees | 本次挂号的应收与实收流水 |
| GET | /api/outpatient/visits/{id}/requests | 本次挂号三类申请和结果 |
| PUT | /api/outpatient/requests/{kind}/{id}/cancel | 未缴费且未执行申请撤销 |
| GET | /api/medical-record/{id}/revisions | 当前医生所属病历的版本记录 |
| PUT | /api/prescription/{id}/cancel | 医生撤销未缴费处方 |
| POST | /api/drug/{id}/stock | 入库、报损或盘点调整，必须填写原因 |
| GET | /api/pharmacy/transactions | 药品操作流水分页查询 |
| GET | /api/pharmacy/drugs | 药房含停用药品的分页目录 |

kind 仅允许 check、inspection、disposal。查询分页参数 page、size，返回 {items,total,page,size}。候诊支持 keyword、date、noon、visitState；处方支持 keyword、drugState、paid、date、registerId；交易支持 keyword、type、date。日期 yyyy-MM-dd，默认候诊和交易为当天。

病历保存请求为病历字段及 version；确诊请求为 diagnosis、cure、diseaseIds、version。创建病历不接受客户端确诊状态。处方请求为 registerId、drugId、drugUsage、drugNumber，数量为正整数。发退药按原表一药一条处方整条操作；退药必须提交 {reason}。库存请求为 {type,quantity,reason}，type 为入库、报损或盘点；盘点 quantity 是盘点后实际总量，其余是本次变动量。

## 本次实现与扩展设计的界限

实现基础合同下的一药一条处方、整条发药和整条退药、总量库存与流水、病历版本、候诊标记、医技申请及结果查看。开方不扣库存、不伪造收费；发药时才扣库存。重复发药或退药返回明确业务错误，不二次扣库。

设计说明书中的处方头与整单、多批次及有效期、部分退药、隔离库存、调拨、fee_item、报告版本引用、请求标识幂等、打印及导出尚不属于这版基础实现。不能把本包描述为已实现上述扩展；若课程验收逐条要求这些内容，需要三位成员统一费用和医技模型后继续扩展。
