import service from './request'

// 登录
export function login(data) {
  return service.post('/login', data)
}

// 退出登录
export function logout() {
  return service.post('/logout')
}

// 当前登录用户信息
export function getUserInfo() {
  return service.get('/user/info')
}
