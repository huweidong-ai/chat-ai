<!-- src/App.vue -->
<template>
  <div class="app-container" :class="{ 'nav-collapsed': isSidebarCollapsed }">
    <!-- 左侧导航栏 -->
    <aside class="sidebar" :class="{ 'collapsed': isSidebarCollapsed }">
      <div class="sidebar-content">
        <div class="ai-assistant">
          <div class="assistant-header">
            <img src="@/assets/ai-avatar.png" alt="AI助手" class="assistant-avatar" />
            <h2>AI助手</h2>
          </div>
          
          <button class="new-chat-btn">
            <i class="fas fa-plus"></i>
            新建对话
          </button>

          <div class="tools-section">
            <h3>效率工具</h3>
            <ul class="tools-list">
              <li><i class="fas fa-history"></i> 实时记录</li>
              <li><i class="fas fa-book"></i> 阅读助手</li>
              <li><i class="fas fa-file-powerpoint"></i> PPT制作</li>
            </ul>
          </div>

          <div class="history-section">
            <h3>历史对话</h3>
            <ul class="history-list">
              <li class="history-item active">
                <span>关于Vue3的讨论</span>
                <span class="time">10:30</span>
              </li>
              <li class="history-item">
                <span>AI助手使用指南</span>
                <span class="time">昨天</span>
              </li>
            </ul>
          </div>
        </div>

        <SidebarFooter />
      </div>
    </aside>
    
    <!-- 主内容区域 -->
    <div class="main-content">
      <div class="nav-toggle" @click="toggleSidebar">
        <i class="fas fa-bars"></i>
        <span class="toggle-text">{{ isSidebarCollapsed ? '展开导航' : '收起导航' }}</span>
      </div>
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import SidebarFooter from '@/components/sidebar/SidebarFooter.vue';

export default {
  name: 'App',
  components: {
    SidebarFooter
  },
  data() {
    return {
      isSidebarCollapsed: false
    }
  },
  methods: {
    toggleSidebar() {
      this.isSidebarCollapsed = !this.isSidebarCollapsed;
    }
  }
};
</script>

<style>

body {
  margin: 0;
  padding: 0;
  background-color: #f4f4f9;
  font-family: Arial, sans-serif;
  height: 100vh;
  overflow: hidden;
}

* {
  box-sizing: border-box;
}

.app-container {
  display: flex;
  height: 100vh;
  position: relative;
  transition: all 0.3s ease;
}

.sidebar {
  width: 260px;
  background: #fff;
  height: 100vh;
  transition: all 0.3s ease;
  border-right: 1px solid #eee;
  display: flex;
  flex-direction: column;
}

.sidebar.collapsed {
  width: 0;
  overflow: hidden;
}

.sidebar-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.ai-assistant {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.assistant-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.assistant-avatar {
  width: 40px;
  height: 40px;
  border-radius: 8px;
}

.assistant-header h2 {
  margin: 0;
  font-size: 18px;
  font-weight: 500;
}

.new-chat-btn {
  width: 100%;
  padding: 12px;
  background: #6366f1;
  color: white;
  border: none;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  margin-bottom: 24px;
  transition: background-color 0.2s;
}

.new-chat-btn:hover {
  background: #4f46e5;
}

.tools-section,
.history-section {
  margin-bottom: 24px;
}

.tools-section h3,
.history-section h3 {
  font-size: 14px;
  color: #666;
  margin-bottom: 12px;
  font-weight: 500;
}

.tools-list,
.history-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.tools-list li,
.history-item {
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
  margin-bottom: 4px;
}

.tools-list li:hover,
.history-item:hover {
  background: #f5f5f5;
}

.history-item {
  justify-content: space-between;
}

.history-item.active {
  background: #f0f0ff;
  color: #6366f1;
}

.time {
  font-size: 12px;
  color: #999;
}

.main-content {
  flex: 1;
  height: 100vh;
  overflow-y: auto;
  position: relative;
  transition: all 0.3s ease;
  padding: 20px;
  background: #fff;
}

.nav-toggle {
  position: fixed;
  top: 20px;
  left: 280px;
  height: 32px;
  display: flex;
  align-items: center;
  padding: 6px;
  background: transparent;
  border-radius: 6px;
  cursor: pointer;
  color: #666;
  transition: all 0.2s ease;
  border: none;
  gap: 8px;
  font-size: 13px;
  z-index: 1000;
}

.nav-collapsed .nav-toggle {
  left: 20px;
}

.nav-toggle i {
  font-size: 16px;
}

.nav-toggle .toggle-text {
  display: none;
  white-space: nowrap;
  font-weight: normal;
}

.nav-toggle:hover {
  background: #2d2d2d;
  color: #fff;
  padding: 6px 12px;
}

.nav-toggle:hover .toggle-text {
  display: inline-block;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #555;
}


</style>