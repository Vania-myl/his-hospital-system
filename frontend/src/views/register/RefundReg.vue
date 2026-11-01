<template>
  <div class="page-card">
    <el-form :inline="true" :model="query" size="small" class="search-bar">
      <el-form-item label="病历号">
        <el-input v-model="query.caseNumber" placeholder="精确" clearable style="width:150px"></el-input>
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="query.realName" placeholder="模糊" clearable style="width:120px"></el-input>
      </el-form-item>
      <el-form-item label="看诊状态">
        <el-select v-model="query.visitState" placeholder="全部" clearable style="width:130px">
          <el-option v-for="(t, k) in stateMap" :key="k" :label="t" :value="Number(k)"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="挂号日期">
        <el-date-picker v-model="query.visitDate" type="date" value-format="yyyy-MM-dd" placeholder="选择日期" style="width:150px"></el-date-picker>
      </el-form-item>
      <el-form-item label="看诊科室">
        <el-select v-model="query.departmentId" placeholder="全部" clearable style="width:150px">
          <el-option v-for="d in departments" :key="d.id" :label="d.deptName" :value="d.id"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="toolbar">
      <el-button type="danger" size="small" :disabled="!selection.length" @click="handleBatchRefund">
        批量退号（{{ selection.length }}）
      </el-button>
    </div>

    <el-table :data="list" border stripe size="small" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45"></el-table-column>
      <el-table-column prop="caseNumber" label="病历号" width="180"></el-table-column>
      <el-table-column prop="realName" label="姓名" width="90"></el-table-column>
      <el-table-column label="看诊医生" width="100">
        <template slot-scope="{ row }">{{ doctorName(row.employeeId) }}</template>
      </el-table-column>
      <el-table-column label="看诊状态" width="100">
        <template slot-scope="{ row }">
          <el-tag :type="stateType(row.visitState)" size="mini">{{ stateText(row.visitState) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="visitDate" label="挂号日期" width="160"></el-table-column>
      <el-table-column label="看诊科室" width="120">
        <template slot-scope="{ row }">{{ deptName(row.departmentId) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" size="small" @click="handleView(row)">查看</el-button>
          <el-button type="text" size="small" :disabled="row.visitState !== 1" @click="handleRefund(row)">退号</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pager"
      background
      layout="total, sizes, prev, pager, next"
      :total="total"
      :page-size="size"
      :current-page="page"
      :page-sizes="[10, 20, 50]"
      @current-change="onPageChange"
      @size-change="onSizeChange"
    ></el-pagination>

    <el-dialog :title="'挂号详情 · ' + current.caseNumber" :visible.sync="dialogVisible" width="560px">
      <el-descriptions :column="2" border size="small" v-if="current.id">
        <el-descriptions-item label="姓名">{{ current.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ current.gender }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ current.cardNumber }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ current.age }}</el-descriptions-item>
        <el-descriptions-item label="看诊日期">{{ current.visitDate }}</el-descriptions-item>
        <el-descriptions-item label="午别">{{ current.noon }}</el-descriptions-item>
        <el-descriptions-item label="挂号科室">{{ deptName(current.departmentId) }}</el-descriptions-item>
        <el-descriptions-item label="接诊医生">{{ doctorName(current.employeeId) }}</el-descriptions-item>
        <el-descriptions-item label="结算类别">{{ categoryName(current.settleCategoryId) }}</el-descriptions-item>
        <el-descriptions-item label="号别">{{ levelName(current.registLevelId) }}</el-descriptions-item>
        <el-descriptions-item label="挂号金额">¥{{ current.registMoney }}</el-descriptions-item>
        <el-descriptions-item label="收费方式">{{ current.registMethod }}</el-descriptions-item>
        <el-descriptions-item label="状态" :span="2">
          <el-tag :type="stateType(current.visitState)" size="mini">{{ stateText(current.visitState) }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { registerList, registerCount, registerRefund } from '@/api/register'
import { departmentList, employeeList, settleCategoryList, registLevelList } from '@/api/base'

export default {
  name: 'RefundReg',
  data() {
    return {
      query: { caseNumber: '', realName: '', visitState: null, visitDate: '', departmentId: null },
      list: [],
      total: 0,
      page: 1,
      size: 10,
      selection: [],
      departments: [],
      empMap: {},
      deptMap: {},
      categoryMap: {},
      levelMap: {},
      dialogVisible: false,
      current: {},
      stateMap: { 1: '已挂号', 2: '医生接诊', 3: '看诊结束', 4: '已退号' }
    }
  },
  created() {
    Promise.all([departmentList(), employeeList(), settleCategoryList(), registLevelList()])
      .then(([depts, emps, cats, levels]) => {
        this.departments = depts
        emps.forEach(e => { this.empMap[e.id] = e.realname })
        depts.forEach(d => { this.deptMap[d.id] = d.deptName })
        cats.forEach(c => { this.categoryMap[c.id] = c.settleName })
        levels.forEach(l => { this.levelMap[l.id] = l.registName })
      })
      .catch(() => {})
    this.fetchData()
  },
  methods: {
    fetchData() {
      registerList({ ...this.query, page: this.page, size: this.size })
        .then(rows => {
          this.list = rows || []
          return registerCount(this.query)
        })
        .then(total => {
          this.total = total || 0
        })
        .catch(() => {})
    },
    handleSearch() {
      this.page = 1
      this.fetchData()
    },
    handleReset() {
      this.query = { caseNumber: '', realName: '', visitState: null, visitDate: '', departmentId: null }
      this.page = 1
      this.fetchData()
    },
    onPageChange(p) {
      this.page = p
      this.fetchData()
    },
    onSizeChange(s) {
      this.size = s
      this.page = 1
      this.fetchData()
    },
    onSelectionChange(s) {
      this.selection = s
    },
    doctorName(id) { return this.empMap[id] || '' },
    deptName(id) { return this.deptMap[id] || '' },
    categoryName(id) { return this.categoryMap[id] || '' },
    levelName(id) { return this.levelMap[id] || '' },
    stateText(s) { return this.stateMap[s] || s },
    stateType(s) { return { 1: 'success', 2: 'warning', 3: 'info', 4: 'danger' }[s] || 'info' },
    handleView(row) {
      this.current = row
      this.dialogVisible = true
    },
    handleRefund(row) {
      this.$confirm(`确定退号吗？患者：${row.realName}（${row.caseNumber}）`, '退号确认', { type: 'warning' })
        .then(() => registerRefund(row.id))
        .then(() => {
          this.$message.success('退号成功')
          this.fetchData()
        })
        .catch(() => {})
    },
    handleBatchRefund() {
      const ids = this.selection.map(r => r.id)
      this.$confirm(`确定对选中的 ${ids.length} 条记录批量退号吗？`, '批量退号', { type: 'warning' })
        .then(() => Promise.all(ids.map(id => registerRefund(id))))
        .then(() => {
          this.$message.success('批量退号完成')
          this.fetchData()
        })
        .catch(() => {})
    }
  }
}
</script>
