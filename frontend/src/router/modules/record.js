// 综合管理模块路由（管理人员 admin）—— 就诊全流程记录 / 历史业务查询
export default [
  {
    path: 'record/flow',
    name: 'RecordFlow',
    component: () => import('@/views/record/RecordFlow.vue'),
    meta: { title: '就诊全流程记录', icon: 'el-icon-document', roles: ['admin'] }
  },
  {
    path: 'record/history',
    name: 'RecordHistory',
    component: () => import('@/views/record/RecordHistory.vue'),
    meta: { title: '历史业务查询', icon: 'el-icon-search', roles: ['admin'] }
  }
]
