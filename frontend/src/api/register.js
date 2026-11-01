import service from './request'

// 挂号
export function register(data) {
  return service.post('/register', data)
}

// 挂号列表
export function registerList(params) {
  return service.get('/register/list', { params })
}

// 挂号总数（分页用）
export function registerCount(params) {
  return service.get('/register/count', { params })
}

// 退号
export function registerRefund(id) {
  return service.post(`/register/refund/${id}`)
}

// 待收费项目
export function chargePending(caseNumber) {
  return service.get('/charge/pending', { params: { caseNumber } })
}

// 收费
export function charge(data) {
  return service.post('/charge', data)
}

// 费用流水查询
export function feeList(caseNumber) {
  return service.get('/fee/list', { params: { caseNumber } })
}

// 可退费列表
export function feeRefundable(caseNumber) {
  return service.get('/fee/refundable', { params: { caseNumber } })
}

// 退费
export function feeRefund(id) {
  return service.post(`/fee/refund/${id}`)
}
