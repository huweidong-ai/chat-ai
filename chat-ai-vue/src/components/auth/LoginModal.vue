<template>
  <div v-if="visible" class="modal-overlay" @click.self="handleClose">
    <div class="modal-content">
      <div class="modal-header">
        <h2>登录后</h2>
        <p class="subtitle">有问题，免费聊</p>
        <button class="close-btn" @click="handleClose">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="login-methods">
        <div class="login-section">
          <h3>微信扫码登录</h3>
          <div class="qr-code">
            <img :src="qrCodeUrl" alt="微信二维码" class="qr-image" />
          </div>
        </div>

        <div class="divider">
          <span>或</span>
        </div>

        <div class="login-section">
          <h3>手机号快捷登录</h3>
          <div class="phone-login">
            <div class="input-group">
              <div class="country-code">
                <span>+86</span>
                <i class="fas fa-chevron-down"></i>
              </div>
              <input 
                type="text" 
                v-model="phone" 
                placeholder="请输入手机号"
                class="phone-input" 
              />
            </div>
            <div class="input-group">
              <input 
                type="text" 
                v-model="verificationCode" 
                placeholder="请输入验证码"
                class="verification-input" 
              />
              <button 
                class="verification-btn"
                :disabled="!canSendCode || countdown > 0"
                @click="sendVerificationCode"
              >
                {{ countdown > 0 ? `${countdown}秒后重试` : '发送验证码' }}
              </button>
            </div>
            <button 
              class="login-btn"
              :disabled="!canLogin"
              @click="handleLogin"
            >
              登录
            </button>
          </div>
        </div>
      </div>

      <div class="terms">
        <label class="checkbox-label">
          <input type="checkbox" v-model="agreeToTerms">
          <span class="checkmark"></span>
          <span class="terms-text">
            已阅读并同意
            <a href="#" @click.prevent="showTerms('service')">《服务条款》</a>
            和
            <a href="#" @click.prevent="showTerms('privacy')">《隐私协议》</a>
          </span>
        </label>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import { useAuthStore } from '@/store/modules/auth';

export default {
  name: 'LoginModal',
  props: {
    visible: {
      type: Boolean,
      default: false
    }
  },
  emits: ['close', 'login-success'],
  setup(props, { emit }) {
    const authStore = useAuthStore();
    const phone = ref('');
    const verificationCode = ref('');
    const agreeToTerms = ref(false);
    const countdown = ref(0);
    const qrCodeUrl = ref(''); // TODO: 替换为实际的二维码URL

    // 验证手机号格式
    const isValidPhone = computed(() => {
      return /^1[3-9]\d{9}$/.test(phone.value);
    });

    // 是否可以发送验证码
    const canSendCode = computed(() => {
      return isValidPhone.value && agreeToTerms.value;
    });

    // 是否可以登录
    const canLogin = computed(() => {
      return isValidPhone.value && verificationCode.value.length === 6 && agreeToTerms.value;
    });

    // 发送验证码
    const sendVerificationCode = async () => {
      if (!canSendCode.value) return;
      try {
        await authStore.sendCode(phone.value);
        // 开始倒计时
        countdown.value = 60;
        const timer = setInterval(() => {
          countdown.value--;
          if (countdown.value <= 0) {
            clearInterval(timer);
          }
        }, 1000);
      } catch (error) {
        console.error('发送验证码失败:', error);
        // TODO: 显示错误提示
      }
    };

    // 处理登录
    const handleLogin = async () => {
      if (!canLogin.value) return;
      try {
        await authStore.login({
          phone: phone.value,
          code: verificationCode.value
        });
        emit('login-success');
        handleClose();
      } catch (error) {
        console.error('登录失败:', error);
        // TODO: 显示错误提示
      }
    };

    // 显示条款
    const showTerms = (type) => {
      // TODO: 显示服务条款或隐私协议
      console.log('Show terms:', type);
    };

    // 关闭弹窗
    const handleClose = () => {
      emit('close');
    };

    return {
      phone,
      verificationCode,
      agreeToTerms,
      countdown,
      qrCodeUrl,
      canSendCode,
      canLogin,
      sendVerificationCode,
      handleLogin,
      showTerms,
      handleClose
    };
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  width: 100%;
  max-width: 480px;
  position: relative;
}

.modal-header {
  text-align: center;
  margin-bottom: 24px;
}

.modal-header h2 {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
  color: #333;
}

.subtitle {
  font-size: 16px;
  color: #666;
  margin: 8px 0 0 0;
}

.close-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  padding: 4px;
}

.login-methods {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.login-section {
  text-align: center;
}

.login-section h3 {
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-bottom: 16px;
}

.qr-code {
  width: 180px;
  height: 180px;
  margin: 0 auto;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}

.qr-image {
  width: 160px;
  height: 160px;
}

.divider {
  position: relative;
  text-align: center;
  margin: 24px 0;
}

.divider::before,
.divider::after {
  content: '';
  position: absolute;
  top: 50%;
  width: calc(50% - 20px);
  height: 1px;
  background-color: #e6e6e6;
}

.divider::before {
  left: 0;
}

.divider::after {
  right: 0;
}

.divider span {
  background: white;
  padding: 0 10px;
  color: #999;
  font-size: 14px;
}

.phone-login {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.input-group {
  display: flex;
  gap: 8px;
}

.country-code {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 12px;
  border: 1px solid #e6e6e6;
  border-radius: 6px;
  color: #333;
  font-size: 14px;
  cursor: pointer;
}

.phone-input,
.verification-input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #e6e6e6;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.3s;
}

.phone-input:focus,
.verification-input:focus {
  border-color: #4f46e5;
}

.verification-btn {
  padding: 0 16px;
  border: 1px solid #4f46e5;
  border-radius: 6px;
  background: none;
  color: #4f46e5;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s;
}

.verification-btn:hover:not(:disabled) {
  background: #4f46e5;
  color: white;
}

.verification-btn:disabled {
  border-color: #e6e6e6;
  color: #999;
  cursor: not-allowed;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: #4f46e5;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-btn:hover:not(:disabled) {
  background: #4338ca;
}

.login-btn:disabled {
  background: #e6e6e6;
  cursor: not-allowed;
}

.terms {
  margin-top: 24px;
  text-align: center;
}

.checkbox-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  display: none;
}

.checkmark {
  width: 16px;
  height: 16px;
  border: 1px solid #e6e6e6;
  border-radius: 4px;
  position: relative;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #4f46e5;
  font-size: 12px;
}

.terms-text {
  font-size: 14px;
  color: #666;
}

.terms-text a {
  color: #4f46e5;
  text-decoration: none;
}

.terms-text a:hover {
  text-decoration: underline;
}
</style> 