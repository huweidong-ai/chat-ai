import {getToken, handleUnauthorized} from './authService';

// 上传文件
export const uploadFile = async (file, isPreview = false) => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    if (isPreview !== undefined) {
      formData.append('isPreview', isPreview);
    }

    const response = await fetch('/uploadFile', {
      method: 'POST',
      headers: {
        // Authorization: `Bearer ${getToken()}`
        Authorization: getToken()
      },
      body: formData
    });

    if (response.status === 401) {
      handleUnauthorized();
      throw new Error('未授权，请重新登录');
    }

    if (!response.ok) {
      throw new Error('上传失败，请检查网络或服务器状态');
    }

    const data = await response.json();
    return {
      success: true,
      data,
      message: data.message || '文件上传成功！'
    };
  } catch (error) {
    return {
      success: false,
      message: error.message || '文件上传失败'
    };
  }
};

// 上传对话文件
export const uploadTopicueFile = async (file) => {
  return uploadFile(file, false);
};