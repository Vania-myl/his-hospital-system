// 检验管理模块路由
export default [
  {
    path: 'inspection',
    name: 'Inspection',
    component: () => import('@/views/inspection/Inspection.vue'),
    meta: { title: '检验管理', icon: 'el-icon-document-checked', roles: ['lab'] }
  }
]
