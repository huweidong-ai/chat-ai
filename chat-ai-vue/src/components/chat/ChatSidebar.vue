<template>
  <div class="chat-sidebar">
    <div class="sidebar-header">
      <img src="@/assets/logo.png" alt="Logo" class="logo" />
      <h2>AI助手</h2>
    </div>
    
    <div class="new-chat-btn">
      <button @click="$emit('new-chat')" class="primary-btn">
        <i class="fas fa-plus"></i>
        新建对话
      </button>
    </div>

    <div class="sidebar-sections">
      <div class="section">
        <h3>效率工具</h3>
        <ul class="tool-list">
          <li>
            <button class="tool-item">
              <i class="fas fa-clock"></i>
              <span>实时记录</span>
            </button>
          </li>
          <li>
            <button class="tool-item">
              <i class="fas fa-book"></i>
              <span>阅读助手</span>
            </button>
          </li>
          <li>
            <button class="tool-item">
              <i class="fas fa-file-powerpoint"></i>
              <span>PPT制作</span>
            </button>
          </li>
        </ul>
      </div>

      <div class="section">
        <h3>历史对话</h3>
        <ul class="chat-history">
          <li v-for="(chat, index) in chatHistory" :key="index">
            <div class="history-item-wrapper">
              <button 
                :class="['history-item', { active: chat.id === currentChatId }]"
                @click="$emit('select-chat', chat.id)"
              >
                <span class="chat-title">{{ chat.title }}</span>
                <span class="chat-time">{{ chat.time }}</span>
              </button>
              <button 
                class="delete-btn" 
                @click="$emit('delete-chat', chat.id)"
                title="删除对话"
              >
                <i class="fas fa-trash-alt"></i>
              </button>
            </div>
          </li>
        </ul>
      </div>
    </div>

    <div class="sidebar-footer">
      <button class="user-btn">
        <i class="fas fa-user"></i>
        <span>个人中心</span>
      </button>
      <button class="settings-btn">
        <i class="fas fa-cog"></i>
        <span>设置</span>
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ChatSidebar',
  props: {
    chatHistory: {
      type: Array,
      default: () => []
    },
    currentChatId: {
      type: String,
      default: ''
    }
  },
  emits: ['new-chat', 'select-chat', 'delete-chat']
}
</script>

<style scoped>
.chat-sidebar {
  width: 260px;
  height: 100vh;
  background-color: #fff;
  border-right: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid #e6e6e6;
}

.logo {
  width: 32px;
  height: 32px;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.new-chat-btn {
  padding: 16px;
}

.primary-btn {
  width: 100%;
  padding: 10px;
  background-color: #4f46e5;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.primary-btn:hover {
  background-color: #4338ca;
}

.sidebar-sections {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.section {
  margin-bottom: 24px;
}

.section h3 {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: #666;
}

.tool-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tool-item {
  width: 100%;
  padding: 8px 12px;
  background: none;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #333;
  transition: all 0.3s;
}

.tool-item:hover {
  background-color: #f5f5f5;
}

.tool-item i {
  width: 16px;
  color: #666;
}

.chat-history {
  list-style: none;
  padding: 0;
  margin: 0;
}

.history-item-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.history-item {
  flex: 1;
  padding: 8px 12px;
  background: none;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
  color: #333;
  transition: all 0.3s;
}

.history-item:hover {
  background-color: #f5f5f5;
}

.chat-title {
  font-size: 14px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  text-align: left;
}

.chat-time {
  font-size: 12px;
  color: #999;
}

.delete-btn {
  opacity: 0;
  padding: 6px;
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
  margin-right: 4px;
}

.history-item-wrapper:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background-color: #fee2e2;
  color: #ef4444;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #e6e6e6;
  display: flex;
  gap: 8px;
}

.user-btn,
.settings-btn {
  flex: 1;
  padding: 8px;
  background: none;
  border: 1px solid #e6e6e6;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #666;
  transition: all 0.3s;
}

.user-btn:hover,
.settings-btn:hover {
  background-color: #f5f5f5;
  border-color: #ccc;
}
</style> 