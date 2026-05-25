import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
// import vueDevTools from 'vite-plugin-vue-devtools'
import mkcert from 'vite-plugin-mkcert';


// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueJsx(),
    // vueDevTools(),
    mkcert(),
  ],
  server: {
    historyApiFallback: true, // This ensures unmatched routes fall back to index.html
    // proxy: {
    //   '/api': {
    //     target: 'http://localhost:8089', // Your backend URL
    //     changeOrigin: true,
    //     // rewrite: (path) => path.replace(/^\/api/, '') // Removes '/api' before sending to backend
    //   }
    // }
    // https: true,
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
})


