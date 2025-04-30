import request from '@/utils/request';

/**
 * 上传单个文件
 * @param {File} file - 文件对象
 * @returns {Promise} - 上传结果
 */
export const uploadFile = async (file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  try {
    const response = await request({
      url: '/uploadFile',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    return {
      success: true,
      data: response.data,
      message: response.data.message || '文件上传成功！'
    };
  } catch (error) {
    return {
      success: false,
      message: error.message || '文件上传失败'
    };
  }
};

/**
 * 批量上传文件
 * @param {File[]} files - 文件数组
 * @returns {Promise} - 上传结果
 */
export const uploadFiles = async (files) => {
  const formData = new FormData();
  files.forEach((file, index) => {
    formData.append(`files[${index}]`, file);
  });
  
  try {
    const response = await request({
      url: '/uploadFile/batch',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    return {
      success: true,
      data: response.data,
      message: response.data.message || '文件批量上传成功！'
    };
  } catch (error) {
    return {
      success: false,
      message: error.message || '文件批量上传失败'
    };
  }
}; 