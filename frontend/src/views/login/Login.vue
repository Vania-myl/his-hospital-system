<template>
  <div class="login-container">
    <div class="login-box">
      <div class="brand">
        <div class="logo">
          <i class="el-icon-s-first-aid-kit"></i>
        </div>
        <h1>东软云HIS医院管理系统</h1>
        <p>Neusoft Cloud Hospital Information System</p>
      </div>

      <el-card class="login-card" shadow="never">
        <div class="card-title">用户登录</div>
        <el-form :model="form" @submit.native.prevent="handleLogin">
          <el-form-item>
            <el-input
              v-model="form.username"
              placeholder="用户名"
              prefix-icon="el-icon-user"
              size="medium"
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="密码"
              prefix-icon="el-icon-lock"
              show-password
              size="medium"
            ></el-input>
          </el-form-item>
          <el-form-item class="btn-item">
            <el-button type="primary" :loading="loading" class="login-btn" native-type="submit">
              登 录
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <div class="copyright">© 2026 东软云HIS医院管理系统</div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth'

export default {
  name: 'Login',
  data() {
    return {
      form: { username: '', password: '' },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      if (!this.form.username || !this.form.password) {
        this.$message.warning('请输入用户名和密码')
        return
      }
      this.loading = true
      login(this.form)
        .then(data => {
          localStorage.setItem('token', data.token)
          localStorage.setItem('user', JSON.stringify(data.user))
          this.$message.success('登录成功')
          // 守卫会按角色重定向 / → /register，push 的 Promise 会以 NavigationRedirected 拒绝，需吞掉
          this.$router.push('/').catch(() => {})
        })
        .catch(() => {
          // 错误已由拦截器统一提示
        })
        .finally(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1b6fd6 0%, #3f97f6 60%, #7cb8ff 100%);
}
.login-box {
  width: 420px;
}
.brand {
  text-align: center;
  margin-bottom: 28px;
  color: #fff;
}
.logo {
  width: 64px;
  height: 64px;
  line-height: 64px;
  margin: 0 auto 16px;
  background: rgba(255, 255, 255, 0.18);
  border-radius: 16px;
  font-size: 34px;
}
.brand h1 {
  font-size: 24px;
  margin: 0 0 8px;
  letter-spacing: 1px;
}
.brand p {
  font-size: 13px;
  margin: 0;
  opacity: 0.85;
  letter-spacing: 0.5px;
}
.login-card {
  border-radius: 8px;
}
.card-title {
  text-align: center;
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 20px;
}
.btn-item {
  margin-bottom: 0;
}
.login-btn {
  width: 100%;
  letter-spacing: 4px;
}
.copyright {
  text-align: center;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
  margin-top: 24px;
}
</style>
