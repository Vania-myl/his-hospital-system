import Vue from 'vue'
import VueRouter from 'vue-router'
import Layout from '@/layout/Layout.vue'
import Login from '@/views/login/Login.vue'

// 各模块路由（挂在 Layout 下，相对路径）
import baseRoutes from './modules/base'
import registerRoutes from './modules/register'
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
    redirect: '/home',
    children: [
      ...baseRoutes,
      ...registerRoutes,
      ...statisticsRoutes,
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

export default router
