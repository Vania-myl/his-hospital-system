// 挂号收费模块路由
export default [
  {
    path: 'register',
    name: 'Register',
    component: () => import('@/views/register/Register.vue'),
    meta: { title: '挂号收费', icon: 'el-icon-s-order' }
  }
]
