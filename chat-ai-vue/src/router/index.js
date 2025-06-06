// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/store/modules/auth';

const routes = [
    {
        path: '/',
        redirect: '/chat'
    },
    {
        path: '/chat',
        name: 'Chat',
        component: () => import('@/components/chat/ChatLayout.vue'),
    },
    {
        path: '/settings',
        name: 'Settings',
        component: () => import('@/views/Settings.vue'),
        meta: { requiresAuth: true }
      }
];

const router = createRouter({
    history: createWebHistory(),
    routes
});

// 全局路由守卫
router.beforeEach(async (to, from, next) => {
    const authStore = useAuthStore();
    
    // 如果路由需要认证
    if (to.meta.requiresAuth) {
        // 检查是否已登录
        if (!authStore.isLoggedIn) {
            // 如果没有登录，重定向到登录页
                next({
                name: 'Login',
                    query: { redirect: to.fullPath }
                });
                return;
        }
    }

    next();
});

export default router;



