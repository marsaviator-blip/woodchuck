import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/workspace', // Change this to your preferred URL path
      name: 'KmsWorkspace',
      component: () => import('../views/KmsWorkspace.vue') // <-- Ensure this path is correct
    }
  ]
})

export default router
