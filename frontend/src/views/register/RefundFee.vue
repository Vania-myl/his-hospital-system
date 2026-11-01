<template>
  <div class="page-card">
    <el-form :inline="true" size="small" class="search-bar">
      <el-form-item label="病历号">
        <el-input v-model="caseNumber" placeholder="输入病历号" clearable style="width:200px"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="list" border stripe size="small">
      <el-table-column prop="caseNumber" label="病历号" width="180"></el-table-column>
      <el-table-column prop="realName" label="姓名" width="90"></el-table-column>
      <el-table-column prop="itemName" label="项目名称"></el-table-column>
      <el-table-column label="单价" width="100">
        <template slot-scope="{ row }">¥{{ Number(row.unitPrice).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="70"></el-table-column>
      <el-table-column label="开立科室" width="110">
        <template slot-scope="{ row }">{{ row.deptName || '—' }}</template>
      </el-table-column>
      <el-table-column label="状态" width="110">
        <template slot-scope="{ row }">
          <el-tag :type="row.itemState === '1' ? 'success' : 'info'" size="mini">{{ stateText(row.itemState) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="金额" width="100">
        <template slot-scope="{ row }">¥{{ Number(row.feeAmount).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="90" fixed="right">
        <template slot-scope="{ row }">
          <el-button type="text" size="small" :disabled="row.itemState !== '1'" @click="handleRefund(row)">退费</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { feeRefundable, feeRefund } from '@/api/register'

export default {
  name: 'RefundFee',
  data() {
    return {
      caseNumber: '',
      list: [],
      loading: false
    }
  },
  methods: {
    handleSearch() {
      if (!this.caseNumber) {
        this.$message.warning('请输入病历号')
        return
      }
      this.loading = true
      feeRefundable(this.caseNumber)
        .then(list => {
          this.list = list || []
          if (!this.list.length) this.$message.info('该患者暂无已收费项目')
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleReset() {
      this.caseNumber = ''
      this.list = []
    },
    stateText(s) {
      return { 1: '已开立', 2: '已执行', 3: '已完成', 4: '已撤销' }[s] || '未知'
    },
    handleRefund(row) {
      this.$confirm(`确认对「${row.itemName}」退费 ¥${Number(row.feeAmount).toFixed(2)}？`, '退费确认', { type: 'warning' })
        .then(() => feeRefund(row.id))
        .then(() => {
          this.$message.success('退费成功')
          return feeRefundable(this.caseNumber)
        })
        .then(list => {
          this.list = list || []
        })
        .catch(() => {})
    }
  }
}
</script>
