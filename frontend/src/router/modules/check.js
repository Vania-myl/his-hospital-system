// 检查管理模块路由
export default [
  {
    path: 'check',
    name: 'Check',
    component: () => import('@/views/check/Check.vue'),
    meta: { title: '检查管理', icon: 'el-icon-camera', roles: ['check'] }
  }
]
