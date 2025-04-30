<template>
  <div class="chat-layout">
    <div class="chat-main">
      <ChatWindow
        :messages="currentMessages"
        :is-streaming="isStreaming"
        @stop-stream="handleStopStream"
        @send="handleSendMessage"
      />
    </div>
    <LoginModal
      v-if="showLoginModal"
      :visible="showLoginModal"
      @close="closeLoginModal"
      @login-success="handleLoginSuccess"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useAuthStore } from '@/store/modules/auth';
import ChatWindow from './ChatWindow.vue';
import LoginModal from '../auth/LoginModal.vue';

export default {
  name: 'ChatLayout',
  components: {
    ChatWindow,
    LoginModal
  },
  setup() {
    const authStore = useAuthStore();
    const showLoginModal = ref(false);
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

    const currentMessages = computed(() => {
      const chat = chatHistory.value.find(c => c.id === currentChatId.value);
      return chat ? chat.messages : [];
    });

    // 检查是否需要登录
    const checkAuth = () => {
      if (!authStore.isLoggedIn) {
        showLoginModal.value = true;
        return false;
      }
      return true;
    };

    const handleSendMessage = (message) => {
      if (!checkAuth() || !message.trim()) return;

      const chat = chatHistory.value.find(c => c.id === currentChatId.value);
      if (chat) {
        // 添加用户消息
        chat.messages.push({
          type: 'Me',
          content: message,
          timestamp: new Date().toLocaleTimeString()
        });

        // 模拟AI响应
        isStreaming.value = true;
        setTimeout(() => {
          chat.messages.push({
            type: 'AI',
            content: '这是一个模拟的AI响应',
            timestamp: new Date().toLocaleTimeString()
          });
          isStreaming.value = false;
        }, 1000);
      }
    };

    const handleStopStream = () => {
      isStreaming.value = false;
    };

    const closeLoginModal = () => {
      showLoginModal.value = false;
    };

    const handleLoginSuccess = () => {
      closeLoginModal();
    };

    onMounted(async () => {
      await authStore.checkAuth();
    });

    return {
      currentMessages,
      isStreaming,
      showLoginModal,
      handleSendMessage,
      handleStopStream,
      closeLoginModal,
      handleLoginSuccess
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
</style>