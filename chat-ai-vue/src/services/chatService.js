import request, { ssePost } from '@/utils/request';

export const chatService = {
  // 开始SSE聊天流
  startChatStream(data, {
    onopen,
    onmessage,
    onclose,
    signal
  }) {
    ssePost('/api/chat/completions', data, onopen, onmessage, onclose, signal);
  },
  // 停止SSE聊天流
  stopChatStream(sseId) {
    return request({
      url: `/sse/stop/${sseId}`,
      method: 'get'
    });
  }
};