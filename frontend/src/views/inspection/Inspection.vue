<template>
  <div class="page-card">
    <div class="page-title">检验管理</div>
    <el-form :inline="true" :model="query" size="small" class="search-bar" @submit.native.prevent="search">
      <el-form-item label="病历号">
        <el-input v-model.trim="query.caseNumber" clearable placeholder="精确查询" style="width:150px" />
      </el-form-item>
      <el-form-item label="患者姓名">
        <el-input v-model.trim="query.realName" clearable placeholder="模糊查询" style="width:130px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.inspectionState" clearable placeholder="全部" style="width:130px">
          <el-option v-for="(name, value) in states" :key="value" :label="name" :value="value" />
        </el-select>
      </el-form-item>
      <el-form-item label="开立日期">
        <el-date-picker v-model="query.creationDate" type="date" value-format="yyyy-MM-dd" clearable placeholder="选择日期" style="width:150px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
        <el-button icon="el-icon-refresh" @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="rows" border stripe size="small">
      <el-table-column prop="caseNumber" label="病历号" min-width="150" />
      <el-table-column prop="realName" label="患者" min-width="90" />
      <el-table-column prop="techName" label="检验项目" min-width="120" />
      <el-table-column prop="inspectionPosition" label="检验部位" min-width="110" show-overflow-tooltip />
      <el-table-column prop="creationTime" label="开立时间" min-width="165" />
      <el-table-column label="状态" width="95">
        <template slot-scope="{ row }">
          <el-tag :type="stateType(row.inspectionState)" size="mini">{{ states[row.inspectionState] || row.inspectionState }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="215" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="showDetails(row)">查看</el-button>
          <el-button v-if="row.inspectionState === '1'" type="text" size="small" :disabled="row.paymentReady !== 1" @click="assign(row)">登记检验</el-button>
          <span v-if="row.inspectionState === '1' && row.paymentReady !== 1" class="unpaid">待收费</span>
          <el-button v-if="row.inspectionState === '2'" type="text" size="small" @click="openResult(row)">录入结果</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pager" background layout="total, sizes, prev, pager, next"
      :total="total" :current-page="page" :page-size="size" :page-sizes="[10, 20, 50]"
      @current-change="changePage" @size-change="changeSize" />

    <el-dialog title="检验申请详情" :visible.sync="detailsVisible" width="650px">
      <el-descriptions v-if="current.id" :column="2" border size="small">
        <el-descriptions-item label="病历号">{{ current.caseNumber }}</el-descriptions-item>
        <el-descriptions-item label="患者">{{ current.realName }}</el-descriptions-item>
        <el-descriptions-item label="检验项目">{{ current.techName }}</el-descriptions-item>
        <el-descriptions-item label="检验部位">{{ current.inspectionPosition || '—' }}</el-descriptions-item>
        <el-descriptions-item label="检验要求" :span="2">{{ current.inspectionInfo || '—' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ states[current.inspectionState] || current.inspectionState }}</el-descriptions-item>
        <el-descriptions-item label="收费状态">{{ current.paymentReady === 1 ? '已收费' : '未收费/已退费' }}</el-descriptions-item>
        <el-descriptions-item label="开立时间">{{ current.creationTime }}</el-descriptions-item>
        <el-descriptions-item label="检验人员">{{ current.inspectionEmployeeName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="检验时间">{{ current.inspectionTime || '—' }}</el-descriptions-item>
        <el-descriptions-item label="结果录入人">{{ current.inputinspectionEmployeeName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="结果">{{ current.inspectionResult || '—' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ current.inspectionRemark || '—' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog title="录入检验结果" :visible.sync="resultVisible" width="560px" @closed="clearResult">
      <el-form ref="resultForm" :model="resultForm" :rules="rules" label-width="90px">
        <el-form-item label="检验结果" prop="inspectionResult">
          <el-input v-model="resultForm.inspectionResult" type="textarea" :rows="5" maxlength="512" show-word-limit />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="resultForm.inspectionRemark" type="textarea" :rows="3" maxlength="512" show-word-limit />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="resultVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitResult">保存结果</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { inspectionRequestList, assignInspectionRequest, saveInspectionResult } from '@/api/inspection'

const emptyQuery = () => ({ caseNumber: '', realName: '', inspectionState: '', creationDate: '' })

export default {
  name: 'Inspection',
  data() {
    return {
      query: emptyQuery(),
      states: { 1: '已开立', 2: '已执行', 3: '已完成', 4: '已撤销' },
      rows: [], total: 0, page: 1, size: 10, loading: false, saving: false,
      detailsVisible: false, resultVisible: false, current: {},
      resultForm: { inspectionResult: '', inspectionRemark: '' },
      rules: { inspectionResult: [{ required: true, whitespace: true, message: '请输入检验结果', trigger: 'blur' }] }
    }
  },
  created() { this.fetchData() },
  methods: {
    fetchData() {
      this.loading = true
      inspectionRequestList({ ...this.query, page: this.page, size: this.size })
        .then(data => {
          this.rows = data.records || []
          this.total = data.total || 0
        })
        .catch(() => {})
        .finally(() => { this.loading = false })
    },
    search() { this.page = 1; this.fetchData() },
    reset() { this.query = emptyQuery(); this.search() },
    changePage(page) { this.page = page; this.fetchData() },
    changeSize(size) { this.size = size; this.page = 1; this.fetchData() },
    stateType(state) { return { 1: '', 2: 'warning', 3: 'success', 4: 'info' }[state] || 'info' },
    showDetails(row) { this.current = row; this.detailsVisible = true },
    assign(row) {
      this.$confirm(`登记 ${row.realName} 的「${row.techName}」检验？`, '登记检验', { type: 'warning' })
        .then(() => assignInspectionRequest(row.id))
        .then(() => { this.$message.success('登记成功'); this.fetchData() })
        .catch(() => {})
    },
    openResult(row) {
      this.current = row
      this.resultForm = { inspectionResult: row.inspectionResult || '', inspectionRemark: row.inspectionRemark || '' }
      this.resultVisible = true
    },
    clearResult() {
      this.resultForm = { inspectionResult: '', inspectionRemark: '' }
      if (this.$refs.resultForm) this.$refs.resultForm.clearValidate()
    },
    submitResult() {
      this.$refs.resultForm.validate(valid => {
        if (!valid || this.saving) return
        this.saving = true
        saveInspectionResult(this.current.id, this.resultForm)
          .then(() => {
            this.$message.success('检验结果已保存')
            this.resultVisible = false
            this.fetchData()
          })
          .catch(() => {})
          .finally(() => { this.saving = false })
      })
    }
  }
}
</script>
<style scoped>
.unpaid { margin-left: 6px; color: #e6a23c; font-size: 12px; }
</style>
