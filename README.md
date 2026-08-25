# 律所管理系统

基于前后端分离架构的律师事务所管理系统，包含用户认证、公告管理、律师文档上传等功能。

## 项目简介

本系统面向律师事务所的日常业务管理，采用 **Vue 3 + Spring Boot** 前后端分离架构。前端负责页面交互，后端提供 RESTful API 并负责数据持久化与鉴权。

## 项目结构

```
├── vue/                 # 前端（Vue 3 + Vite + TypeScript）
└── ychs-base-parent/    # 后端（Spring Boot 多模块 Maven 项目）
    ├── ychs-base-common/  # 公共模块（工具类、通用组件）
    └── ychs-base-web/     # Web 主模块（Controller、Service、Mapper）
```

## 技术栈

### 前端
- **Vue 3** + **Vite** + **TypeScript**
- **Element Plus**（UI 组件库）
- **Pinia**（状态管理）+ **Vue Router**
- **Axios**（HTTP 请求）+ **ECharts**（数据可视化）

### 后端
- **Spring Boot 3.1**（Java 17）
- **MyBatis-Plus**（ORM）
- **MySQL**（数据库）+ **Druid**（连接池）
- **JWT**（身份认证）

## 快速开始

### 环境要求
- JDK 17+、Maven 3.6+
- Node.js 18+
- MySQL 8+

### 后端启动
1. 复制 `ychs-base-web/src/main/resources/application-example.yml` 为 `application-test.yml`（或 `application-active.yml`），填入真实数据库密码与密钥
2. 导入 `ychs-base(1).sql` 初始化数据库
3. 启动 `ychs-base-web` 模块

### 前端启动
```bash
cd vue
npm install
npm run dev
```

> 访问地址：前端 `http://localhost:5173`，后端 `http://localhost:8089`
