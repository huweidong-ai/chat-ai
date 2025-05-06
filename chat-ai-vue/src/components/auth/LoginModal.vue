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

      <div class="login-methods horizontal">
        <div class="login-section">
          <h3>微信扫码登录</h3>
          <div class="qr-code">
            <img :src="qrCodeUrl" alt="微信二维码" class="qr-image" />
          </div>
        </div>

        <div class="divider-vertical">
          <span>或</span>
        </div>

        <div class="login-section">
          <h3>手机号登录</h3>
          <div class="phone-login">
            <div class="input-group">
              <div class="country-code">
                <span>+86</span>
                <i class="fas fa-chevron-down"></i>
              </div>
              <input
                type="text"
                v-model="phone"
                :class="['phone-input', phoneError ? 'error-input' : '']"
                placeholder="请输入手机号"
                @input="onPhoneInput"
                @blur="validatePhone"
                maxlength="11"
              />
            </div>
            <div v-if="phoneError" class="error-message">{{ phoneError }}</div>
            <div class="input-group">
              <input
                type="text"
                v-model="verificationCode"
                :class="['verification-input', codeError ? 'error-input' : '']"
                placeholder="请输入验证码"
                @input="onCodeInput"
                @blur="validateCode"
                maxlength="6"
              />
              <button
                class="verification-btn"
                :class="{ 'active-btn': isPhoneValid && !isCountingDown }"
                :disabled="!isPhoneValid || isCountingDown"
                @click="sendVerificationCode"
              >
                {{ countdownText }}
              </button>
            </div>
            <div v-if="codeError" class="error-message">{{ codeError }}</div>
            <button
              class="login-btn"
              :disabled="!canLogin"
              @click="handleLogin"
            >
              登录
            </button>
            <div v-if="showAgreementTip" class="agreement-tip">
              请勾选《用户协议与隐私政策》
            </div>
          </div>
        </div>
      </div>

      <div class="terms">
        <label class="checkbox-label">
          <input type="checkbox" v-model="agreeToTerms">
          <span class="checkmark"></span>
          <span class="terms-text">
            勾选即代表您阅读并同意
            <a href="#" @click.prevent="showTerms('service')">《用户协议》</a>
            与
            <a href="#" @click.prevent="showTerms('privacy')">《隐私政策》</a>
          </span>
        </label>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import { useAuthStore } from '@/store/modules/auth';
import { ElMessage } from 'element-plus';

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

    const phoneError = ref('');
    const codeError = ref('');

    const isPhoneValid = computed(() => /^1[3-9]\d{9}$/.test(phone.value));
    const isCountingDown = computed(() => countdown.value > 0);

    const validatePhone = () => {
      if (!phone.value) {
        phoneError.value = '请输入手机号';
      } else if (!isPhoneValid.value) {
        phoneError.value = '请输入正确格式的手机号';
      } else {
        phoneError.value = '';
      }
    };

    const validateCode = () => {
      if (!verificationCode.value) {
        codeError.value = '请输入验证码';
      } else if (verificationCode.value.length !== 6) {
        codeError.value = '请输入6位验证码';
      } else {
        codeError.value = '';
      }
    };

    const canLogin = computed(() =>
      isPhoneValid.value &&
      verificationCode.value.length === 6 &&
      agreeToTerms.value &&
      !phoneError.value &&
      !codeError.value
    );

    const showAgreementTip = computed(() =>
      isPhoneValid.value &&
      verificationCode.value.length === 6 &&
      !agreeToTerms.value
    );

    const countdownText = computed(() =>
      countdown.value > 0 ? `${countdown.value}秒后重试` : '获取验证码'
    );

    const sendVerificationCode = async () => {
      validatePhone();
      if (!isPhoneValid.value) return;
      try {
        const res = await authStore.sendCode(phone.value);
        if (res.code === 200) {
          countdown.value = 60;
          const timer = setInterval(() => {
            countdown.value--;
            if (countdown.value <= 0) {
              clearInterval(timer);
            }
          }, 1000);
        }
      } catch (error) {
        console.error('发送验证码失败:', error);
      }
    };

    const handleLogin = async () => {
      validatePhone();
      validateCode();
      if (!canLogin.value) return;
      try {
        const res = await authStore.login({
          phone: phone.value,
          code: verificationCode.value
        });
        if (res.code === 200) {
          ElMessage({
            message: `欢迎回来，${res.data.user.username}！`,
            type: 'success',
            duration: 3000
          });
          emit('login-success');
          handleClose();
        }
      } catch (error) {
        console.error('登录失败:', error);
        ElMessage({
          message: error.message || '登录失败',
          type: 'error',
          duration: 3000
        });
      }
    };

    const showTerms = () => {
      // TODO: 显示服务条款或隐私协议
    };

    const handleClose = () => {
      emit('close');
    };

    const onPhoneInput = (e) => {
      // 只允许输入数字，且最大11位
      phone.value = e.target.value.replace(/\D/g, '').slice(0, 11);
    };

    const onCodeInput = (e) => {
      verificationCode.value = e.target.value.replace(/\D/g, '').slice(0, 6);
    };

    return {
      phone,
      verificationCode,
      agreeToTerms,
      countdown,
      qrCodeUrl,
      phoneError,
      codeError,
      isPhoneValid,
      isCountingDown,
      canLogin,
      showAgreementTip,
      countdownText,
      sendVerificationCode,
      handleLogin,
      showTerms,
      handleClose,
      onPhoneInput,
      onCodeInput
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
  min-height: 100vh;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  border-radius: 18px;
  padding: 36px 32px 24px 32px;
  width: 720px;
  max-width: 98vw;
  margin: 0 16px;
  position: relative;
  box-shadow: 0 8px 32px 0 rgba(60, 60, 100, 0.18);
  display: flex;
  flex-direction: column;
  gap: 0;
}

