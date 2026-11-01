// 公共模块路由
export default [
  {
    path: 'home',
    name: 'Home',
    component: () => import('@/views/home/Home.vue'),
    // 首页面向管理员及未实现模块的其他岗位；窗口收费岗（window）无首页，登录直接进挂号
    meta: { title: '首页', icon: 'el-icon-s-home', roles: ['doctor', 'check', 'lab', 'pharmacy', 'treat'] }
  }
]
