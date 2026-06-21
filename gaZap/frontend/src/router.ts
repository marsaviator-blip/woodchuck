import { createRouter, createWebHistory } from 'vue-router';
import Dashboard from './views/HomeView.vue';
import Neo4jPanel from './views/Neo4jPanel.vue';
import KnowledgeGraphPanel from './views/KnowledgeGraphPanel.vue';
import AuthorPanel from './views/AuthorPanel.vue';
import SearchPanel from './views/SearchPanel.vue';
import SetupPanel from './views/SetupPanel.vue';
import SetupD3Panel from './views/SetupD3Panel.vue';
import SearchRelationshipPanel from './views/SearchRelationshipPanel.vue';
import ContainerStatus from './views/ContainerStatusPanel.vue';
import Instructions from './views/InstructionalPanel.vue';
import ContainerInstructionPanel from './views/ContainerInstructionPanel.vue';

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Dashboard },
    { path: '/neo4j', component: Neo4jPanel },
    { path: '/knowledge-graph', component: KnowledgeGraphPanel },
    { path: '/authors', component: AuthorPanel },
    { path: '/search', component: SearchPanel },
    { path: '/scholarlySearch', component: SearchRelationshipPanel },
    { path: '/container-status', component: ContainerStatus },
    { path: '/setup', component: SetupPanel },
    { path: '/setup-d3', component: SetupD3Panel },
    { path: '/instructions', component: Instructions },
    { path: '/container-instructions', component: ContainerInstructionPanel },
  ]
});