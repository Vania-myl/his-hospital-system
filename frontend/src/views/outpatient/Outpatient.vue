<template>
  <div class="doctor-layout">
    <aside class="page-card patients">
      <h3>我的门诊患者</h3>
      <el-form size="small" @submit.native.prevent="search">
        <el-form-item><el-input v-model.trim="query.keyword" clearable placeholder="病历号或姓名" @keyup.enter.native="search" /></el-form-item>
        <el-form-item><el-date-picker v-model="query.date" type="date" value-format="yyyy-MM-dd" placeholder="就诊日期" style="width:100%" /></el-form-item>
        <el-form-item><el-select v-model="query.noon" clearable placeholder="全部午别" style="width:100%"><el-option label="上午" value="上午" /><el-option label="下午" value="下午" /></el-select></el-form-item>
        <el-form-item><el-select v-model="query.visitState" clearable placeholder="全部就诊状态" style="width:100%"><el-option label="已挂号" :value="1" /><el-option label="接诊中" :value="2" /><el-option label="已结束" :value="3" /></el-select></el-form-item>
        <el-button type="primary" size="small" :loading="patientsLoading" @click="search">查询</el-button>
        <el-button size="small" @click="resetQuery">重置</el-button>
      </el-form>
      <el-table :data="patients" v-loading="patientsLoading" highlight-current-row size="small" @row-click="selectPatient">
        <el-table-column prop="realName" label="姓名" width="75" />
        <el-table-column label="状态"><template slot-scope="{row}">{{ visitLabel(row.visitState) }}<small v-if="Number(row.visitState)===1"> · {{ queueLabel(row.queueState) }}</small></template></el-table-column>
      </el-table>
      <el-pagination small layout="prev, pager, next" :page-size="query.size" :total="patientTotal" :current-page.sync="query.page" @current-change="loadPatients" />
    </aside>
    <section class="page-card work" v-loading="detailLoading">
      <el-empty v-if="!patient" description="从左侧选择本次就诊患者" />
      <template v-else>
        <el-descriptions border :column="3" size="small">
          <el-descriptions-item label="姓名">{{ patient.realName }}（{{ patient.gender }} {{ patient.age }}岁）</el-descriptions-item>
          <el-descriptions-item label="病历号">{{ patient.caseNumber }}</el-descriptions-item>
          <el-descriptions-item label="挂号编号">{{ patient.id }}</el-descriptions-item>
          <el-descriptions-item label="就诊日期">{{ patient.visitDate }}</el-descriptions-item>
          <el-descriptions-item label="就诊状态">{{ visitLabel(patient.visitState) }}</el-descriptions-item>
          <el-descriptions-item label="确诊状态">{{ form.diagnosisState===1 ? '已确诊' : '未确诊' }}</el-descriptions-item>
        </el-descriptions>
        <div class="actions">
          <el-button size="small" :disabled="Number(patient.visitState)!==1 || busy" @click="act('call')">叫号</el-button>
          <el-button size="small" :disabled="Number(patient.visitState)!==1 || busy" @click="act('skip')">过号</el-button>
          <el-button size="small" type="primary" :disabled="Number(patient.visitState)!==1 || busy" @click="act('start')">开始接诊</el-button>
          <el-button size="small" type="success" :disabled="!editable || form.diagnosisState!==1 || busy" @click="act('finish')">结束看诊</el-button>
          <el-button size="small" :disabled="busy" @click="reload">刷新本次就诊</el-button>
        </div>
        <el-alert v-if="!editable" :title="Number(patient.visitState)===1 ? '点击开始接诊后填写病历' : '本次看诊已结束，记录只读'" type="info" :closable="false" />
        <el-tabs v-model="tab">
          <el-tab-pane label="病历与确诊" name="record">
            <el-form ref="recordForm" :model="form" :rules="rules" label-width="118px" size="small" :disabled="!editable || busy">
              <el-form-item v-for="field in recordFields" :key="field.key" :label="field.label" :prop="field.key"><el-input v-model="form[field.key]" type="textarea" :rows="2" maxlength="512" show-word-limit /></el-form-item>
              <el-form-item><el-button type="primary" :loading="busy" @click="save">保存病历</el-button></el-form-item>
            </el-form>
            <el-divider content-position="left">ICD 确诊</el-divider>
            <el-form label-width="118px" size="small" :disabled="!editable || busy || !form.id">
              <el-form-item label="诊断疾病" required><el-select v-model="diagnosis.diseaseIds" multiple filterable placeholder="搜索疾病名称或ICD" style="width:100%"><el-option v-for="d in diseases" :key="d.id" :value="d.id" :label="d.diseaseName+' '+(d.diseaseIcd||'')" /></el-select></el-form-item>
              <el-form-item label="诊断说明" required><el-input v-model="diagnosis.diagnosis" type="textarea" maxlength="512" /></el-form-item>
              <el-form-item label="处理意见"><el-input v-model="diagnosis.cure" type="textarea" maxlength="512" /></el-form-item>
              <el-form-item><el-button type="success" :loading="busy" @click="confirmDiagnosis">保存确诊</el-button></el-form-item>
            </el-form>
            <el-button size="small" :disabled="!form.id" @click="showRevisions">查看病历版本</el-button>
          </el-tab-pane>
          <el-tab-pane label="检查 检验 处置" name="orders">
            <el-form inline size="small" :disabled="!editable || !form.id || busy">
              <el-form-item label="类型"><el-select v-model="order.kind" style="width:100px" @change="order.medicalTechnologyId=null"><el-option v-for="(v,k) in kinds" :key="k" :value="k" :label="v" /></el-select></el-form-item>
              <el-form-item label="医技项目"><el-select v-model="order.medicalTechnologyId" filterable placeholder="选择项目"><el-option v-for="t in matchingTechnologies" :key="t.id" :value="t.id" :label="t.techName+' ¥'+money(t.techPrice)" /></el-select></el-form-item>
              <el-form-item label="部位或标本"><el-input v-model.trim="order.position" maxlength="255" /></el-form-item>
              <el-form-item label="目的要求"><el-input v-model.trim="order.info" maxlength="512" /></el-form-item>
              <el-button type="primary" :loading="busy" @click="submitOrder">开立申请</el-button>
            </el-form>
            <el-table :data="requests" border size="small" empty-text="本次就诊暂无申请">
              <el-table-column prop="id" label="申请号" width="70" /><el-table-column label="类型" width="70"><template slot-scope="{row}">{{ kinds[row.kind] }}</template></el-table-column>
              <el-table-column prop="techName" label="项目" /><el-table-column prop="position" label="部位或标本" />
              <el-table-column label="状态" width="90"><template slot-scope="{row}">{{ orderLabel(row.state) }}</template></el-table-column>
              <el-table-column label="结果"><template slot-scope="{row}">{{ row.result || '尚无结果' }}</template></el-table-column>
              <el-table-column label="操作" width="80"><template slot-scope="{row}"><el-button type="text" :disabled="!editable || row.state!=='1' || busy" @click="removeOrder(row)">撤销</el-button></template></el-table-column>
            </el-table>
          </el-tab-pane>
          <el-tab-pane label="开立处方" name="prescriptions">
            <el-form inline size="small" :disabled="!editable || form.diagnosisState!==1 || busy">
              <el-form-item label="药品"><el-select v-model="prescription.drugId" filterable placeholder="名称或助记码"><el-option v-for="d in drugs" :key="d.id" :value="d.id" :label="d.drugName+' '+(d.drugFormat||'')+' '+(d.mnemonicCode||'')+' 库存'+d.stockQuantity" /></el-select></el-form-item>
              <el-form-item label="数量"><el-input-number v-model="prescription.drugNumber" :min="1" :max="999999" :precision="0" /></el-form-item>
              <el-form-item label="用法用量频次"><el-input v-model.trim="prescription.drugUsage" maxlength="255" placeholder="填写途径、每次用量、频次和疗程" style="width:300px" /></el-form-item>
              <el-button type="primary" :loading="busy" @click="submitPrescription">开立处方</el-button>
            </el-form>
            <el-alert title="开立后由窗口收费，药房核对后发药。开方不扣库存。" :closable="false" type="info" />
            <el-table :data="prescriptions" border size="small">
              <el-table-column prop="id" label="处方号" width="75" /><el-table-column prop="drugName" label="药品" /><el-table-column prop="drugNumber" label="数量" width="65" />
              <el-table-column prop="drugUsage" label="医嘱" /><el-table-column label="金额" width="90"><template slot-scope="{row}">¥{{ money(Number(row.unitPrice)*Number(row.drugNumber)) }}</template></el-table-column>
              <el-table-column label="状态" width="90"><template slot-scope="{row}">{{ drugLabel(row.drugState) }}</template></el-table-column>
              <el-table-column label="缴费" width="80"><template slot-scope="{row}">{{ Number(row.paid) ? '已缴费' : '未缴费' }}</template></el-table-column>
              <el-table-column label="操作" width="70"><template slot-scope="{row}"><el-button type="text" :disabled="!editable || row.drugState!=='1' || Number(row.paid)===1 || busy" @click="removePrescription(row)">撤销</el-button></template></el-table-column>
            </el-table>
            <el-pagination layout="prev, pager, next, total" :current-page.sync="prescriptionPage" :page-size="20" :total="prescriptionTotal" @current-change="loadPrescriptions" />
          </el-tab-pane>
          <el-tab-pane label="本次就诊费用" name="fees">
            <h4>应收项目</h4><el-table :data="feeInfo.items || []" border size="small"><el-table-column prop="itemType" label="类型" /><el-table-column prop="itemName" label="项目" /><el-table-column label="应收"><template slot-scope="{row}">¥{{ money(row.amount) }}</template></el-table-column><el-table-column label="净已收"><template slot-scope="{row}">¥{{ money(row.paidAmount) }}</template></el-table-column><el-table-column label="缴费状态"><template slot-scope="{row}">{{ row.state==='4' ? '已撤销' : Number(row.paidAmount)>=Number(row.amount) && Number(row.paidAmount)>0 ? '已缴费' : '待缴费或已退费' }}</template></el-table-column></el-table>
            <h4>真实收费与退费流水</h4><el-table :data="feeInfo.records || []" border size="small"><el-table-column prop="itemType" label="项目类型" /><el-table-column prop="itemId" label="项目号" /><el-table-column prop="feeType" label="收退方向" /><el-table-column label="金额"><template slot-scope="{row}">¥{{ money(row.feeAmount) }}</template></el-table-column><el-table-column prop="createTime" label="时间" /></el-table>
          </el-tab-pane>
        </el-tabs>
      </template>
    </section>
    <el-dialog title="病历版本记录" :visible.sync="revisionVisible" width="75%">
      <el-collapse><el-collapse-item v-for="r in revisionItems" :key="r.version" :title="'版本 '+r.version+' · '+r.createTime+' · 操作人 '+r.operatorId" :name="r.version"><el-descriptions :column="1" border size="small"><el-descriptions-item v-for="f in revisionFields" :key="f.key" :label="f.label">{{ parseRevision(r.content)[f.key] || '无' }}</el-descriptions-item></el-descriptions></el-collapse-item></el-collapse>
    </el-dialog>
  </div>
