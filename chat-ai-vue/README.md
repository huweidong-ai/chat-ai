# chat-ai-vue

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).

### 项目结构如下
```
my-vue-project/
├── public/         # 静态资源（这里的文件会被原封不动地复制到构建后的目录中）
├── src/
│   ├── assets/     # 静态资源（这里的文件会被编译成 URL）
│   ├── components/ # 可复用组件
│   │   └── ChatLayout.vue # 可复用的聊天组件
│   ├── config/
│   │   └── constant.js  # API 配置（包括接口地址、请求方法等）
│   ├── router/         # 路由配置
│   │   └── index.js    # 路由配置（映射 views 中的组件）
│   ├── services/       # 服务层（处理业务逻辑）
│   │   └── topicService.js
│   ├── views/          # 页面级组件（路由直接指向的）
│   │   └── Login.vue   # 登录页（完整业务场景）
│   ├── App.vue
│   └── main.js
├── .env.development    # 环境变量配置
├── .env.production     # 环境变量配置
├── .env.testing        # 环境变量配置
├── package.json        # 项目配置（包含项目依赖、脚本命令等信息）
└── vue.config.js       # Vue 配置，可以配置构建、开发服务器等选项
```
### 前端结构
+------------------------+
|        Header         |
+--------+-------------+
|        |             |
|        |             |
|Sidebar | ChatWindow  |
|        |             |
|        |             |
|        +-------------+
|        | ChatInput   |
+--------+-------------+

### 项目特点
1. 采用了标准的 Vue CLI 项目结构
2. 使用组件化开发，提高代码复用性
3. 清晰的职责分离：
   - 视图层（views、components）
   - 业务逻辑层（services）
   - 配置层（config）
   - 路由管理（router）
4. 支持多环境部署（开发、测试、生产）