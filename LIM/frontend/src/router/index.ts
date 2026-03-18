import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/formview',
      name: 'formview',
      component: () => import('../views/FormView.vue'), 
    },
    {
      path: '/addinterest',
      name: 'addinterest',
      component: () => import('../components/AddInterest.vue'), 
    },
  ],
})

export default router
