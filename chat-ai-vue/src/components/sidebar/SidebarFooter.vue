<template>
  <div class="sidebar-footer">
    <div class="login-section">
      <div class="user-info" v-if="isLoggedIn" @click="goToSettings" style="cursor:pointer;">
        <img :src="userAvatar" alt="用户头像" class="user-avatar" />
        <span class="username">{{ username }}</span>
      </div>
      <button v-else class="login-btn" @click="showLoginModal">
        <i class="fas fa-user"></i>
        <span>登录</span>
      </button>
      <button class="expand-btn" @click="toggleMenu">
        <i :class="['fas', isExpanded ? 'fa-chevron-down' : 'fa-chevron-up']"></i>
      </button>
    </div>
    <div class="expanded-menu" :class="{ 'expanded': isExpanded }">
      <!-- <button class="menu-item">
        <i class="fas fa-info-circle"></i>
        关于我们
      </button> -->
      <button class="menu-item" @click="goToSettings">
        <i class="fas fa-user-cog"></i>
        个人设置
      </button>
      <button class="menu-item">
        <i class="fas fa-comment-alt"></i>
        用户反馈
      </button>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import { useAuthStore } from '@/store/modules/auth';
import { useRouter } from 'vue-router';

export default {
  name: 'SidebarFooter',
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    const isExpanded = ref(false);

    const isLoggedIn = computed(() => authStore.isLoggedIn);
    const username = computed(() => authStore.user?.username || '');
    const userAvatar = computed(() => authStore.user?.avatar || '');

    const toggleMenu = () => {
      isExpanded.value = !isExpanded.value;
    };

    const showLoginModal = () => {
      authStore.showLoginModal = true;
    };

    const handleLogout = () => {
      authStore.logout();
    };

    const goToSettings = () => {
      router.push('/settings');
    };

    return {
      isLoggedIn,
      username,
      userAvatar,
      isExpanded,
      toggleMenu,
      showLoginModal,
      handleLogout,
      goToSettings
    };
  }
};
</script>

<style scoped>
.sidebar-footer {
  padding: 0;
  border-top: 1px solid #eee;
  background: #fff;
}

.login-section {
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
  padding: 12px 16px;
  gap: 8px;
}

.user-info {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.username {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.login-btn {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: none;
  border: none;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.3s;
}

.login-btn:hover {
  background: #f5f5f5;
  border-radius: 6px;
}

.expand-btn {
  width: 32px;
  height: 32px;
  padding: 0;
  border: none;
  background: none;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  border-radius: 4px;
}

.expand-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.expand-btn i {
  font-size: 12px;
}

.expanded-menu {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.3s ease;
  background: #f8f9fa;
}

.expanded-menu.expanded {
  max-height: 200px;
}

.menu-item {
  width: 100%;
  padding: 12px 16px;
  border: none;
  background: none;
  display: flex;
  align-items: center;
  gap: 12px;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.menu-item:hover {
  background: #f0f0f0;
  color: #333;
}

.menu-item i {
  width: 16px;
  text-align: center;
}

.menu-item.logout {
  color: #e53e3e;
}

.menu-item.logout:hover {
  background: #fbeaea;
}
</style> 