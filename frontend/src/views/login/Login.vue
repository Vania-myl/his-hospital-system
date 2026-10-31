<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="title">东软云HIS医院管理系统</h2>
      <el-form :model="form" @submit.native.prevent="handleLogin">
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="el-icon-user"></el-input>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            prefix-icon="el-icon-lock"
            show-password
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" native-type="submit">
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
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
          this.$router.push('/')
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
  background: #304156;
}
.login-card {
  width: 360px;
}
.title {
  text-align: center;
  margin-bottom: 20px;
}
</style>
