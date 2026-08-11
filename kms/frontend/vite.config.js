import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss({
      prefligh: false
    })
  ],
  server: {
    port: 3006 // <-- Sets the development server port
  },
  preview: {
    port: 3006 // <-- Optional: Sets the production preview port too
  }
})
import { defineConfig } from 'vite'
import vue from '@vitejs/vue'
import tailwindcss from '@tailwindcss/vite' // [1] Ensure this import exists

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(), // [2] Must be placed in the plugins array
  ],
})
