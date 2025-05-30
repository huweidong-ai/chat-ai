<template>
  <div class="chat-layout">
    <div class="chat-main">
      <ChatWindow
        :messages="currentMessages"
        :is-streaming="isStreaming"
        @stop-stream="handleStopStream"
        @send="handleSendMessage"
      />
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick } from 'vue';
import { useAuthStore } from '@/store/modules/auth';
import ChatWindow from './ChatWindow.vue';
import { chatService } from '@/services/chatService';

export default {
  name: 'ChatLayout',
  components: {
    ChatWindow
  },
  setup() {
    const authStore = useAuthStore();
    const chatHistory = ref([
      {
        id: '1',
        title: '关于Vue3的讨论',
        time: '10:30',
        messages: []
      },
      {
        id: '2',
        title: 'AI助手使用指南',
        time: '昨天',
        messages: []
      }
    ]);

    const currentChatId = ref('1');
    const isStreaming = ref(false);
    const errorMessage = ref(null);
    // 创建 abort controller 用于取消请求
    const abortController = ref(null);
    
    // 添加滚动到底部的函数
    const scrollToBottom = () => {
      nextTick(() => {
        const chatBody = document.querySelector('.chat-body');
        if (chatBody) {
          chatBody.scrollTop = chatBody.scrollHeight;
        }
      });
    };

    const currentMessages = computed(() => {
      const chat = chatHistory.value.find(c => c.id === currentChatId.value);
      return chat ? chat.messages : [];
    });

    // 检查是否需要登录
    const checkAuth = () => {
      if (!authStore.isLoggedIn) {
        authStore.showLoginModal = true;
        return false;
      }
      return true;
    };

    const handleSendMessage = (message) => {
      // 确保检查 message.text 而非整个 message 对象
      if (!checkAuth() || !message.text || !message.text.trim()) return;

      const chat = chatHistory.value.find(c => c.id === currentChatId.value);
      if (chat) {
        // 添加用户消息
        chat.messages.push({
          type: 'Me',
          content: message.text,
          timestamp: new Date().toLocaleTimeString()
        });
        
        // 滚动到最新消息
        scrollToBottom();

        // 发送请求到后端
        isStreaming.value = true;
        
        // 重置并创建新的 abort controller
        abortController.value = new AbortController();
        
        // 开始SSE流
        chatService.startChatStream(
          {
            messages: [
              {
                role: 'user',
                content: message.text
              }
            ],
            model: 'glm-4-flash',
            temperature: 0.7,
            stream: true
          },
          {
            // 连接开始时的回调
            onStart: () => {
              console.log('连接已开始');
            },
            
            // 收到消息时的回调
            onMessage: (data) => {
              if (!data) return;
              
              console.log('处理消息:', data.result?.output?.text ? data.result.output.text.substring(0, 30) + '...' : '无文本内容');
              
              const result = data.result;
              // 检查是否有文本内容
              if (result && result.output) {
                const text = result.output.text || '';
                
                // 如果有文本内容，则处理
                if (text) {
                  // 检查是否已存在AI回复消息
                  const lastMessageIndex = chat.messages.length - 1;
                  const lastMessage = chat.messages[lastMessageIndex];
                  
                  if (lastMessage && lastMessage.type === 'AI') {
                    // 已有AI消息，追加内容
                    console.log('添加新文本到现有消息:', text);
                    
                    // 创建新消息数组并替换旧数组
                    const newMessages = [...chat.messages];
                    newMessages[lastMessageIndex] = {
                      ...lastMessage,
                      content: lastMessage.content + text
                    };
                    
                    // 强制Vue更新数组
                    chat.messages.length = 0;
                    chat.messages.push(...newMessages);
                  } else {
                    // 新建一条AI消息
                    console.log('创建新的AI消息');
                    chat.messages.push({
                      type: 'AI',
                      content: text,
                      timestamp: new Date().toLocaleTimeString()
                    });
                  }
                  
                  // 滚动到底部
                  scrollToBottom();
                }
                
                // 检查是否完成
                const finishReason = result?.output?.metadata?.finishReason;
                if (finishReason === 'STOP') {
                  console.log('流式响应完成，原因:', finishReason);
                  isStreaming.value = false;
                }
              }
            },
            
            // 流完成时的回调
            onComplete: () => {
              console.log('流式响应完成');
              isStreaming.value = false;
            },
            
            // 发生错误时的回调
            onError: (error) => {
              console.error('错误发生:', error);
              isStreaming.value = false;
              
              // 设置错误消息
              errorMessage.value = error.message;
              
              // 在聊天中添加系统错误消息
              chat.messages.push({
                type: 'system',
                content: `错误: ${error.message}`,
                timestamp: new Date().toLocaleTimeString()
              });
              
              // 滚动到底部显示错误消息
              scrollToBottom();
            },
            
            // 用于取消请求的信号
            signal: abortController.value.signal
          }
        );
      }
    };

    const handleStopStream = () => {
      isStreaming.value = false;
      // 如果存在abortController，则取消请求
      if (abortController.value) {
        abortController.value.abort();
      }
    };

    onMounted(async () => {
      await checkAuth();
    });

    return {
      currentMessages,
      isStreaming,
      handleSendMessage,
      handleStopStream,
      errorMessage,
      scrollToBottom
    };
  }
};
</script>

<style scoped>
.chat-layout {
  height: 100vh;
  background-color: #fff;
}

.chat-main {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  position: relative;
}

.error-message {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  padding: 1rem;
  color: #721c24;
  z-index: 1000;
  border-radius: 0.25rem;
}
</style>