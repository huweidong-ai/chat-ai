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
import { ref, computed, onMounted } from 'vue';
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

        // 发送请求到后端
        isStreaming.value = true;
        
        // 创建 abort controller 用于取消请求
        const abortController = new AbortController();
        
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
            onopen: () => console.log('Connection opened'),
            onmessage: (event) => {
              // 处理接收到的消息
              try {
                const data = JSON.parse(event.data);
                const result = data.result;
                if (result && result.output && result.output.text) {
                  // 检查是否已存在AI回复消息
                  const lastMessage = chat.messages[chat.messages.length - 1];
                  if (lastMessage && lastMessage.type === 'AI') {
                    // 如果最后一条是AI消息，则追加内容并强制更新
                    lastMessage.content += result.output.text;
                    chat.messages.push(...chat.messages.splice(0));
                  } else {
                    // 如果不是，则创建新的AI消息
                    chat.messages.push({
                      type: 'AI',
                      content: result.output.text,
                      timestamp: new Date().toLocaleTimeString()
                    });
                  }
                }
                // 检查是否需要结束流式响应
                if (result && result.output && result.output.metadata && result.output.metadata.finishReason === 'STOP') {
                  isStreaming.value = false;
                }
              } catch (error) {
                console.error('解析消息数据失败:', error);
                // 在解析失败时添加友好的错误提示
                chat.messages.push({
                  type: 'system',
                  content: '抱歉，我遇到了一些问题，无法处理这个请求。请稍后再试。',
                  timestamp: new Date().toLocaleTimeString()
                });
                isStreaming.value = false;
              }
            },
            onclose: () => {
              isStreaming.value = false;
              console.log('Connection closed');
            },
            onerror: (error) => {
              isStreaming.value = false;
              console.error('SSE错误:', error);
              // 设置 errorMessage 状态为“系统繁忙，请稍后重试”
              errorMessage.value = '系统繁忙，请稍后重试。';
            },
            signal: abortController.signal
          }
        );
      }
    };

    const handleStopStream = () => {
      isStreaming.value = false;
    };

    onMounted(async () => {
      await checkAuth();
    });

    return {
      currentMessages,
      isStreaming,
      handleSendMessage,
      handleStopStream,
      errorMessage
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