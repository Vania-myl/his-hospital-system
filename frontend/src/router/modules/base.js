// 公共模块路由
export default [
  {
    path: 'home',
    name: 'Home',
    component: () => import('@/views/home/Home.vue'),
    meta: { title: '首页', icon: 'el-icon-s-home' }
  }
]
