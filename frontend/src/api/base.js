import service from './request'

// 科室列表
export function departmentList() {
  return service.get('/department/list')
}

// 挂号级别列表
export function registLevelList() {
  return service.get('/regist-level/list')
}

// 结算类别列表
export function settleCategoryList() {
  return service.get('/settle-category/list')
}

// 排班列表
export function schedulingList() {
  return service.get('/scheduling/list')
}

// 员工列表
export function employeeList() {
  return service.get('/employee/list')
}
