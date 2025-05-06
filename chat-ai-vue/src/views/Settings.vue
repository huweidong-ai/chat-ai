<template>
  <div class="settings-page">
    <!-- 关闭按钮 -->
    <button class="close-btn" @click="closeSettings">
      <i class="fas fa-times"></i>
    </button>
    <!-- 个人信息卡片 -->
    <div class="profile-card">
      <img :src="user.avatar" class="avatar" />
      <div class="profile-info">
        <div class="username">{{ user.username }}</div>
        <div class="phone">{{ maskedPhone }}</div>
      </div>
    </div>

    <!-- 通用分组 -->
    <div class="group-title">通用</div>
    <div class="group-card">
      <div class="group-item">
        <i class="fas fa-sun"></i>
        <span>主题</span>
        <span class="item-action">浅色</span>
      </div>
      <div class="group-item">
        <i class="fas fa-language"></i>
        <span>Language</span>
        <span class="item-action">中文</span>
      </div>
    </div>

    <!-- 会话分组 -->
    <div class="group-title">会话</div>
    <div class="group-card">
      <div class="group-item">
        <i class="fas fa-cube"></i>
        <span>常用语</span>
        <span class="item-action">&gt;</span>
      </div>
      <div class="group-item">
        <i class="fas fa-book"></i>
        <span>自动展开参考网页</span>
        <input type="checkbox" checked />
      </div>
    </div>

    <!-- 关于我们分组 -->
    <div class="group-title">关于我们</div>
    <div class="group-card">
      <div class="group-item">
        <i class="fas fa-file-alt"></i>
        <span>用户协议</span>
      </div>
      <div class="group-item">
        <i class="fas fa-file-alt"></i>
        <span>隐私协议</span>
      </div>
    </div>

    <!-- 退出登录 -->
    <button class="logout-btn" @click="logout">
      <i class="fas fa-sign-out-alt"></i> 退出登录
    </button>
  </div>
</template>

<script>
import { useAuthStore } from '@/store/modules/auth';
import { useRouter } from 'vue-router';
import { computed } from 'vue';

export default {
  name: 'UserSettings',
  setup() {
    const authStore = useAuthStore();
    const router = useRouter();
    const user = authStore.user || {};
    // 手机号脱敏
    const maskedPhone = computed(() => {
      if (!user.phone) return user.email || '';
      return user.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    });
    const logout = () => {
      authStore.logout();
      router.push('/chat');
    };
    const closeSettings = () => {
      router.back();
    };
    return { user, logout, maskedPhone, closeSettings };
  }
};
</script>

<style scoped>
.settings-page {
  max-width: 520px;
  margin: 40px auto;
  padding: 0 16px 40px 16px;
}
.profile-card {
  display: flex;
  align-items: center;
  background: #f7f8fa;
  border-radius: 16px;
  box-shadow: 0 2px 8px 0 rgba(0,0,0,0.04);
  padding: 24px 32px;
  margin-bottom: 32px;
}
.avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #eee;
  margin-right: 24px;
}
.profile-info {
  flex: 1;
}
.username {
  font-size: 20px;
  font-weight: 600;
  color: #222;
  margin-bottom: 6px;
}
.phone {
  color: #888;
  font-size: 15px;
}
.group-title {
  font-size: 15px;
  color: #888;
  font-weight: 500;
  margin: 24px 0 8px 4px;
  letter-spacing: 1px;
}
.group-card {
  background: #f7f8fa;
  border-radius: 12px;
  margin-bottom: 8px;
  box-shadow: 0 1px 4px 0 rgba(0,0,0,0.03);
  padding: 0 8px;
}
.group-item {
  display: flex;
  align-items: center;
  padding: 18px 8px;
  border-bottom: 1px solid #ececec;
  font-size: 16px;
  color: #333;
}
.group-item:last-child {
  border-bottom: none;
}
.group-item i {
  width: 22px;
  text-align: center;
  margin-right: 14px;
  color: #bbb;
  font-size: 18px;
}
.item-action {
  margin-left: auto;
  color: #888;
  font-size: 15px;
}
.logout-btn {
  display: block;
  width: 100%;
  color: #e53e3e;
  background: #fbeaea;
  border: none;
  padding: 16px 0;
  border-radius: 12px;
  margin: 40px 0 0 0;
  cursor: pointer;
  font-size: 18px;
  font-weight: 500;
  letter-spacing: 2px;
  transition: background 0.2s;
}
.logout-btn:hover {
  background: #f8d7da;
}
.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  font-size: 22px;
  color: #999;
  cursor: pointer;
  z-index: 10;
}
.close-btn:hover {
  color: #333;
}
</style> 