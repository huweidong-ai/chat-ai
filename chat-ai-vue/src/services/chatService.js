import request, { ssePost } from '@/utils/request';

export const chatService = {
  /**
   * 开始SSE聊天流
   * @param {Object} data - 请求数据
   * @param {Object} options - 配置选项
   * @param {Function} options.onStart - 连接开始时的回调
   * @param {Function} options.onMessage - 收到消息时的回调
   * @param {Function} options.onComplete - 流完成时的回调
   * @param {Function} options.onError - 发生错误时的回调
   * @param {AbortSignal} options.signal - 用于取消请求的信号
   * @returns {Object} 包含取消方法的对象
   */
  startChatStream(data, options) {
    return ssePost('/api/chat/completions', data, options);
  },
  
  /**
   * 停止SSE聊天流
   * @param {string} sseId - SSE连接ID
   * @returns {Promise} 请求响应
   */
  stopChatStream(sseId) {
    return request({
      url: `/sse/stop/${sseId}`,
      method: 'get'
    });
  }
};