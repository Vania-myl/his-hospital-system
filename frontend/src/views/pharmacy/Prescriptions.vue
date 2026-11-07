<template>
  <div class="page-card">
    <h3>{{ returning ? '药房退药' : '药房发药' }}</h3>
    <el-alert :title="returning ? '核对原处方和实际退回药品后，按处方明细整条退药。退药后患者需到窗口办理资金退费。' : '仅已缴费且尚未发药的处方可发药。请核对患者、药品、规格、数量及医嘱。'" type="info" :closable="false" />
    <el-form inline size="small" class="search-bar" @submit.native.prevent="search">
      <el-form-item label="检索"><el-input v-model.trim="query.keyword" placeholder="病历号 姓名 处方号 药品" clearable @keyup.enter.native="search" /></el-form-item>
      <el-form-item label="开立日期"><el-date-picker v-model="query.date" type="date" value-format="yyyy-MM-dd" placeholder="全部日期" /></el-form-item>
      <el-form-item label="处方状态"><el-select v-model="query.drugState" clearable><el-option v-for="(label,state) in states" :key="state" :value="state" :label="label" /></el-select></el-form-item>
      <el-form-item v-if="!returning" label="缴费"><el-select v-model="query.paid" clearable style="width:120px"><el-option label="已缴费" :value="true" /><el-option label="未缴费" :value="false" /></el-select></el-form-item>
      <el-form-item><el-button type="primary" :loading="loading" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item>
    </el-form>
    <el-table :data="rows" v-loading="loading" border size="small">
      <el-table-column prop="id" label="处方号" width="75" /><el-table-column prop="caseNumber" label="病历号" min-width="160" /><el-table-column prop="realName" label="患者" width="85" /><el-table-column prop="doctorName" label="医生" width="85" />
      <el-table-column prop="drugName" label="药品" min-width="130" /><el-table-column prop="drugFormat" label="规格" min-width="120" /><el-table-column prop="drugNumber" label="数量" width="65" /><el-table-column prop="stockQuantity" label="可用库存" width="85" /><el-table-column prop="drugUsage" label="医嘱" min-width="160" />
      <el-table-column label="金额" width="90"><template slot-scope="{row}">¥{{ money(Number(row.unitPrice)*Number(row.drugNumber)) }}</template></el-table-column>
      <el-table-column label="缴费" width="80"><template slot-scope="{row}"><el-tag size="mini" :type="Number(row.paid) ? 'success' : 'warning'">{{ Number(row.paid) ? '已缴费' : '未缴费' }}</el-tag></template></el-table-column>
      <el-table-column label="状态" width="85"><template slot-scope="{row}">{{ states[row.drugState] }}</template></el-table-column>
      <el-table-column label="操作" width="90" fixed="right"><template slot-scope="{row}"><el-button type="text" :disabled="busy || (returning ? row.drugState!=='2' : row.drugState!=='1' || !Number(row.paid))" @click="prepare(row)">{{ returning ? '核对退药' : '核对发药' }}</el-button></template></el-table-column>
    </el-table>
    <el-pagination layout="total, prev, pager, next" :total="total" :page-size="query.size" :current-page.sync="query.page" @current-change="load" />
    <el-dialog :title="returning ? '核对并退药' : '核对并发药'" :visible.sync="visible" width="620px" :close-on-click-modal="false" :before-close="close">
      <template v-if="selected"><el-descriptions border :column="2" size="small"><el-descriptions-item label="患者">{{ selected.realName }}</el-descriptions-item><el-descriptions-item label="病历号">{{ selected.caseNumber }}</el-descriptions-item><el-descriptions-item label="处方号">{{ selected.id }}</el-descriptions-item><el-descriptions-item label="药品">{{ selected.drugName }}</el-descriptions-item><el-descriptions-item label="规格">{{ selected.drugFormat }}</el-descriptions-item><el-descriptions-item label="数量">{{ selected.drugNumber }} {{ selected.drugUnit }}</el-descriptions-item><el-descriptions-item label="医嘱">{{ selected.drugUsage }}</el-descriptions-item></el-descriptions>
      <el-form v-if="returning" label-width="90px" style="margin-top:18px"><el-form-item label="退药原因" required><el-input v-model.trim="reason" type="textarea" maxlength="255" show-word-limit placeholder="填写实际退药原因；本版仅接收可重新入库的完整退药" /></el-form-item></el-form>
      <el-checkbox v-model="checked" style="margin-top:18px">已核对患者身份、处方和实际药品</el-checkbox></template>
      <span slot="footer"><el-button :disabled="busy" @click="visible=false">取消</el-button><el-button type="primary" :loading="busy" :disabled="!checked" @click="submit">{{ returning ? '确认退药' : '确认发药' }}</el-button></span>
    </el-dialog>
  </div>
</template>
<script>
import { prescriptions,dispense,returnDrug } from '@/api/pharmacy'
export default {
  name:'PharmacyPrescriptions',data(){return {query:{keyword:'',date:null,drugState:'1',paid:true,page:1,size:20},rows:[],total:0,loading:false,busy:false,visible:false,selected:null,checked:false,reason:'',requestSeq:0,states:{1:'已开立',2:'已发药',3:'已退药',4:'已撤销'}}},
  computed:{returning(){return this.$route.name==='PharmacyReturn'}},watch:{'$route.name'(){this.reset()}},created(){this.reset()},
  methods:{money(v){return Number(v||0).toFixed(2)},reset(){this.query={keyword:'',date:null,drugState:this.returning?'2':'1',paid:this.returning?null:true,page:1,size:20};this.visible=false;this.load()},search(){this.query.page=1;this.load()},async load(){const seq=++this.requestSeq;this.loading=true;try{const d=await prescriptions(this.query);if(seq===this.requestSeq){this.rows=d.items;this.total=d.total}}catch(e){/* 请求层提示 */}finally{if(seq===this.requestSeq)this.loading=false}},prepare(row){this.selected={...row};this.reason='';this.checked=false;this.visible=true},close(done){if(!this.busy)done()},async submit(){if(this.busy||!this.checked)return;if(this.returning&&!this.reason)return this.$message.warning('请填写退药原因');this.busy=true;try{if(this.returning)await returnDrug(this.selected.id,this.reason);else await dispense(this.selected.id);this.$message.success(this.returning?'退药成功，请到窗口办理退费':'发药成功');this.visible=false;await this.load()}catch(e){/* 请求层提示，保留核对内容 */}finally{this.busy=false}}}
}
</script>
<style scoped>h3{margin-top:0}.search-bar{margin-top:18px}.el-pagination{margin-top:16px}</style>
