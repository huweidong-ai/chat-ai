import request from '@/utils/request';

// 从Cookie中获取token
export const getToken = () => {
  return localStorage.getItem('token');
};


// 移除token
export const removeToken = () => {
  localStorage.removeItem('user');
  localStorage.removeItem('token');
};

// 处理未授权的情况
export const handleUnauthorized = () => {
  removeToken();
  window.location.href = '/chat';
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