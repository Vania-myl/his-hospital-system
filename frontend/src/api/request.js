import axios from 'axios'
import { Message } from 'element-ui'
import router from '@/router'

// axios 实例：baseURL 统一 /api（vue.config.js 里已代理到后端 8080）
const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：自动携带 token
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

// 响应拦截器：统一处理 { code, msg, data }
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    if (res.code === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      Message.error(res.msg || '登录已过期，请重新登录')
      // 已在登录页时避免重复跳转，否则 vue-router 抛 NavigationDuplicated
      if (router.currentRoute.path !== '/login') {
        router.push('/login').catch(() => {})
      }
      return Promise.reject(new Error(res.msg))
    }
    Message.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg))
  },
  error => {
    Message.error('网络异常，请检查后端是否已启动')
    return Promise.reject(error)
  }
)

export default service
