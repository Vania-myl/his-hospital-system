// 处置管理模块路由
export default [
  {
    path: 'disposal',
    name: 'Disposal',
    component: () => import('@/views/disposal/Disposal.vue'),
    meta: { title: '处置管理', icon: 'el-icon-first-aid-kit', roles: ['treat'] }
  }
]
