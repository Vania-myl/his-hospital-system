import service from './request'

// 历史业务查询（分页）
export function recordHistory(params) {
  return service.get('/record/history', { params })
}

// 就诊全流程记录（registerId 或 caseNumber 二选一）
export function recordFlow(params) {
  return service.get('/record/flow', { params })
}
