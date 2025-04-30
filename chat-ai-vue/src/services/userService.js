import request from '@/utils/request';

export const userService = {
  // 登录
  login(data) {
    return request({
      url: '/login',
      method: 'post',
      data
    });
  },

  // 注册
  register(data) {
    return request({
      url: '/register',
      method: 'post',
      data
    });
  }
};