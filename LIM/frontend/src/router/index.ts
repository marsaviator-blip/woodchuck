import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/formview',
      name: 'formview',
      component: () => import('../views/FormView.vue'), 
    },
  ],
})

export default router
