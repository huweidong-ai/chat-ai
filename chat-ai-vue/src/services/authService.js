import request from '@/utils/request';

// 处理认证相关的服务
export const TOKEN_KEY = 'authToken';

// 从Cookie中获取token
export const getToken = () => {
  return document.cookie.replace(/(?:^|.*;\s*)authToken\s*=\s*([^;]*).*$|^.*$/, "$1");
};

// 设置token到Cookie
export const setToken = (token, hours = 8) => {
  const date = new Date();
  date.setTime(date.getTime() + (hours * 60 * 60 * 1000));
  document.cookie = `${TOKEN_KEY}=${encodeURIComponent(token)};expires=${date.toUTCString()};path=/`;
};

// 移除token
export const removeToken = () => {
  document.cookie = `${TOKEN_KEY}=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/`;
};

// 检查是否已登录
export const isAuthenticated = () => {
  return !!getToken();
};

// 处理未授权的情况
export const handleUnauthorized = () => {
  removeToken();
  window.location.href = '/login';
};

// 发送验证码
export const sendVerificationCode = (phone) => {
  return request({
    url: '/api/auth/verification-code',
    method: 'post',
    data: { phone }
  });
};

// 手机号验证码登录
export const phoneLogin = (phone, code) => {
  return request({
    url: '/api/auth/login/phone',
    method: 'post',
    data: { phone, code }
  });
};

// 获取当前用户
export const getCurrentUser = () => {
  return request({
    url: '/api/auth/me',
    method: 'get'
  });
};