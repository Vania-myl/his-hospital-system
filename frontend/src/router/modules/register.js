// 挂号收费模块路由（窗口收费 window）
export default [
  {
    path: 'register',
    name: 'Register',
    component: () => import('@/views/register/RegisterForm.vue'),
    meta: { title: '挂号', icon: 'el-icon-s-order', roles: ['window'] }
  },
  {
    path: 'register/refund',
    name: 'RefundReg',
    component: () => import('@/views/register/RefundReg.vue'),
    meta: { title: '退号', icon: 'el-icon-refresh-left', roles: ['window'] }
  },
  {
    path: 'register/charge',
    name: 'Charge',
    component: () => import('@/views/register/Charge.vue'),
    meta: { title: '收费', icon: 'el-icon-s-finance', roles: ['window'] }
  },
  {
    path: 'register/refund-fee',
    name: 'RefundFee',
    component: () => import('@/views/register/RefundFee.vue'),
    meta: { title: '退费', icon: 'el-icon-money', roles: ['window'] }
  },
  {
    path: 'register/fee-query',
    name: 'FeeQuery',
    component: () => import('@/views/register/FeeQuery.vue'),
    meta: { title: '费用流水查询', icon: 'el-icon-s-data', roles: ['window'] }
  }
]
