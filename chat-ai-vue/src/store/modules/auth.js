import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || null,
    isAuthenticated: false
  }),

  getters: {
    currentUser: (state) => state.user,
    isLoggedIn: (state) => state.isAuthenticated
  },

  actions: {
    async login(credentials) {
      try {
        // TODO: 实现登录API调用
        const response = await fetch('/api/auth/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(credentials)
        });

        if (!response.ok) {
          throw new Error('Login failed');
        }

        const data = await response.json();
        this.setAuthData(data);
        return data;
      } catch (error) {
        console.error('Login error:', error);
        throw error;
      }
    },

    async sendVerificationCode(phone) {
      try {
        // TODO: 实现发送验证码API调用
        const response = await fetch('/api/auth/send-code', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({ phone })
        });

        if (!response.ok) {
          throw new Error('Failed to send verification code');
        }

        return await response.json();
      } catch (error) {
        console.error('Send verification code error:', error);
        throw error;
      }
    },

    setAuthData(data) {
      this.token = data.token;
      this.user = data.user;
      this.isAuthenticated = true;
      localStorage.setItem('token', data.token);
    },

    logout() {
      this.token = null;
      this.user = null;
      this.isAuthenticated = false;
      localStorage.removeItem('token');
    },

    async checkAuth() {
      if (!this.token) return false;

      try {
        // TODO: 实现验证token的API调用
        const response = await fetch('/api/auth/verify', {
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        });

        if (!response.ok) {
          this.logout();
          return false;
        }

        const data = await response.json();
        this.user = data.user;
        this.isAuthenticated = true;
        return true;
      } catch (error) {
        console.error('Auth check error:', error);
        this.logout();
        return false;
      }
    }
  }
}); 