import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss({
      preflight: false
    })
  ],
  server: {
    port: 3006, // <-- Optional: Sets the dev server port
    strictPort: true, // <-- Optional: Ensures the dev server fails if the port is already in use
    cors: true, // <-- Optional: Enables CORS for development
    proxy: {
      '/api': {
        target: 'http://localhost:3007',
        changeOrigin: true,
        configure: (proxy) => {
          proxy.on('proxyRes', (proxyRes, req, res) => {
            res.setHeader('X-Accel-Buffering', 'no');
            res.setHeader('Cache-Control', 'no-cache');
          });
        }
      }
    }
  },
  preview: {
    port: 3006 // <-- Optional: Sets the production preview port too
  }
})