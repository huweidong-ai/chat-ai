<template>
  <div class="chat-input-container">
    <div class="input-area">
      <!-- 文件预览区域 -->
      <div v-if="attachments.length" class="file-preview-area">
        <div class="file-preview-scroll" ref="previewScroll">
          <div v-for="(file, index) in attachments" :key="index" class="file-preview-item">
            <div class="file-icon">
              <i :class="getFileIcon(file.type)"></i>
            </div>
            <div class="file-info">
              <span class="file-name">{{ file.name }}</span>
              <span class="file-size">{{ formatFileSize(file.size) }}</span>
            </div>
            <button class="remove-file" @click="removeFile(index)">
              <i class="fas fa-times"></i>
            </button>
          </div>
        </div>
      </div>

      <textarea
        v-model="message"
        @keydown="handleKeyDown"
        placeholder="输入消息，Enter 发送，Shift + Enter 换行..."
        class="message-input"
        ref="messageInput"
        :maxlength="10000"
      ></textarea>
      <div class="char-count" :class="{ 'limit-reached': isAtLimit }">
        {{ message.length }}/10000
      </div>
    </div>
    
    <div class="toolbar">
      <div class="left-tools">
        <button 
          class="model-btn" 
          :class="{ active: selectedModels.includes('deep') }"
          @click="toggleModel('deep')"
        >
          <i class="fas fa-brain"></i>
          深度思考 (R1)
        </button>
        <button 
          class="model-btn" 
          :class="{ active: selectedModels.includes('search') }"
          @click="toggleModel('search')"
        >
          <i class="fas fa-globe"></i>
          联网搜索
        </button>
      </div>
      <div class="right-tools">
        <div class="tooltip-container">
          <button class="tool-btn" @click="triggerFileUpload">
            <i class="fas fa-paperclip"></i>
            <span v-if="attachments.length" class="file-count">{{ attachments.length }}</span>
          </button>
          <div class="tooltip">
            <div class="tooltip-content">
              支持上传文件<br/>
              (最多 50 个，每个 100 MB)<br/>
              接受 pdf、doc、xlsx、ppt、txt、图片等
            </div>
          </div>
        </div>
        <button 
          @click="handleSend" 
          :disabled="!message.trim() && !attachments.length"
          class="send-button"
        >
          发送 <i class="fas fa-paper-plane"></i>
        </button>
      </div>
    </div>

    <div class="disclaimer">
      所有产出内容均源自人工智能模型，其内容的精确度和全面性不能确保，不反映我们的立场或看法。
    </div>

    <!-- Hidden file input -->
    <input
      type="file"
      ref="fileInput"
      style="display: none"
      @change="handleFileSelect"
      multiple
      accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.txt,.jpg,.jpeg,.png,.gif,.bmp,.webp"
    />
  </div>
</template>

<script>
import { ref, computed, onMounted, nextTick } from 'vue';

