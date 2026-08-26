import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../views/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: MainLayout, // Wrapper holding your left navigation menu
      children: [
        {
          path: '',
          redirect: '/landscape' // Automatically jumps to your workspace
        },
        {
          path: 'workspace',
          name: 'KmsWorkspace',
          component: () => import('../views/KmsWorkspace.vue')
        },
        {
          path: 'landscape',
          name: 'DocumentLandscape',
          component: () => import('../views/DocumentLandscape.vue')
        }
      ]
    }
  ]
})

export default router

