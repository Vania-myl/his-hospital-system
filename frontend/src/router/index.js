import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layout/Layout.vue'
import Login from '@/views/login/Login.vue'

// 各模块路由（挂在 Layout 下，相对路径）
import baseRoutes from './modules/base'
import registerRoutes from './modules/register'
import recordRoutes from './modules/record'
import statisticsRoutes from './modules/statistics'
import outpatientRoutes from './modules/outpatient'
import pharmacyRoutes from './modules/pharmacy'
import checkRoutes from './modules/check'
import inspectionRoutes from './modules/inspection'
import disposalRoutes from './modules/disposal'

Vue.use(VueRouter)

const routes = [
  { path: '/login', name: 'Login', component: Login, meta: { title: '登录' } },
  {
    path: '/',
    component: Layout,
    children: [
      ...baseRoutes,
      ...registerRoutes,
      ...statisticsRoutes,
      ...recordRoutes,
      ...outpatientRoutes,
      ...pharmacyRoutes,
      ...checkRoutes,
      ...inspectionRoutes,
      ...disposalRoutes
    ]
  }
]

const router = new VueRouter({
  mode: 'hash',
  routes
})

// 各岗位登录后的默认落地页（其他岗位模块实现后在此补充）
const ROLE_HOME = {
  window: '/register',
  doctor: '/outpatient',
  admin: '/statistics'
}
// 路由守卫：登录校验 + 角色权限（admin 可访问全部）
router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    return next()
  }
  const token = localStorage.getItem('token')
  if (!token) {
    return next('/login')
  }
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const role = user.role

  // 根路径按角色落到各岗位首页
  if (to.path === '/') {
    return next(ROLE_HOME[role] || '/home')
  }

  // 角色无权访问：跳回该角色首页
  const roles = to.meta && to.meta.roles
  if (roles && roles.length && !roles.includes(role)) {
    return next(ROLE_HOME[role] || '/home')
  }
  next()
})

export default router
