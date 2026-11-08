<template>
  <div class="page-card">
    <div class="section-title">历史业务查询</div>

    <!-- 查询条件 -->
    <div class="filter-bar">
      <el-input
        v-model="query.caseNumber"
        placeholder="病历号"
        clearable
        style="width: 160px"
        @keyup.enter.native="search"
      />
      <el-input
        v-model="query.realName"
        placeholder="患者姓名"
        clearable
        style="width: 160px"
        @keyup.enter.native="search"
      />
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd"
        style="width: 260px"
      />
      <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
      <el-button icon="el-icon-refresh" @click="reset">重置</el-button>
    </div>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="list" border stripe size="medium">
      <el-table-column prop="caseNumber" label="病历号" width="130" />
      <el-table-column prop="realName" label="姓名" width="90" />
      <el-table-column prop="gender" label="性别" width="60" />
      <el-table-column prop="age" label="年龄" width="60" />
      <el-table-column prop="deptName" label="科室" min-width="110" />
      <el-table-column prop="registLevelName" label="号别" width="90" />
      <el-table-column prop="visitDate" label="就诊时间" width="150" />
      <el-table-column prop="noon" label="午别" width="70" />
      <el-table-column label="状态" width="95">
        <template slot-scope="scope">
          <el-tag :type="visitStateType(scope.row.visitState)" size="small">
            {{ visitStateText(scope.row.visitState) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="收费合计" width="110" align="right">
        <template slot-scope="scope">¥{{ money(scope.row.chargeAmount) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template slot-scope="scope">
          <el-button type="text" size="small" @click="viewFlow(scope.row)">全流程</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pager">
      <el-pagination
        background
        layout="total, sizes, prev, pager, next"
        :total="total"
        :page-size="size"
        :current-page="page"
        :page-sizes="[10, 20, 50]"
        @current-change="onPageChange"
        @size-change="onSizeChange"
      />
    </div>
  </div>
</template>

<script>
import { recordHistory } from '@/api/record'

export default {
  name: 'RecordHistory',
  data() {
    return {
      query: { caseNumber: '', realName: '' },
      dateRange: [],
      page: 1,
      size: 10,
      list: [],
      total: 0,
      loading: false
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    money(v) {
      const n = Number(v)
      return isNaN(n) ? '0.00' : n.toFixed(2)
    },
    visitStateText(s) {
      return { 1: '已挂号', 2: '医生接诊', 3: '看诊结束', 4: '已退号' }[s] || '未知'
    },
    visitStateType(s) {
      return { 1: 'info', 2: 'warning', 3: 'success', 4: 'danger' }[s] || 'info'
    },
    viewFlow(row) {
      this.$router.push({ path: '/record/flow', query: { registerId: row.registerId } })
    },
    loadData() {
      this.loading = true
      const params = {
        caseNumber: this.query.caseNumber || '',
        realName: this.query.realName || '',
        startDate: (this.dateRange && this.dateRange[0]) || '',
        endDate: (this.dateRange && this.dateRange[1]) || '',
        offset: (this.page - 1) * this.size,
        size: this.size
      }
      recordHistory(params)
        .then(data => {
          this.list = (data && data.list) || []
          this.total = (data && data.total) || 0
        })
        .finally(() => {
          this.loading = false
        })
    },
    search() {
      this.page = 1
      this.loadData()
    },
    reset() {
      this.query = { caseNumber: '', realName: '' }
      this.dateRange = []
      this.page = 1
      this.loadData()
    },
    onPageChange(p) {
      this.page = p
      this.loadData()
    },
    onSizeChange(s) {
      this.size = s
      this.page = 1
      this.loadData()
    }
  }
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 16px;
}
.pager {
  margin-top: 16px;
  text-align: right;
}
</style>
