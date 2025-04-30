import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { sendVerificationCode, phoneLogin, getCurrentUser } from '@/services/authService';

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null);
  const token = ref(null);
  const showLoginModal = ref(false);

  const isLoggedIn = computed(() => !!token.value);

  // 发送验证码（调用service）
  const sendCode = async (phone) => {
    return sendVerificationCode(phone);
  };

  // 手机号验证码登录（调用service）
  const login = async ({ phone, code }) => {
    const res = await phoneLogin(phone, code);
    if (res.success && res.data) {
      user.value = res.data.user;
      token.value = res.data.token;
      localStorage.setItem('user', JSON.stringify(res.data.user));
      localStorage.setItem('token', res.data.token);
      return res.data.user;
    } else {
      throw new Error(res.message || '登录失败');
    }
  };

  // 获取当前用户（可选，用于自动登录）
  const fetchCurrentUser = async () => {
    const res = await getCurrentUser();
    if (res.success && res.data) {
      user.value = res.data;
      return res.data;
    }
    return null;
  };

  // 登出
  const logout = () => {
    user.value = null;
    token.value = null;
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  };

  // 初始化状态
  const initAuth = () => {
    const savedUser = localStorage.getItem('user');
    const savedToken = localStorage.getItem('token');
    if (savedUser && savedToken) {
      user.value = JSON.parse(savedUser);
      token.value = savedToken;
    }
  };

  return {
    user,
    token,
    isLoggedIn,
    sendCode,
    login,
    logout,
    initAuth,
    fetchCurrentUser,
    showLoginModal
  };
}); 