.modal-header {
  text-align: center;
  margin-bottom: 18px;
}

.modal-header h2 {
  font-size: 26px;
  font-weight: 700;
  margin: 0;
  color: #222;
  letter-spacing: 1px;
}

.subtitle {
  font-size: 15px;
  color: #aaa;
  margin: 8px 0 0 0;
  font-weight: 400;
}

.close-btn {
  position: absolute;
  top: 18px;
  right: 18px;
  background: none;
  border: none;
  font-size: 22px;
  color: #bbb;
  cursor: pointer;
  z-index: 10;
  transition: color 0.2s;
}
.close-btn:hover {
  color: #6366f1;
}

.login-methods.horizontal {
  display: flex;
  flex-direction: row;
  gap: 0;
  align-items: stretch;
  margin-bottom: 0;
  background: none;
}

.login-section {
  flex: 1;
  min-width: 0;
  padding: 0 18px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.login-section h3 {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 18px;
  text-align: center;
}

.qr-code {
  width: 180px;
  height: 180px;
  margin: 0 auto;
  background: #f7f8fa;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 14px;
  border: 1.5px solid #ececec;
  box-shadow: 0 2px 8px 0 rgba(60,60,100,0.04);
}

.qr-image {
  width: 150px;
  height: 150px;
  object-fit: contain;
}

.divider-vertical {
  width: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}
.divider-vertical span {
  writing-mode: vertical-lr;
  color: #bbb;
  font-size: 14px;
  background: #fff;
  padding: 0 2px;
  z-index: 1;
  font-weight: 500;
}
.divider-vertical::before {
  content: '';
  position: absolute;
  left: 50%;
  top: 0;
  width: 1.5px;
  height: 100%;
  background: #ececec;
  transform: translateX(-50%);
  z-index: 0;
  border-radius: 1px;
}

.phone-login {
  display: flex;
  flex-direction: column;
  gap: 14px;
  width: 100%;
  align-items: center;
}

.input-group {
  display: flex;
  gap: 8px;
  width: 100%;
}

.country-code {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 0 12px;
  border: 1.5px solid #e6e6e6;
  border-radius: 10px;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  background: #fafbfc;
  height: 40px;
}

.phone-input,
.verification-input {
  flex: 1;
  padding: 10px 14px;
  border: 1.5px solid #e6e6e6;
  border-radius: 10px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.3s;
  background: #fafbfc;
  height: 40px;
}

.phone-input:focus,
.verification-input:focus {
  border-color: #6366f1;
}

.error-input {
  border-color: #e53e3e !important;
}

.error-message {
  color: #e53e3e;
  font-size: 13px;
  margin-top: 2px;
  text-align: left;
  width: 100%;
}

.agreement-tip {
  color: #e53e3e;
  font-size: 13px;
  margin-top: 8px;
  text-align: left;
  width: 100%;
}

.login-btn {
  width: 100%;
  padding: 12px;
  background: #6366f1;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s, box-shadow 0.2s;
  font-weight: 600;
  box-shadow: 0 2px 8px 0 rgba(60,60,100,0.04);
}

.login-btn:hover:not(:disabled) {
  background: #4f46e5;
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
  width: 18px;
  height: 18px;
  border: 1.5px solid #e6e6e6;
  border-radius: 5px;
  position: relative;
  background: #fafbfc;
}

.checkbox-label input[type="checkbox"]:checked + .checkmark::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #6366f1;
  font-size: 13px;
}

.terms-text {
  font-size: 14px;
  color: #888;
}

.terms-text a {
  color: #6366f1;
  text-decoration: none;
}

.terms-text a:hover {
  text-decoration: underline;
}

.verification-btn {
  width: 120px;
  min-width: 100px;
  max-width: 140px;
  box-sizing: border-box;
  padding: 0 16px;
  border: 1.5px solid #e6e6e6;
  border-radius: 10px;
  background: none;
  color: #999;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.3s;
  height: 40px;
  font-weight: 500;
}

.verification-btn.active-btn {
  border: 1.5px solid #6366f1;
  color: #6366f1;
  background: #f5f7ff;
}

.verification-btn.active-btn:hover:not(:disabled) {
  background: #6366f1;
  color: #fff;
}

.verification-btn:disabled {
  border-color: #e6e6e6;
  color: #999;
  cursor: not-allowed;
  background: none;
}

@media (max-width: 900px) {
  .modal-content {
    width: 98vw;
    padding: 18px 2vw 12px 2vw;
  }
  .login-methods.horizontal {
    flex-direction: column;
  }
  .divider-vertical {
    width: 100%;
    height: 32px;
    min-height: 32px;
    max-width: 100%;
    justify-content: center;
    align-items: center;
  }
  .divider-vertical span {
    writing-mode: horizontal-tb;
    padding: 0 10px;
  }
  .login-section {
    padding: 0;
  }
}
</style> 