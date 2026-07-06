import { createRouter, createWebHistory } from 'vue-router';
import Dashboard from './views/HomeView.vue';
import Neo4jPanel from './views/Neo4jPanel.vue';
import KnowledgeGraphPanel from './views/KnowledgeGraphPanel.vue';
import AuthorPanel from './views/AuthorPanel.vue';
import AuthorRelationPanel from './views/AuthorRelationPanel2.vue';
import SearchPanel from './views/SearchPanel.vue';
import SetupPanel from './views/SetupPanel.vue';
import SetupD3Panel from './views/SetupD3Panel.vue';
import SearchRelationshipPanel from './views/SearchRelationshipPanel.vue';
import ContainerStatus from './views/ContainerStatusPanel.vue';
import Instructions from './views/InstructionalPanel.vue';
import ContainerInstructionPanel from './views/ContainerInstructionPanel.vue';
import SystemDashboard from './views/SystemDashboard.vue';
import ChatClient from './views/ChatClient.vue';

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', component: Dashboard, meta: { useTailwind: false } },
    { path: '/neo4j', component: Neo4jPanel, meta: { useTailwind: false } },
    { path: '/knowledge-graph', component: KnowledgeGraphPanel, meta: { useTailwind: false } },
    { path: '/authors', component: AuthorPanel, meta: { useTailwind: false } },
    { path: '/authorsDocuments', component: AuthorRelationPanel, meta: { useTailwind: false } },
    { path: '/search', component: SearchPanel, meta: { useTailwind: false } },
    { path: '/scholarlySearch', component: SearchRelationshipPanel, meta: { useTailwind: false } },
    { path: '/container-status', component: ContainerStatus, meta: { useTailwind: false } },
    { path: '/setup', component: SetupPanel, meta: { useTailwind: false } },
    { path: '/setup-d3', component: SetupD3Panel, meta: { useTailwind: false } },
    { path: '/instructions', component: Instructions, meta: { useTailwind: false } },
    { path: '/container-instructions', component: ContainerInstructionPanel, meta: { useTailwind: false } },
    { path: '/system-design', component: SystemDashboard, meta: { useTailwind: true } }, 
    { path: '/chat', component: ChatClient, meta: { useTailwind: false } },
  ]
});