<template>
  <el-container class="layout" direction="vertical">
    <!-- 顶部导航 -->
    <el-header class="topbar" height="60px">
      <div class="topbar-left">
        <div class="logo-box">
          <i class="el-icon-s-first-aid-kit"></i>
        </div>
        <span class="sys-name">东软云HIS医院管理系统</span>
      </div>
      <div class="topbar-right">
        <span class="user">
          <i class="el-icon-user-solid"></i>
          {{ user.realname || user.username }}
          <el-tag size="mini" class="role-tag">{{ roleName }}</el-tag>
        </span>
        <el-button type="text" class="logout" @click="handleLogout">
          <i class="el-icon-switch-button"></i> 退出登录
        </el-button>
      </div>
    </el-header>

    <el-container class="body">
      <!-- 左侧菜单 -->
      <el-aside width="200px" class="aside">
        <el-menu
          :default-active="activeMenu"
          router
          class="side-menu"
          background-color="#ffffff"
          text-color="#606266"
          active-text-color="#409EFF"
        >
          <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
            <i :class="item.icon"></i>
            <span>{{ item.title }}</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 内容区 -->
      <el-main class="content">
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
      const role = this.user.role
      return layoutRoute.children
        .filter(r => r.meta && r.meta.title)
        .filter(r => !r.meta.roles || !r.meta.roles.length || r.meta.roles.includes(role))
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

/* 顶部导航：蓝色渐变，白字 */
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(90deg, #1b6fd6 0%, #3f97f6 100%);
  color: #fff;
  padding: 0 20px;
}
.topbar-left {
  display: flex;
  align-items: center;
}
.logo-box {
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  font-size: 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  margin-right: 12px;
}
.sys-name {
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 1px;
}
.topbar-right {
  display: flex;
  align-items: center;
}
.user {
  display: flex;
  align-items: center;
  margin-right: 16px;
  font-size: 14px;
}
.role-tag {
  margin-left: 8px;
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.4);
  color: #fff;
}
.logout {
  color: #fff;
}
.logout:hover {
  color: #e0f0ff;
}

/* 主体：左侧菜单 + 内容区 */
.body {
  height: calc(100% - 60px);
}
.aside {
  background: #fff;
  border-right: 1px solid #e8eaee;
  box-shadow: 1px 0 4px rgba(0, 21, 41, 0.04);
}
.content {
  background: #f0f2f5;
  padding: 16px;
  overflow: auto;
}
</style>
