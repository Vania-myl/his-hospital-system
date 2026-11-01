<template>
  <div class="page-card">
    <el-form :inline="true" size="small" class="search-bar">
      <el-form-item label="病历号">
        <el-input v-model="caseNumber" placeholder="输入病历号" clearable style="width:200px"></el-input>
      </el-form-item>
      <el-form-item label="收费方式">
        <el-select v-model="feeMethod" style="width:120px">
          <el-option v-for="m in methods" :key="m" :label="m" :value="m"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="patient.caseNumber" class="patient-bar">
      <span>患者：<b>{{ patient.realName }}</b></span>
      <span>性别：{{ patient.gender }}</span>
      <span>年龄：{{ patient.age }}</span>
      <span>身份证号：{{ patient.cardNumber }}</span>
    </div>

    <el-table :data="items" border stripe size="small" @selection-change="onSelectionChange">
      <el-table-column type="selection" width="45"></el-table-column>
      <el-table-column prop="itemType" label="项目类型" width="100"></el-table-column>
      <el-table-column prop="itemName" label="项目名称"></el-table-column>
      <el-table-column label="单价" width="100">
        <template slot-scope="{ row }">¥{{ Number(row.unitPrice).toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="quantity" label="数量" width="80"></el-table-column>
      <el-table-column label="金额" width="110">
        <template slot-scope="{ row }">¥{{ Number(row.amount).toFixed(2) }}</template>
      </el-table-column>
    </el-table>

    <div class="charge-footer">
      <span class="total">合计：<b class="money">¥{{ totalText }}</b>（已选 {{ selection.length }} 项）</span>
      <el-button type="primary" :disabled="!selection.length" :loading="loading" @click="handleCharge">收费结算</el-button>
    </div>
  </div>
</template>

<script>
import { chargePending, charge, registerList } from '@/api/register'

const METHODS = ['现金', '银行卡', '医保卡', '微信', '支付宝']

export default {
  name: 'Charge',
  data() {
    return {
      caseNumber: '',
      feeMethod: '现金',
      methods: METHODS,
      patient: {},
      items: [],
      selection: [],
      loading: false
    }
  },
  computed: {
    totalText() {
      const sum = this.selection.reduce((acc, r) => acc + Number(r.amount || 0), 0)
      return sum.toFixed(2)
    }
  },
  methods: {
    handleSearch() {
      if (!this.caseNumber) {
        this.$message.warning('请输入病历号')
        return
      }
      this.loading = true
      Promise.all([
        registerList({ caseNumber: this.caseNumber, page: 1, size: 1 }),
        chargePending(this.caseNumber)
      ])
        .then(([rows, items]) => {
          this.patient = rows && rows.length ? rows[0] : {}
          this.items = items || []
          this.selection = []
          if (!this.items.length) this.$message.info('该患者暂无待收费项目')
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleReset() {
      this.caseNumber = ''
      this.feeMethod = '现金'
      this.patient = {}
      this.items = []
      this.selection = []
    },
    onSelectionChange(s) {
      this.selection = s
    },
    handleCharge() {
      const items = this.selection.map(r => ({ itemType: r.itemType, itemId: r.itemId }))
      this.$confirm(`确认对选中的 ${items.length} 项收费，合计 ¥${this.totalText}？`, '收费确认', { type: 'warning' })
        .then(() => charge({ caseNumber: this.caseNumber, feeMethod: this.feeMethod, items }))
        .then(() => {
          this.$message.success('收费成功')
          return chargePending(this.caseNumber)
        })
        .then(items => {
          this.items = items || []
          this.selection = []
        })
        .catch(() => {})
    }
  }
}
</script>
