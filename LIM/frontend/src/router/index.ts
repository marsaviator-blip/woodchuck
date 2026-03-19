import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/addinterest',
      name: 'addinterest',
      component: () => import('../views/interests/AddInterest.vue'), 
    },
    {
      path: '/modifyinterest',
      name: 'modifyinterest',
      component: () => import('../views/interests/ModifyInterest.vue'), 
    },
    {
      path: '/removeinterest',
      name: 'removeinterest',
      component: () => import('../views/interests/RemoveInterest.vue'), 
    },
    {
      path: '/viewinterest',
      name: 'viewinterest',
      component: () => import('../views/interests/ViewInterest.vue'), 
    },
    {
      path: '/basicmodel',
      name: 'basicmodel',
      component: () => import('../views/basicmodel/BasicModel.vue'),
    },
  ],
})

export default router
