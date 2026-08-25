import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  server: {
    port: 8081,
    host: '0.0.0.0', // 解决 use --host to expose问题
    hmr: true, // 热更新
    // 自动打开浏览器：指定用 Chrome（而不是系统默认的 Edge）
    open: true,
    proxy: {
      '/api': {
        target: 'http://localhost:8089', //后端地址：ychs-base-parent(IDEA中启动)，端口8089
        // 使用EasyMock地址
        // target: 'https://www.fastmock.site/mock/8c70dce529c6c4880cfe889aa62b4103/api',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      },
      // 审批表/扫描件等上传文件：后端把本地 upload 目录映射到 /images/**（已在鉴权白名单放行，无需 token），
      // 这里同样代理到后端。否则点击"查看PDF"打开 /images/xxx 会落到 SPA 的 index.html，
      // 触发路由守卫把新标签页重定向到登录页。
      '/images': {
        target: 'http://localhost:8089',
        changeOrigin: true
      }
    }

  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
})
