import axios from 'axios';
import { ElMessage } from 'element-plus';
import { handleUnauthorized, getToken } from '@/services/authService';
import { fetchEventSource } from '@microsoft/fetch-event-source';

const BASE_URL = process.env.VUE_APP_BASE_URL

// 创建axios实例
const request = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  headers: {'Content-Type': 'application/json'}
});


function ssePost (url, data, openFun, successFun, closeFun, signal) {
  fetchEventSource(BASE_URL + url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'text/event-stream',
      'Authorization': getToken()
    },
    body: JSON.stringify(data),
    onopen: openFun,
    onmessage(event ) {
      successFun(event)
    },
    onclose: closeFun,
    onerror(error) {
      throw error
    },
    openWhenHidden: true,
    signal
  });
}


// 请求拦截器：添加token
request.interceptors.request.use(
  config => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器：处理响应和错误
request.interceptors.response.use(
  response => {
    const res = response.data;
    // 如果响应成功
    if (res.code === 200) {
      return res;
    }
    // 处理业务错误
    ElMessage({
      message: res.message || '操作失败',
      type: 'error',
      duration: 3000
    });
    return Promise.reject(new Error(res.message || '操作失败'));
  },
  error => {
    console.error('Response error:', error);
    // 处理HTTP错误
    if (error.response) {
      const { status, data } = error.response;
      // 处理401未授权
      if (status === 401) {
        handleUnauthorized();
        return Promise.reject(new Error('请先登录'));
      }
      // 处理其他错误
      ElMessage({
        message: data?.message || `请求失败(${status})`,
        type: 'error',
        duration: 3000
      });
    } else if (error.request) {
      // 请求发出但没有收到响应
      ElMessage({
        message: '网络错误，请检查网络连接',
        type: 'error',
        duration: 3000
      });
    } else {
      // 请求配置出错
      ElMessage({
        message: error.message || '请求配置错误',
        type: 'error',
        duration: 3000
      });
    }
    return Promise.reject(error);
  }
);

export { ssePost };
export default request;