export default {
  name: 'ChatInput',
  
  props: {
    isSidebarCollapsed: {
      type: Boolean,
      default: false
    }
  },
  
  setup(props, { emit }) {
    const message = ref('');
    const attachments = ref([]);
    const fileInput = ref(null);
    const messageInput = ref(null);
    const previewScroll = ref(null);
    const canScrollLeft = ref(false);
    const canScrollRight = ref(false);
    const selectedModels = ref(['search']); // 可以包含 'deep' 和 'search'
    
    const MAX_FILES = 50;
    const MAX_FILE_SIZE = 100 * 1024 * 1024; // 100MB
    const ALLOWED_FILE_TYPES = {
      // 文档类型
      'application/pdf': '.pdf',
      'application/msword': '.doc',
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document': '.docx',
      'application/vnd.ms-excel': '.xls',
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet': '.xlsx',
      'application/vnd.ms-powerpoint': '.ppt',
      'application/vnd.openxmlformats-officedocument.presentationml.presentation': '.pptx',
      'text/plain': '.txt',
      // 图片类型
      'image/jpeg': '.jpg,.jpeg',
      'image/png': '.png',
      'image/gif': '.gif',
      'image/bmp': '.bmp',
      'image/webp': '.webp'
    };

    const isAtLimit = computed(() => {
      return message.value.length >= 10000;
    });

    const handleKeyDown = (e) => {
      if (e.key === 'Enter') {
        if (e.shiftKey) {
          // Allow default behavior for Shift+Enter (new line)
          return;
        }
        e.preventDefault();
        handleSend();
      }
    };
    
    const handleSend = () => {
      if (!message.value.trim() && !attachments.value.length) return;
      
      emit('send', {
        text: message.value,
        attachments: attachments.value
      });
      
      // Clear input and attachments
      message.value = '';
      attachments.value = [];
      
      // Focus back on input
      messageInput.value?.focus();
    };
    
    const triggerFileUpload = () => {
      fileInput.value?.click();
    };
    
    const validateFile = (file) => {
      // 检查文件类型
      if (!Object.keys(ALLOWED_FILE_TYPES).includes(file.type)) {
        const allowedExtensions = [...new Set(Object.values(ALLOWED_FILE_TYPES).join(',').split(','))].join(', ');
        return {
          valid: false,
          message: `不支持的文件类型，请上传以下格式的文件：${allowedExtensions}`
        };
      }

      // 检查文件大小
      if (file.size > MAX_FILE_SIZE) {
        return {
          valid: false,
          message: `文件大小不能超过 100MB，当前文件大小：${formatFileSize(file.size)}`
        };
      }

      // 检查文件总数
      if (attachments.value.length >= MAX_FILES) {
        return {
          valid: false,
          message: `最多只能上传 ${MAX_FILES} 个文件`
        };
      }

      return { valid: true };
    };
    
    const handleFileSelect = async (event) => {
      const files = Array.from(event.target.files || []);
      if (files.length > 0) {
        // 检查是否超过最大文件数
        const remainingSlots = MAX_FILES - attachments.value.length;
        if (remainingSlots <= 0) {
          event.target.value = '';
          return;
        }

        // 过滤重复文件和验证文件
        const newFiles = files.filter(file => {
          // 检查是否已存在相同的文件（通过文件名和大小判断）
          const isDuplicate = attachments.value.some(existingFile => 
            existingFile.name === file.name && existingFile.size === file.size
          );
          
          if (isDuplicate) {
            return false;
          }

          // 验证文件
          const validation = validateFile(file);
          return validation.valid;
        });

        // 只添加不超过剩余槽位数量的文件
        const filesToAdd = newFiles.slice(0, remainingSlots);
        
        // 添加新文件到附件列表
        filesToAdd.forEach(file => {
          attachments.value.push({
            name: file.name,
            size: file.size,
            type: file.type,
            file: file
          });
        });

        event.target.value = ''; // 重置文件输入
      }
      await nextTick();
      checkScroll();
    };

    const removeFile = (index) => {
      attachments.value.splice(index, 1);
    };

    const getFileIcon = (fileType) => {
      if (fileType.includes('pdf')) return 'fas fa-file-pdf';
      if (fileType.includes('word') || fileType.includes('document')) return 'fas fa-file-word';
      if (fileType.includes('excel') || fileType.includes('sheet')) return 'fas fa-file-excel';
      if (fileType.includes('powerpoint') || fileType.includes('presentation')) return 'fas fa-file-powerpoint';
      if (fileType.includes('text/plain')) return 'fas fa-file-alt';
      if (fileType.includes('image/')) return 'fas fa-file-image';
      return 'fas fa-file';
    };

    const formatFileSize = (bytes) => {
      if (bytes === 0) return '0 B';
      const k = 1024;
      const sizes = ['B', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    };
    
    const checkScroll = () => {
      if (!previewScroll.value) return;
      const { scrollLeft, scrollWidth, clientWidth } = previewScroll.value;
      canScrollLeft.value = scrollLeft > 0;
      canScrollRight.value = scrollLeft + clientWidth < scrollWidth;
    };

    const scrollPreview = (direction) => {
      if (!previewScroll.value) return;
      const scrollAmount = 200;
      const currentScroll = previewScroll.value.scrollLeft;
      previewScroll.value.scrollTo({
        left: direction === 'left' ? currentScroll - scrollAmount : currentScroll + scrollAmount,
        behavior: 'smooth'
      });
    };

    const toggleSidebar = () => {
      emit('toggle-sidebar');
    };

    const toggleModel = (model) => {
      const index = selectedModels.value.indexOf(model);
      if (index === -1) {
        selectedModels.value.push(model);
      } else {
        selectedModels.value.splice(index, 1);
      }
    };

    onMounted(() => {
      if (previewScroll.value) {
        previewScroll.value.addEventListener('scroll', checkScroll);
        checkScroll();
      }
    });

    return {
      message,
      attachments,
      fileInput,
      messageInput,
      previewScroll,
      canScrollLeft,
      canScrollRight,
      isAtLimit,
      selectedModels,
      toggleModel,
      handleKeyDown,
      handleSend,
      triggerFileUpload,
      handleFileSelect,
      removeFile,
      getFileIcon,
      formatFileSize,
      scrollPreview,
      toggleSidebar,
    };
  }
};
</script>

<style scoped>
.chat-input-container {
  background: #fff;
  border-top: 1px solid #e6e6e6;
  position: relative;
}

.input-area {
  position: relative;
  padding: 12px 16px;
}

.message-input {
  width: 100%;
  min-height: 24px;
  max-height: 150px;
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  outline: none;
  resize: none;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
  background: #fff;
}

.message-input::placeholder {
  color: #9ca3af;
}

.char-count {
  position: absolute;
  bottom: 8px;
  right: 16px;
  font-size: 12px;
  color: #999;
}

.char-count.limit-reached {
  color: #f56c6c;
  font-weight: 500;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  background: #fff;
}

.left-tools,
.right-tools {
  display: flex;
  align-items: center;
  gap: 8px;
}

.model-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border: none;
  background: rgba(45, 45, 45, 0.3);
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
  backdrop-filter: blur(8px);
}

