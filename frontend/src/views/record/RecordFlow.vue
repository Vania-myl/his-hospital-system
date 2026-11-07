<template>
  <div class="page-card">
    <div class="section-title">就诊全流程记录</div>

    <!-- 病历号查询 -->
    <div class="filter-bar">
      <el-input
        v-model="caseNumber"
        placeholder="请输入病历号"
        clearable
        style="width: 220px"
        @keyup.enter.native="search"
      />
      <el-button type="primary" icon="el-icon-search" @click="search">查询</el-button>
    </div>

    <el-empty
      v-if="!flow"
      description="请输入病历号查询，或从「历史业务查询」点击全流程进入"
    />

    <template v-else>
      <!-- 挂号信息 -->
      <div class="flow-section">
        <div class="flow-title">挂号信息</div>
        <div class="info-grid">
          <div class="info-item"><span class="info-label">病历号</span>{{ flow.caseNumber }}</div>
          <div class="info-item"><span class="info-label">姓名</span>{{ flow.realName }}</div>
          <div class="info-item"><span class="info-label">性别</span>{{ flow.gender }}</div>
          <div class="info-item"><span class="info-label">年龄</span>{{ flow.age }}</div>
          <div class="info-item"><span class="info-label">就诊卡号</span>{{ flow.cardNumber || '-' }}</div>
          <div class="info-item"><span class="info-label">科室</span>{{ flow.deptName }}</div>
          <div class="info-item"><span class="info-label">号别</span>{{ flow.registLevelName }}</div>
          <div class="info-item"><span class="info-label">就诊时间</span>{{ flow.visitDate }}</div>
          <div class="info-item"><span class="info-label">午别</span>{{ flow.noon }}</div>
          <div class="info-item"><span class="info-label">状态</span>{{ visitStateText(flow.visitState) }}</div>
          <div class="info-item"><span class="info-label">挂号费</span>¥{{ money(flow.registMoney) }}</div>
          <div class="info-item"><span class="info-label">收费合计</span>¥{{ money(flow.chargeAmount) }}</div>
        </div>
      </div>

      <!-- 医生接诊 -->
      <div class="flow-section">
        <div class="flow-title">医生接诊（病历）</div>
        <template v-if="flow.medicalRecord">
          <div class="mr-line"><span class="info-label">主诉</span>{{ flow.medicalRecord.readme || '-' }}</div>
          <div class="mr-line"><span class="info-label">现病史</span>{{ flow.medicalRecord.present || '-' }}</div>
          <div class="mr-line"><span class="info-label">现病治疗情况</span>{{ flow.medicalRecord.presentTreat || '-' }}</div>
          <div class="mr-line"><span class="info-label">既往史</span>{{ flow.medicalRecord.history || '-' }}</div>
          <div class="mr-line"><span class="info-label">过敏史</span>{{ flow.medicalRecord.allergy || '-' }}</div>
          <div class="mr-line"><span class="info-label">体格检查</span>{{ flow.medicalRecord.physique || '-' }}</div>
          <div class="mr-line"><span class="info-label">诊断结果</span>{{ flow.medicalRecord.diagnosis || '-' }}</div>
          <div class="mr-line"><span class="info-label">处理意见</span>{{ flow.medicalRecord.cure || '-' }}</div>
          <div class="mr-line">
            <span class="info-label">疾病</span>{{ (flow.diseases && flow.diseases.length) ? flow.diseases.join('、') : '-' }}
          </div>
        </template>
        <el-empty v-else description="暂无病历记录" :image-size="60" />
      </div>

      <!-- 医技项目 -->
      <div class="flow-section">
        <div class="flow-title">检查 / 检验 / 处置</div>
        <el-table v-if="flow.items && flow.items.length" :data="flow.items" size="small" border>
          <el-table-column prop="itemType" label="类型" width="80" />
          <el-table-column prop="techName" label="项目名称" min-width="150" />
          <el-table-column label="状态" width="90">
            <template slot-scope="scope">{{ itemStateText(scope.row.state) }}</template>
          </el-table-column>
          <el-table-column prop="creationTime" label="开立时间" width="150" />
          <el-table-column prop="result" label="结果" min-width="140" />
        </el-table>
        <el-empty v-else description="暂无医技项目" :image-size="60" />
      </div>

      <!-- 处方 -->
      <div class="flow-section">
        <div class="flow-title">处方 / 发药</div>
        <el-table v-if="flow.prescriptions && flow.prescriptions.length" :data="flow.prescriptions" size="small" border>
          <el-table-column prop="drugName" label="药品名称" min-width="140" />
          <el-table-column prop="drugFormat" label="规格" width="130" />
          <el-table-column prop="drugUsage" label="用法用量" min-width="140" />
          <el-table-column prop="drugNumber" label="数量" width="80" />
          <el-table-column label="单价" width="90" align="right">
            <template slot-scope="scope">¥{{ money(scope.row.drugPrice) }}</template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template slot-scope="scope">{{ drugStateText(scope.row.state) }}</template>
          </el-table-column>
          <el-table-column prop="creationTime" label="开立时间" width="150" />
        </el-table>
        <el-empty v-else description="暂无处方" :image-size="60" />
      </div>

      <!-- 费用流水 -->
      <div class="flow-section">
        <div class="flow-title">费用流水</div>
        <el-table v-if="flow.fees && flow.fees.length" :data="flow.fees" size="small" border>
          <el-table-column prop="itemType" label="项目类型" width="90" />
          <el-table-column prop="itemName" label="项目名称" min-width="150" />
          <el-table-column label="收费类型" width="90">
            <template slot-scope="scope">
              <el-tag :type="scope.row.feeType === '退费' ? 'danger' : 'success'" size="mini">
                {{ scope.row.feeType }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="金额" width="100" align="right">
            <template slot-scope="scope">¥{{ money(scope.row.feeAmount) }}</template>
          </el-table-column>
          <el-table-column prop="createTime" label="时间" width="170" />
        </el-table>
        <el-empty v-else description="暂无费用记录" :image-size="60" />
      </div>
    </template>
  </div>
</template>

<script>
import { recordFlow } from '@/api/record'

export default {
  name: 'RecordFlow',
  data() {
    return {
      caseNumber: '',
      flow: null
    }
  },
  mounted() {
    const q = this.$route.query
    if (q.registerId) {
      this.loadByRegisterId(q.registerId)
    } else if (q.caseNumber) {
      this.caseNumber = q.caseNumber
      this.loadByCaseNumber(q.caseNumber)
    }
  },
  methods: {
    money(v) {
      const n = Number(v)
      return isNaN(n) ? '0.00' : n.toFixed(2)
    },
    visitStateText(s) {
      return { 1: '已挂号', 2: '医生接诊', 3: '看诊结束', 4: '已退号' }[s] || '未知'
    },
    itemStateText(s) {
      return { 1: '已开立', 2: '已执行', 3: '已完成', 4: '已撤销' }[s] || s || '-'
    },
    drugStateText(s) {
      return { 1: '已开立', 2: '已发药', 3: '已退药', 4: '已撤销' }[s] || s || '-'
    },
    search() {
      if (!this.caseNumber) {
        this.$message.warning('请输入病历号')
        return
      }
      this.loadByCaseNumber(this.caseNumber)
    },
    loadByRegisterId(registerId) {
      recordFlow({ registerId }).then(data => {
        this.flow = data
      }).catch(() => {})
    },
    loadByCaseNumber(caseNumber) {
      recordFlow({ caseNumber }).then(data => {
        this.flow = data
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.filter-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}
.flow-section {
  margin-bottom: 20px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 14px 16px;
}
.flow-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid #409eff;
}
.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px 24px;
}
.info-item {
  font-size: 14px;
  color: #303133;
}
.info-label {
  display: inline-block;
  min-width: 72px;
  color: #909399;
  margin-right: 6px;
}
.mr-line {
  font-size: 14px;
  color: #303133;
  line-height: 1.9;
}
.mr-line .info-label {
  min-width: 100px;
}
</style>
