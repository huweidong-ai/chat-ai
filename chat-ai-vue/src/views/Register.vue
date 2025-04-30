<template>
  <div class="login-container">
    <h2>注册</h2>
    <form @submit.prevent="handleRegister">
      <input type="text" v-model="username" placeholder="用户名" required>
      <input type="password" v-model="password" placeholder="密码" required>
      <input type="password" v-model="confirmPassword" placeholder="确认密码" required>
      <input type="submit" value="注册">
    </form>
    <div id="message">{{ message }}</div>
    <div class="nav-link">
      已有账号？<router-link to="/login">立即登录</router-link>
    </div>
  </div>
</template>

<script>
import { userService } from '@/services/userService';

export default {
  name: 'UserRegister',
  data() {
    return {
      username: '',
      password: '',
      confirmPassword: '',
      message: ''
    };
  },
  methods: {
    async handleRegister() {
      this.username = this.username.trim();
      this.password = this.password.trim();
      this.confirmPassword = this.confirmPassword.trim();

      if (!this.username || !this.password || !this.confirmPassword) {
        this.message = '请填写所有字段';
        return;
      }

      if (this.password !== this.confirmPassword) {
        this.message = '两次输入的密码不一致';
        return;
      }

      try {
        const data = await userService.register({
          username: this.username,
          password: this.password
        });
        
        if (data.success) {
          this.message = '注册成功！';
          setTimeout(() => {
            this.$router.push('/login');
          }, 1500);
        } else {
          this.message = data.message || '注册失败，请重试';
        }
      } catch (error) {
        this.message = '网络错误，请重试';
      }
    }
  }
};
</script>

<style scoped>
/* 删除 body 相关样式 */
.login-container {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  width: 300px;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
h2 {
  text-align: center;
  color: #333;
}
form {
  display: flex;
  flex-direction: column;
}
input[type="text"], input[type="password"] {
  margin-bottom: 10px;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
input[type="submit"] {
  background-color: #007BFF;
  color: #fff;
  padding: 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
input[type="submit"]:hover {
  background-color: #0056b3;
}
#message {
  margin-top: 20px;
  text-align: center;
  color: #d9534f;
}
.nav-link {
  margin-top: 15px;
  text-align: center;
  color: #666;
}
.nav-link a {
  color: #007BFF;
  text-decoration: none;
}
.nav-link a:hover {
  text-decoration: underline;
}
</style>