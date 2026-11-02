import service from './request'

// 业务数据统计总览（指标 + 科室分布 + 收费项目分布）
export function statisticsOverview(params) {
  return service.get('/statistics/overview', { params })
}
