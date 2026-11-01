// 综合管理模块路由（管理人员 admin）—— 业务数据统计
export default [
  {
    path: 'statistics',
    name: 'Statistics',
    component: () => import('@/views/statistics/Statistics.vue'),
    meta: { title: '业务数据统计', icon: 'el-icon-data-analysis', roles: ['admin'] }
  }
]
