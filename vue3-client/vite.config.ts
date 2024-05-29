import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    port: 10001,
    host: '0.0.0.0',
    // 配置代理  
    proxy: {  
      // 示例：将所有以 /api 开头的请求代理
      '/api': {  
        target: 'http://localhost:11451',  
        changeOrigin: true, // 允许跨域  
        rewrite: (path) => path.replace(/^\/api/, ''), // 移除前缀  
      },  
      // 你可以添加更多代理规则...  
    },  
  },
})
