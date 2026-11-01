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

    <div v-if="info.caseNumber" class="patient-panel">
      <el-descriptions :column="5" border size="small">
        <el-descriptions-item label="姓名">{{ info.realName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ info.cardNumber }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ info.age }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ info.gender }}</el-descriptions-item>
        <el-descriptions-item label="累计收费 / 退费">
          <b class="money">¥{{ chargeTotalText }}</b> / ¥{{ refundTotalText }}
        </el-descriptions-item>
      </el-descriptions>
    </div>

    <el-table :data="records" border stripe size="small">
      <el-table-column prop="itemName" label="项目名称"></el-table-column>
      <el-table-column label="单价" width="100">
        <template slot-scope="{ row }">{{ row.unitPrice != null ? '¥' + Number(row.unitPrice).toFixed(2) : '—' }}</template>
      </el-table-column>
      <el-table-column prop="itemType" label="类型" width="90"></el-table-column>
      <el-table-column label="规格" width="120">
        <template slot-scope="{ row }">{{ row.itemFormat || '—' }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="70"></el-table-column>
      <el-table-column prop="createTime" label="开立/收费时间" width="170"></el-table-column>
      <el-table-column label="金额" width="100">
        <template slot-scope="{ row }">¥{{ Number(row.feeAmount).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90">
        <template slot-scope="{ row }">
          <el-tag :type="row.feeType === '收费' ? 'success' : 'danger'" size="mini">{{ row.feeType }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { feeList } from '@/api/register'

export default {
  name: 'FeeQuery',
  data() {
    return {
      caseNumber: '',
      info: {},
      records: [],
      loading: false
    }
  },
  computed: {
    chargeTotalText() {
      return (this.info.chargeTotal != null ? Number(this.info.chargeTotal) : 0).toFixed(2)
    },
    refundTotalText() {
      return (this.info.refundTotal != null ? Number(this.info.refundTotal) : 0).toFixed(2)
    }
  },
  methods: {
    handleSearch() {
      if (!this.caseNumber) {
        this.$message.warning('请输入病历号')
        return
      }
      this.loading = true
      feeList(this.caseNumber)
        .then(data => {
          this.info = data || {}
          this.records = (data && data.records) || []
          if (!this.records.length) this.$message.info('该患者暂无费用流水')
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleReset() {
      this.caseNumber = ''
      this.info = {}
      this.records = []
    }
  }
}
</script>

<style scoped>
.patient-panel {
  margin-bottom: 12px;
}
</style>
