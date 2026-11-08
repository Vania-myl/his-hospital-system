import service from './request'

export function inspectionRequestList(params) {
  return service.get('/inspection-request/list', { params })
}

export function assignInspectionRequest(id) {
  return service.put(`/inspection-request/${id}/assign`)
}

export function saveInspectionResult(id, data) {
  return service.put(`/inspection-request/${id}/result`, data)
}
