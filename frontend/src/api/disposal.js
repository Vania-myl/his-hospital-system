import service from './request'

export function disposalRequestList(params) {
  return service.get('/disposal-request/list', { params })
}

export function assignDisposalRequest(id) {
  return service.put(`/disposal-request/${id}/assign`)
}

export function saveDisposalResult(id, data) {
  return service.put(`/disposal-request/${id}/result`, data)
}
