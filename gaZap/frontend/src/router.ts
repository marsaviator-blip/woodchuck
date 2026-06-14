import { createRouter, createWebHistory } from 'vue-router';
import Dashboard from './views/HomeView.vue';
import Neo4jPanel from './views/Neo4jPanel.vue';
import SearchPanel from './views/SearchPanel.vue';
import ContainerStatus from './views/ContainerStatusPanel.vue';

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Dashboard },
    { path: '/neo4j', component: Neo4jPanel },
    { path: '/search', component: SearchPanel },
    { path: '/container-status', component: ContainerStatus },
  ]
});