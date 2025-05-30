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


/**
 * 发送SSE请求并处理流式响应
 * @param {string} url - 请求路径
 * @param {Object} data - 请求数据
 * @param {Object} options - 配置选项
 * @param {Function} options.onStart - 连接开始时的回调
 * @param {Function} options.onMessage - 收到消息时的回调
 * @param {Function} options.onComplete - 流完成时的回调
 * @param {Function} options.onError - 发生错误时的回调
 * @param {AbortSignal} options.signal - 用于取消请求的信号
 * @returns {Object} 包含取消方法的对象
 */
function ssePost(url, data, options = {}) {
  const {
    onStart,
    onMessage,
    onComplete,
    onError,
    signal
  } = options;
  
  // 创建默认的错误处理函数
  const handleError = (error, status) => {
    console.error('SSE error:', error);
    
    // 构建错误消息
    let errorMessage = '连接错误，请稍后重试';
    if (status) {
      errorMessage = `服务器错误 (${status}): 请稍后重试`;
    }
    
    // 显示错误消息
    ElMessage.error(errorMessage);
    
    // 如果提供了错误回调，则调用它
    if (onError) {
      try {
        onError({
          message: errorMessage,
          status,
          originalError: error
        });
      } catch (e) {
        console.error('Error in error callback:', e);
      }
    }
  };
  
  // 调用fetchEventSource
  fetchEventSource(BASE_URL + url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Accept': 'text/event-stream',
      'Authorization': `Bearer ${getToken()}`
    },
    body: JSON.stringify(data),
    onopen: async (response) => {
      console.log('SSE connection opened with status:', response.status);
      
      // 检查响应状态码
      if (!response.ok) {
        console.error(`SSE connection failed with status: ${response.status}`);
        // 读取错误响应内容
        const errorText = await response.text();
        console.error('Error response:', errorText);
        
        // 处理错误
        handleError(new Error(errorText), response.status);
        return;
      }
      
      // 调用开始回调
      if (onStart) {
        try {
          onStart(response);
        } catch (e) {
          console.error('Error in start callback:', e);
        }
      }
    },
    onmessage: (event) => {
      if (event && event.data) {
        try {
          // 尝试解析消息
          const data = JSON.parse(event.data);
          
          // 调用消息回调
          if (onMessage) {
            onMessage(data, event);
          }
          
          // 检查是否完成
          const finishReason = data?.result?.output?.metadata?.finishReason;
          if (finishReason === 'STOP' && onComplete) {
            try {
              onComplete(data);
            } catch (e) {
              console.error('Error in complete callback:', e);
            }
          }
        } catch (e) {
          console.error('Error parsing SSE message:', e);
          // 即使解析错误也传递原始消息
          if (onMessage) {
            onMessage(null, event);
          }
        }
      }
    },
    onclose: () => {
      console.log('SSE connection closed');
      if (onComplete) {
        try {
          onComplete();
        } catch (e) {
          console.error('Error in complete callback:', e);
        }
      }
    },
    onerror: (error) => {
      handleError(error);
    },
    openWhenHidden: true,
    signal
  });
  
  // 返回一个对象，包含取消方法
  return {
    cancel: () => {
      console.log('SSE connection manually cancelled');
      if (onComplete) {
        try {
          onComplete({ cancelled: true });
        } catch (e) {
          console.error('Error in complete callback:', e);
        }
      }
    }
  };
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
        message: data?.message || `系统繁忙，稍后重试(${status})`,
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