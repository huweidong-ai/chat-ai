<template>
  <div class="chat-container">
    <header class="chat-header">
      <div class="header-left">
        <h3>{{ chatHeaderTitle }}</h3>
      </div>
      <div class="header-right">
        <button v-if="isStreaming" class="icon-btn stop-btn" title="停止生成" @click="stopStream">
          <i class="fas fa-stop"></i>
        </button>
        <button class="icon-btn" title="复制对话">
          <i class="fas fa-copy"></i>
        </button>
        <button class="icon-btn" title="导出对话">
          <i class="fas fa-download"></i>
        </button>
      </div>
    </header>
    <main class="chat-body">
      <ul class="message-list">
        <li v-for="(msg, index) in messages" :key="index" :class="['message', msg.type]">
          <div class="avatar">
            <img v-if="msg.type === 'AI'" src="../../assets/ai-avatar.png" alt="AI" />
            <img v-else-if="msg.type === 'Me' && authStore.user && authStore.user.avatar" :src="authStore.user.avatar" alt="用户头像" />
            <span v-else>{{ msg.type === 'Me' ? '我' : (msg.type === 'system' ? '系统' : 'AI') }}</span>
          </div>
          <div class="message-wrapper">
            <div class="message-header">
              <span class="sender-name">{{ msg.type === 'Me' ? '我' : (msg.type === 'system' ? '系统' : 'AI助手') }}</span>
              <span class="message-time">{{ msg.timestamp || '刚刚' }}</span>
            </div>
            <div v-if="msg.type === 'system'" class="message-content system">{{ msg.content }}</div>
            <div v-else v-html="renderMarkdown(msg.content)" class="message-content"></div>
            <div class="message-actions" v-if="msg.type === 'AI'">
              <button class="action-btn" title="复制">
                <i class="fas fa-copy"></i>
              </button>
              <button class="action-btn" title="点赞">
                <i class="fas fa-thumbs-up"></i>
              </button>
              <button class="action-btn" title="点踩">
                <i class="fas fa-thumbs-down"></i>
              </button>
            </div>
          </div>
        </li>
      </ul>
    </main>
    <div class="chat-footer">
      <ChatInput @send="handleSend" class="chat-input" />
    </div>
  </div>
</template>

<script>
import { computed } from 'vue';
import { Marked } from 'marked';
import { markedHighlight } from 'marked-highlight';
import hljs from 'highlight.js/lib/core';
import javascript from 'highlight.js/lib/languages/javascript';
import python from 'highlight.js/lib/languages/python';
import java from 'highlight.js/lib/languages/java';
import shell from 'highlight.js/lib/languages/shell';
import sql from 'highlight.js/lib/languages/sql';
import plaintext from 'highlight.js/lib/languages/plaintext';
import ChatInput from './ChatInput.vue';
import { useAuthStore } from '@/store/modules/auth';
import 'highlight.js/styles/atom-one-dark.css';

hljs.registerLanguage('javascript', javascript);
hljs.registerLanguage('python', python);
hljs.registerLanguage('java', java);
hljs.registerLanguage('shell', shell);
hljs.registerLanguage('sql', sql);
hljs.registerLanguage('plaintext', plaintext);

const renderer = new Marked(
  markedHighlight({
    langPrefix: 'hljs language-',
    highlight(code, lang) {
      const language = hljs.getLanguage(lang) ? lang : 'plaintext';
      return hljs.highlight(code, { language }).value;
    }
  })
);

export default {
  name: 'ChatWindow',
  components: {
    ChatInput
  },
  props: {
    messages: {
      type: Array,
      required: true
    },
    isStreaming: {
      type: Boolean,
      default: false
    }
  },

  setup(props, { emit }) {
    const authStore = useAuthStore();
    const renderMarkdown = (content) => {
      try {
        if (content === null || content === undefined) {
          console.error('Content is null or undefined');
          return '';
        }
        if (typeof content === 'object' && content.content) {
          content = content.content;
        }
        const strContent = typeof content === 'object' ? JSON.stringify(content) : String(content);
        if (strContent.length > 10000) {
          console.warn('Content too long, truncating...');
          return `${strContent.substring(0, 10000)}... (内容过长已截断)`;
        }
        const sanitizedContent = strContent
          .replace(/^\s*[#\-*]\s*$/gm, '')
          .replace(/([\r\n])\1{3,}/g, '$1$1');
        return renderer.parse(sanitizedContent);
      } catch (error) {
        console.error('Markdown rendering error:', error);
        return '内容渲染失败，请查看原始文本';
      }
    };

    const chatHeaderTitle = computed(() => {
      return props.isStreaming ? 'AI回复中...' : '聊天窗口';
    });

    const handleSend = (message) => {
      emit('send', message);
    };
    
    const stopStream = () => {
      emit('stop-stream');
    };

    return {
      renderMarkdown,
      chatHeaderTitle,
      handleSend,
      stopStream,
      authStore
    };
  }
};
</script>

<style scoped>
.chat-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  position: relative;
  height: 100%;
  overflow: hidden;
}

.chat-header {
  padding: 16px 24px;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  padding-bottom: 100px; /* Add space for the input area */
}

.chat-footer {
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  z-index: 10;
}

.message-list {
  list-style: none;
  margin: 0;
  padding: 0;
}

.message {
  display: flex;
  margin-bottom: 24px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 16px;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.message-wrapper {
  flex: 1;
  max-width: calc(100% - 56px);
}

.message-header {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.sender-name {
  font-weight: 500;
  color: #333;
  margin-right: 8px;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-content {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 12px 16px;
  color: #333;
  line-height: 1.5;
  word-break: break-word;
}

.message-content.system {
  background: #fff3e0;
  color: #e65100;
}

.message-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
  justify-content: flex-end;
}

.action-btn {
  padding: 4px 8px;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: #f0f0f0;
  color: #333;
}

.icon-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.icon-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.header-right {
  display: flex;
  gap: 8px;
}

.stop-btn {
  background-color: #f56c6c;
  color: white;
}

.stop-btn:hover {
  background-color: #e64242;
  color: white;
}

.chat-input {
  border-top: 1px solid #e6e6e6;
}
</style>