import request from '@/utils/request';
import { getToken, handleUnauthorized } from '@/services/authService';
import { fetchEventSource } from '@microsoft/fetch-event-source';

export const chatService = {
  // 开始SSE聊天流
  startChatStream(data, {
    onopen,
    onmessage,
    onclose,
    onerror,
    signal
  }) {
    const token = getToken();
    return fetchEventSource(`${process.env.VUE_APP_BASE_URL}/sse/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
        'Authorization': token
      },
      body: JSON.stringify(data),
      openWhenHidden: true,
      signal,
      onopen(response) {
        if (response.status === 401) {
          handleUnauthorized();
          throw new Error('未授权，请重新登录');
        }
        if (response.ok && response.headers.get('content-type') === 'text/event-stream') {
          if (onopen) onopen(response);
        } else {
          throw new Error(`服务器响应异常：${response.status}`);
        }
      },
      onmessage,
      onclose,
      onerror(error) {
        console.error('SSE连接错误:', error);
        if (onerror) onerror(error);
        throw new Error("请求失败，停止重试");
      }
    });
  },

  // 停止SSE聊天流
  stopChatStream(sseId) {
    return request({
      url: `/sse/stop/${sseId}`,
      method: 'get'
    });
  }
};