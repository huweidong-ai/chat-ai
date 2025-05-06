// src/main.js
import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import './assets/styles/icons.css';
import '@fortawesome/fontawesome-free/css/all.min.css';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import { useAuthStore } from './store/modules/auth';

const app = createApp(App);
const pinia = createPinia();

app.use(pinia);

// 初始化 auth store
const authStore = useAuthStore();
authStore.initAuth();

app.use(router);

app.use(ElementPlus);

app.mount('#app');



