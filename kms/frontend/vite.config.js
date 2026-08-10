import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss()
  ],
  server: {
    port: 3006 // <-- Sets the development server port
  },
  preview: {
    port: 3006 // <-- Optional: Sets the production preview port too
  }
})
