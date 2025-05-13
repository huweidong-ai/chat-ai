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
  // 设置token过期时间（20小时）
  const TOKEN_EXPIRY_TIME = 20 * 60 * 60 * 1000;

  // 检查token是否过期
  const isTokenExpired = () => {
    const tokenExpiry = localStorage.getItem('tokenExpiry');
    return !tokenExpiry || Date.now() > parseInt(tokenExpiry);
  };

  const login = async ({ phone, code }) => {
    const res = await phoneLogin(phone, code);
    if (res.code === 200 && res.data) {
      user.value = res.data.user;
      token.value = res.data.token;
      const expiryTime = Date.now() + TOKEN_EXPIRY_TIME;
      localStorage.setItem('user', JSON.stringify(res.data.user));
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('tokenExpiry', expiryTime.toString());
      return res;
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
    localStorage.removeItem('tokenExpiry');
  };

  // 初始化状态
  const initAuth = () => {
    const savedUser = localStorage.getItem('user');
    const savedToken = localStorage.getItem('token');
    if (savedUser && savedToken && !isTokenExpired()) {
      user.value = JSON.parse(savedUser);
      token.value = savedToken;
    } else {
      // 如果token过期，清除所有认证信息
      logout();
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