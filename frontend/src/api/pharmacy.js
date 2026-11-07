import request from './request'
export const prescriptions = params => request.get('/prescription/list', { params })
export const prescribe = data => request.post('/prescription', data)
export const cancelPrescription = id => request.put(`/prescription/${id}/cancel`)