.model-btn i {
  font-size: 14px;
}

.model-btn:hover {
  background: rgba(45, 45, 45, 0.5);
  color: #fff;
}

.model-btn.active {
  background: #4f46e5;
  color: #fff;
}

.model-btn.active:hover {
  background: #4338ca;
}

.tool-btn {
  position: relative;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 6px;
  border: none;
  background: none;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.tool-btn:hover {
  background: #f5f5f5;
}

.send-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 16px;
  border: none;
  background: #4f46e5;
  color: white;
  font-size: 14px;
  font-weight: 500;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.send-button:hover {
  background: #4338ca;
}

.send-button:disabled {
  background: #e5e7eb;
  cursor: not-allowed;
}

.send-button i {
  font-size: 14px;
}

.disclaimer {
  padding: 8px 16px;
  background: #f8f9fa;
  color: #666;
  font-size: 12px;
  text-align: center;
  border-top: 1px solid #eee;
}

.tooltip-container {
  position: relative;
  display: inline-block;
}

.tooltip {
  display: none;
  position: absolute;
  bottom: calc(100% + 10px);
  left: 50%;
  transform: translateX(-50%);
  background: #2d2d2d;
  color: white;
  font-size: 13px;
  border-radius: 6px;
  padding: 12px 16px;
  white-space: nowrap;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  width: max-content;
  text-align: center;
  line-height: 1.6;
}

.tooltip::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  border-width: 6px 6px 0;
  border-style: solid;
  border-color: #2d2d2d transparent transparent transparent;
}

.tooltip-container:hover .tooltip {
  display: block;
}

.tooltip-content {
  white-space: normal;
  max-width: 240px;
}

.file-count {
  position: absolute;
  top: -6px;
  right: -6px;
  background: #4f46e5;
  color: white;
  font-size: 12px;
  min-width: 16px;
  height: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
}

.file-preview-area {
  margin: 8px 0;
  padding: 0 16px;
}

.file-preview-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 4px 0;
  scrollbar-width: thin;
  -ms-overflow-style: none;
}

.file-preview-scroll::-webkit-scrollbar {
  height: 4px;
}

.file-preview-scroll::-webkit-scrollbar-track {
  background: transparent;
}

.file-preview-scroll::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.2);
  border-radius: 2px;
}

.file-preview-item {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  background: #f8f9fa;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  min-width: 200px;
  max-width: 280px;
  position: relative;
}

.file-icon {
  margin-right: 12px;
  color: #666;
}

.file-icon i {
  font-size: 20px;
}

.file-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.file-name {
  font-size: 13px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.file-size {
  font-size: 12px;
  color: #666;
}

.remove-file {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 16px;
  height: 16px;
  border: none;
  background: rgba(0, 0, 0, 0.1);
  color: #666;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 0;
  font-size: 10px;
  transition: all 0.2s;
}

.remove-file:hover {
  background: rgba(0, 0, 0, 0.2);
  color: #333;
}
</style> 