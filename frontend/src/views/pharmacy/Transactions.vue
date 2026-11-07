<template>
  <div class="page-card">
    <h3>药品交易记录</h3>
    <el-alert title="这里展示药品库存流水，发退药与资金收退费分别记录。资金退费由窗口办理。" type="info" :closable="false" />
    <el-form inline size="small" class="search-bar" @submit.native.prevent="search"><el-form-item label="检索"><el-input v-model.trim="query.keyword" clearable placeholder="病历号 姓名 处方号 药品" @keyup.enter.native="search" /></el-form-item><el-form-item label="类型"><el-select v-model="query.type" clearable><el-option v-for="t in ['发药','退药','入库','报损','盘点']" :key="t" :value="t" :label="t" /></el-select></el-form-item><el-form-item label="日期"><el-date-picker v-model="query.date" value-format="yyyy-MM-dd" type="date" placeholder="全部日期" /></el-form-item><el-form-item><el-button type="primary" :loading="loading" @click="search">查询</el-button><el-button @click="reset">重置</el-button></el-form-item></el-form>
    <el-table :data="rows" v-loading="loading" border size="small"><el-table-column prop="id" label="流水号" width="80" /><el-table-column prop="prescriptionId" label="处方号" width="80" /><el-table-column prop="caseNumber" label="病历号" min-width="165" /><el-table-column prop="realName" label="患者" width="90" /><el-table-column prop="drugName" label="药品" min-width="130" /><el-table-column prop="type" label="类型" width="75" /><el-table-column label="库存变化" width="90"><template slot-scope="{row}">{{ row.quantity>0?'+':'' }}{{ row.quantity }}</template></el-table-column><el-table-column prop="stockAfter" label="变动后库存" width="105" /><el-table-column prop="reason" label="原因" min-width="160" /><el-table-column prop="operatorName" label="操作人" width="100" /><el-table-column prop="createTime" label="时间" width="175" /></el-table>
    <el-pagination layout="total, prev, pager, next" :total="total" :page-size="query.size" :current-page.sync="query.page" @current-change="load" />
  </div>
</template>
<script>
import {transactions} from '@/api/pharmacy'
function today(){const d=new Date();return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`}
export default {name:'PharmacyTransactions',data(){return {query:{keyword:'',type:'',date:today(),page:1,size:20},rows:[],total:0,loading:false}},created(){this.load()},methods:{search(){this.query.page=1;this.load()},reset(){this.query={keyword:'',type:'',date:today(),page:1,size:20};this.load()},async load(){this.loading=true;try{const d=await transactions({...this.query,date:this.query.date||''});this.rows=d.items;this.total=d.total}catch(e){/* 请求层提示 */}finally{this.loading=false}}}}
</script>
<style scoped>h3{margin-top:0}.search-bar{margin-top:18px}.el-pagination{margin-top:16px}</style>
