<template>
  <div class="page-card">
    <div class="section-title">业务数据统计</div>

    <!-- 筛选条件 -->
    <div class="filter-bar">
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        value-format="yyyy-MM-dd"
        style="width: 260px"
      />
      <el-select
        v-model="query.departmentId"
        placeholder="全部科室"
        clearable
        style="width: 180px"
      >
        <el-option v-for="d in deptList" :key="d.id" :label="d.deptName" :value="d.id" />
      </el-select>
      <el-button type="primary" icon="el-icon-search" @click="loadData">查询</el-button>
      <el-button icon="el-icon-refresh" @click="reset">重置</el-button>
    </div>

    <!-- 指标卡 -->
    <div class="stat-cards">
      <div v-for="card in cards" :key="card.label" class="stat-card">
        <div class="stat-value" :style="{ color: card.color }">{{ card.value }}</div>
        <div class="stat-label">{{ card.label }}</div>
      </div>
    </div>

    <!-- 图表 -->
    <div class="charts">
      <div class="chart-box">
        <div class="chart-title">各科室就诊人次</div>
        <div ref="deptChart" class="chart" />
      </div>
      <div class="chart-box">
        <div class="chart-title">收费项目金额分布</div>
        <div ref="itemChart" class="chart" />
      </div>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { statisticsOverview } from '@/api/statistics'
import { departmentList } from '@/api/base'

export default {
  name: 'Statistics',
  data() {
    return {
      dateRange: [],
      deptList: [],
      query: { departmentId: null },
      overview: {},
      deptChart: null,
      itemChart: null
    }
  },
  computed: {
    cards() {
      const o = this.overview
      return [
        { label: '门诊人次', value: o.outPatientCount || 0, color: '#409EFF' },
        { label: '挂号量', value: o.registerCount || 0, color: '#409EFF' },
        { label: '退号量', value: o.refundCount || 0, color: '#F56C6C' },
        { label: '收费金额(元)', value: this.money(o.chargeAmount), color: '#67C23A' },
        { label: '退费金额(元)', value: this.money(o.refundAmount), color: '#F56C6C' },
        { label: '检查例数', value: o.checkCount || 0, color: '#909399' },
        { label: '检验例数', value: o.inspectionCount || 0, color: '#909399' },
        { label: '处置例数', value: o.disposalCount || 0, color: '#909399' },
        { label: '处方量', value: o.prescriptionCount || 0, color: '#909399' },
        { label: '发药量', value: o.dispenseCount || 0, color: '#909399' }
      ]
    }
  },
  mounted() {
    this.loadDepartments()
    this.loadData()
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCharts)
    this.disposeCharts()
  },
  methods: {
    money(v) {
      const n = Number(v)
      return isNaN(n) ? '0.00' : n.toFixed(2)
    },
    loadDepartments() {
      departmentList().then(list => {
        this.deptList = list || []
      })
    },
    reset() {
      this.dateRange = []
      this.query.departmentId = null
      this.loadData()
    },
    loadData() {
      const params = {
        startDate: (this.dateRange && this.dateRange[0]) || '',
        endDate: (this.dateRange && this.dateRange[1]) || '',
        departmentId: this.query.departmentId || null
      }
      statisticsOverview(params).then(data => {
        this.overview = data || {}
        this.$nextTick(() => this.renderCharts())
      })
    },
    renderCharts() {
      this.renderDeptChart()
      this.renderItemChart()
    },
    renderDeptChart() {
      if (!this.deptChart) {
        this.deptChart = echarts.init(this.$refs.deptChart)
      }
      const list = this.overview.deptDistribution || []
      this.deptChart.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: 40, right: 20, top: 20, bottom: 40 },
        xAxis: {
          type: 'category',
          data: list.map(i => i.deptName),
          axisLabel: { rotate: 30 }
        },
        yAxis: { type: 'value', minInterval: 1 },
        series: [
          {
            type: 'bar',
            data: list.map(i => i.count),
            barWidth: 24,
            itemStyle: { color: '#409EFF' }
          }
        ]
      })
    },
    renderItemChart() {
      if (!this.itemChart) {
        this.itemChart = echarts.init(this.$refs.itemChart)
      }
      const list = this.overview.itemFeeDistribution || []
      this.itemChart.setOption({
        tooltip: { trigger: 'item', formatter: '{b}: ¥{c} ({d}%)' },
        legend: { bottom: 0 },
        series: [
          {
            type: 'pie',
            radius: ['40%', '65%'],
            center: ['50%', '45%'],
            data: list.map(i => ({ name: i.itemType, value: Number(i.amount) })),
            label: { formatter: '{b}\n¥{c}' }
          }
        ]
      })
    },
    resizeCharts() {
      if (this.deptChart) this.deptChart.resize()
      if (this.itemChart) this.itemChart.resize()
    },
    disposeCharts() {
      if (this.deptChart) {
        this.deptChart.dispose()
        this.deptChart = null
      }
      if (this.itemChart) {
        this.itemChart.dispose()
        this.itemChart = null
      }
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
  margin-bottom: 20px;
}
.stat-cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 14px;
  margin-bottom: 20px;
}
.stat-card {
  background: #f5f7fa;
  border-radius: 6px;
  padding: 16px;
  text-align: center;
}
.stat-value {
  font-size: 22px;
  font-weight: 600;
}
.stat-label {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}
.charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.chart-box {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  padding: 12px;
}
.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}
.chart {
  height: 320px;
}
</style>
