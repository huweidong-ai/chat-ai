import axios from 'axios';
import { getToken, handleUnauthorized } from '@/services/authService';
const request = axios.create({
  baseURL: process.env.VUE_APP_BASE_URL,
  timeout: 10000,
  headers: {'Content-Type': 'application/json'}
});

// 请求拦截器：添加token
request.interceptors.request.use(
  config => {
    const token = getToken();
    if (token) {
      // config.headers['Authorization'] = `Bearer ${token}`;
      config.headers['Authorization'] = token;
    }
    return config;
  },
  error => Promise.reject(error)
);

// 响应拦截器：处理响应和错误
request.interceptors.response.use(
  response => {
    const { data } = response;
    // 判断业务状态码{code: 200, data: {…}, message: '请求成功', success: true}
    if (data.code === 200 || data.success) {
      return {
        success: true,
        data: data.data,
        message: data.message || '操作成功'
      };
    } else {
      console.log('请求失败:', data.message);
      return Promise.reject(new Error(data.message || '请求失败'));
    }
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // console.log('status:', error.response.status);
          // console.log('错误信息:', error.response.data.message);
          if (error.response.data.code === '20004') {
            handleUnauthorized();
          }
          return Promise.reject(new Error(error.response.data.message || '认证失败'));
        case 403:
          return Promise.reject(new Error('没有权限访问'));
        case 404:
          return Promise.reject(new Error('请求的资源不存在'));
        case 500:
        case 502:
          return Promise.reject(new Error('服务器内部错误'));
        default:
          return Promise.reject(new Error('网络错误'));
      }
    }
    return Promise.reject(error);
  }
);

export default request;