</template>
<script>
import * as api from '@/api/outpatient'
import * as pharmacy from '@/api/pharmacy'
import { diseaseList, drugList, medicalTechnologyList } from '@/api/base'
const fields = [{key:'readme',label:'主诉'},{key:'present',label:'现病史'},{key:'history',label:'既往史'},{key:'allergy',label:'过敏史'},{key:'physique',label:'体格检查'},{key:'presentTreat',label:'现病治疗情况'},{key:'proposal',label:'检查建议'},{key:'careful',label:'注意事项'},{key:'cure',label:'处理意见'}]
function blankRecord() { const v={ id:null,version:null,diagnosisState:0 }; fields.forEach(f => {v[f.key]=''}); return v }
function today() { const d=new Date(); return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}` }
export default {
  name:'Outpatient',
  data() { return { query:{keyword:'',date:today(),noon:'',visitState:null,page:1,size:10},patients:[],patientTotal:0,patient:null,patientsLoading:false,detailLoading:false,busy:false,selection:0,tab:'record',form:blankRecord(),diagnosis:{diseaseIds:[],diagnosis:'',cure:''},recordFields:fields,revisionFields:[...fields,{key:'diagnosis',label:'诊断说明'}],rules:Object.fromEntries(fields.slice(0,5).map(f => [f.key,[{required:true,whitespace:true,message:'请填写'+f.label,trigger:'blur'}]])),diseases:[],drugs:[],technologies:[],requests:[],prescriptions:[],prescriptionTotal:0,prescriptionPage:1,feeInfo:{},order:{kind:'check',medicalTechnologyId:null,position:'',info:''},kinds:{check:'检查',inspection:'检验',disposal:'处置'},prescription:{drugId:null,drugNumber:1,drugUsage:''},revisionItems:[],revisionVisible:false } },
  computed:{ editable(){return this.patient && Number(this.patient.visitState)===2},matchingTechnologies(){return this.technologies.filter(t => t.techType===this.kinds[this.order.kind])} },
  created() { this.loadPatients(); this.loadDictionaries() },
  methods:{
    money(v){return Number(v||0).toFixed(2)}, visitLabel(v){return {1:'已挂号',2:'接诊中',3:'已结束',4:'已退号'}[v]||v},queueLabel(v){return {waiting:'候诊',called:'已叫号',skipped:'已过号'}[v]||v},orderLabel(v){return {1:'已开立',2:'已执行',3:'已完成',4:'已撤销'}[v]||v},drugLabel(v){return {1:'已开立',2:'已发药',3:'已退药',4:'已撤销'}[v]||v},
    async loadDictionaries(){try{[this.diseases,this.drugs,this.technologies]=await Promise.all([diseaseList(),drugList(),medicalTechnologyList()])}catch(e){/* 请求层显示错误 */}},
    search(){this.query.page=1;this.loadPatients()},resetQuery(){Object.assign(this.query,{keyword:'',date:today(),noon:'',visitState:null,page:1});this.loadPatients()},
    async loadPatients(){this.patientsLoading=true;try{const d=await api.waiting(this.query);this.patients=d.items;this.patientTotal=d.total;if(this.patient){const match=this.patients.find(p=>p.id===this.patient.id);if(match)this.patient={...match}}}catch(e){/* 请求层显示错误 */}finally{this.patientsLoading=false}},
    async selectPatient(row){if(this.busy)return;const seq=++this.selection;this.patient={...row};this.form=blankRecord();this.diagnosis={diseaseIds:[],diagnosis:'',cure:''};this.requests=[];this.prescriptions=[];this.prescriptionTotal=0;this.prescriptionPage=1;this.feeInfo={};this.prescription={drugId:null,drugNumber:1,drugUsage:''};this.order={kind:'check',medicalTechnologyId:null,position:'',info:''};this.tab='record';this.detailLoading=true;try{await this.loadDetails(row.id,seq)}catch(e){/* 请求层显示错误 */}finally{if(seq===this.selection)this.detailLoading=false}},
    async loadDetails(id,seq){const [r,orders,prescriptions,fees]=await Promise.all([api.record(id),api.orders(id),pharmacy.prescriptions({registerId:id,page:1,size:20}),api.fees(id)]);if(seq!==this.selection)return;this.form={...blankRecord(),...(r||{}),registerId:id};this.diagnosis={diseaseIds:(r&&r.diseaseIds)||[],diagnosis:(r&&r.diagnosis)||'',cure:(r&&r.cure)||''};this.requests=orders;this.prescriptions=prescriptions.items;this.prescriptionTotal=prescriptions.total;this.feeInfo=fees},
    async loadPrescriptions(){const id=this.patient.id;try{const d=await pharmacy.prescriptions({registerId:id,page:this.prescriptionPage,size:20});if(this.patient&&this.patient.id===id){this.prescriptions=d.items;this.prescriptionTotal=d.total}}catch(e){/* 请求层显示错误 */}},
    async reload(){if(!this.patient)return;await this.loadPatients();await this.loadDetails(this.patient.id,this.selection);await this.loadDictionaries()},
    async run(fn){if(this.busy)return;this.busy=true;try{await fn()}catch(e){/* 请求层显示错误；输入保留 */}finally{this.busy=false}},
    act(action){this.run(async()=>{if(action==='finish')await this.$confirm('确认结束本次看诊？结束后病历与医嘱只读。','结束看诊',{type:'warning'});await api.visitAction(this.patient.id,action);if(action==='start')this.patient.visitState=2;if(action==='finish')this.patient.visitState=3;this.$message.success('操作成功');await this.reload()})},
    save(){this.$refs.recordForm.validate(valid=>{if(!valid)return;this.run(async()=>{await api.saveRecord({...this.form,registerId:this.patient.id});this.$message.success('病历已保存');await this.reload()})})},
    confirmDiagnosis(){if(!this.diagnosis.diseaseIds.length||!this.diagnosis.diagnosis.trim())return this.$message.warning('请选择疾病并填写诊断说明');this.run(async()=>{await api.diagnose(this.form.id,{...this.diagnosis,version:this.form.version});this.$message.success('确诊已保存');await this.reload()})},
    submitOrder(){if(!this.order.medicalTechnologyId||!this.order.info.trim()||!this.order.position.trim())return this.$message.warning('请选择项目并填写部位或标本、目的要求');this.run(async()=>{await api.createOrder(this.order.kind,{...this.order,registerId:this.patient.id});this.$message.success('申请已开立，请到窗口缴费');this.order.info='';this.order.position='';await this.reload()})},
    removeOrder(row){this.run(async()=>{await this.$confirm('确认撤销该申请？已缴费须先退费。','撤销申请',{type:'warning'});await api.cancelOrder(row.kind,row.id);await this.reload()})},
    submitPrescription(){if(!this.prescription.drugId||!this.prescription.drugUsage.trim())return this.$message.warning('请选择药品并填写完整医嘱');this.run(async()=>{const d=this.drugs.find(d=>d.id===this.prescription.drugId);if(d&&Number(d.stockQuantity)<Number(this.prescription.drugNumber))await this.$confirm('当前库存不足，开立后须补充库存才能发药，是否继续？','库存提示',{type:'warning'});await pharmacy.prescribe({...this.prescription,drugNumber:String(this.prescription.drugNumber),registerId:this.patient.id});this.$message.success('处方已开立，请到窗口缴费');this.prescription={drugId:null,drugNumber:1,drugUsage:''};await this.reload()})},
    removePrescription(row){this.run(async()=>{await this.$confirm('确认撤销该未缴费处方？','撤销处方',{type:'warning'});await pharmacy.cancelPrescription(row.id);await this.reload()})},
    async showRevisions(){try{const id=this.form.id;const data=await api.revisions(id);if(this.form.id===id){this.revisionItems=data;this.revisionVisible=true}}catch(e){/* 请求层显示错误 */}},parseRevision(s){try{return JSON.parse(s)}catch(e){return {}}}
  }
}
</script>
<style scoped>
.doctor-layout{display:grid;grid-template-columns:260px minmax(0,1fr);gap:16px;align-items:start}.patients{padding:16px}.patients h3{margin:0 0 16px}.patients .el-form-item{margin-bottom:12px}.patients .el-table{margin-top:16px}.work{min-height:600px}.actions{margin:14px 0}.el-pagination{margin-top:16px}.el-alert{margin:12px 0}.el-form--inline{padding-top:14px}small{color:#909399}@media(max-width:1000px){.doctor-layout{grid-template-columns:220px minmax(0,1fr)}}
</style>
