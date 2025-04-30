<template>
  <div class="chat-topic-list">
    <div class="topic-header">
      <h2><i class="el-icon-chat-line-round"></i> 对话列表</h2>
      <button @click="navigateToNewChat" class="new-topic-btn">
        <i class="el-icon-plus"></i> 新建对话
      </button>
    </div>
    <div class="topic-list">
      <div
        v-for="topic in topics"
        :key="topic.id"
        @click="selectTopic(topic.id)"
        :class="['topic-item', selectedTopicId === topic.id ? 'selected' : '']">
        <div class="topic-icon">
          <i class="el-icon-message"></i>
        </div>
        <div class="topic-content">
          <div class="topic-title">{{ topic.title }}</div>
          <div class="topic-date">{{ formatDate(topic.createdAt) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import topicService from '@/services/topicService';

export default {
  name: 'ChatTopicList',
  emits: ['select-topic', 'new-chat'],
  setup(props, { emit }) {
    const topics = ref([]);
    const selectedTopicId = ref(null);

    const loadTopics = () => {
      topics.value = topicService.getAllTopics();
      if (topics.value.length > 0) {
        selectedTopicId.value = topics.value[0].id;
        emit('select-topic', selectedTopicId.value);
      }
    };

    const navigateToNewChat = () => {
      selectedTopicId.value = null;
      emit('new-chat');
    };

    const selectTopic = (id) => {
      if (selectedTopicId.value !== id) {
        selectedTopicId.value = parseInt(id);
        emit('select-topic', selectedTopicId.value);
      }
    };

    const formatDate = (date) => {
      return new Date(date).toLocaleString();
    };

    loadTopics();

    return {
      topics,
      selectedTopicId,
      navigateToNewChat,
      selectTopic,
      formatDate
    };
  }
};
</script>

<style scoped>
.chat-topic-list {
  width: 280px;
  height: 100%;
  border-right: 1px solid #e6e6e6;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
}

.topic-header {
  padding: 20px;
  border-bottom: 1px solid #e6e6e6;
}

.topic-header h2 {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.new-topic-btn {
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  background-color: #6366f1;
  color: white;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.new-topic-btn:hover {
  background-color: #4f46e5;
  transform: translateY(-1px);
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.topic-list {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
}

.topic-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  margin-bottom: 8px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.topic-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background-color: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6366f1;
}

.topic-content {
  flex: 1;
  min-width: 0;
}

.topic-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.topic-date {
  font-size: 12px;
  color: #666;
}

.topic-item:hover {
  background-color: #f3f4f6;
}

.topic-item.selected .topic-icon {
  background-color: #6366f1;
  color: white;
}

.topic-item.selected .topic-title {
  color: #6366f1;
}
</style>