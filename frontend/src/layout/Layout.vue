<template>
  <el-container class="layout">
    <el-aside width="200px">
      <div class="logo">东软云HIS</div>
      <el-menu
        :default-active="activeMenu"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <i :class="item.icon"></i>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <span class="user">{{ user.realname || user.username }}（{{ roleName }}）</span>
        <el-button type="text" @click="handleLogout">退出登录</el-button>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'Layout',
  data() {
    return {
      user: {}
    }
  },
  computed: {
    activeMenu() {
      return this.$route.path
    },
    // 从路由配置动态生成左侧菜单，新增模块路由自动出现，无需改本文件
    menuItems() {
      const layoutRoute = this.$router.options.routes.find(r => r.path === '/')
      if (!layoutRoute) return []
      return layoutRoute.children
        .filter(r => r.meta && r.meta.title)
        .map(r => ({
          path: '/' + r.path,
          title: r.meta.title,
          icon: r.meta.icon || 'el-icon-menu'
        }))
    },
    roleName() {
      const map = {
        window: '窗口收费',
        doctor: '门诊医生',
        check: '检查医生',
        lab: '检验医生',
        pharmacy: '药房人员',
        treat: '处置医生',
        admin: '管理员'
      }
      return map[this.user.role] || this.user.role
    }
  },
  created() {
    const user = localStorage.getItem('user')
    this.user = user ? JSON.parse(user) : {}
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.layout {
  height: 100%;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  background: #263445;
}
.header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  border-bottom: 1px solid #e6e6e6;
  background: #fff;
}
.user {
  margin-right: 16px;
}
</style>
