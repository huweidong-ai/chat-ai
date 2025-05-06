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
    return fetchEventSource(`${process.env.VUE_APP_BASE_URL}/api/chat/completions`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        messages: [{ role: 'user', content: data.text }],
        model: 'glm-4-flash',
        temperature: 0.7,
        stream: true
      }),
      openWhenHidden: true,
      signal,
      onopen(response) {
        if (response.status === 401) {
          handleUnauthorized();
          console.error('未授权，请重新登录');
        }
        if (response.status === 403) {
          console.error('禁止访问:', response.status);
        }
        if (response.ok && response.headers.get('content-type') === 'text/event-stream') {
          if (onopen) onopen(response);
        } else {
          console.error(`服务器响应异常：${response.status}`);
        }
      },
      onmessage,
      onclose,
      onerror(error) {
        console.error('SSE连接错误:', error);
        if (onerror) onerror(`请求失败: ${error.message}`);
        // 仅在非中断情况下抛出错误
        if (!(error instanceof DOMException && error.name === 'AbortError')) {
          console.error(`请求失败: ${error.message}`);
        }
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