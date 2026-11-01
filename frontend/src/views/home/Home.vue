<template>
  <div class="page-container">
    <el-card class="page-card welcome-card" shadow="never">
      <div class="welcome-icon">
        <i class="el-icon-s-first-aid-kit"></i>
      </div>
      <h2 class="welcome-title">欢迎使用东软云HIS医院管理系统</h2>
      <p class="welcome-sub">请从左侧菜单选择功能模块开始工作。</p>
      <el-alert
        title="权限说明"
        type="info"
        :closable="false"
        show-icon
        description="不同岗位登录后，左侧菜单将按岗位权限动态显示，仅展示该岗位可访问的功能模块。"
      ></el-alert>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.welcome-card {
  text-align: center;
  padding: 48px 20px;
}
.welcome-icon {
  width: 72px;
  height: 72px;
  line-height: 72px;
  margin: 0 auto 20px;
  font-size: 40px;
  color: #409eff;
  background: #ecf5ff;
  border-radius: 50%;
}
.welcome-title {
  font-size: 22px;
  color: #303133;
  margin: 0 0 8px;
}
.welcome-sub {
  color: #909399;
  margin: 0 0 24px;
}
.el-alert {
  max-width: 560px;
  margin: 0 auto;
  text-align: left;
}
</style>
