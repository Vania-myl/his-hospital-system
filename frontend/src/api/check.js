import service from './request'

export function checkRequestList(params) {
  return service.get('/check-request/list', { params })
}

export function assignCheckRequest(id) {
  return service.put(`/check-request/${id}/assign`)
}

export function saveCheckResult(id, data) {
  return service.put(`/check-request/${id}/result`, data)
}
