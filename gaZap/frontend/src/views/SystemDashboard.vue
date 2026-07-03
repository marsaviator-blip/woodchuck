<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import SystemNodeComponent from './SystemNode.vue';

interface SystemNode {
  id: string;
  label: string;
  type: 'client' | 'gateway' | 'controller' | 'service' | 'activity' | 'database';
  x: number;
  y: number;
}

interface Connection {
  id: string;
  from: string;
  to: string;
}

interface UseCase {
  id: string;
  title: string;
  description: string;
  activeNodes: string[];
  activeConnections: string[];
}

interface LogEntry {
  id: string;
  timestamp: string;
  message: string;
  type: 'info' | 'success' | 'warn';
}

const nodes = ref<SystemNode[]>([
  { id: 'client_web', label: 'Web Application', type: 'client', x: 10, y: 10 },
  { id: 'client_mobile', label: 'Mobile App', type: 'client', x: 50, y: 10 },
  { id: 'api_gw', label: 'API Gateway', type: 'gateway', x: 30, y: 25 },
  { id: 'auth_svc', label: 'Auth Service', type: 'service', x: 15, y: 25 },
  { id: 'search_contr', label: 'SearchController', type: 'controller', x: 10, y: 40 },
  { id: 'doc_contr', label: 'Doc Controller', type: 'controller', x: 30, y: 40 },
  { id: 'search_svc', label: 'Search Service', type: 'service', x: 10, y: 55 },
  { id: 'doc_svc', label: 'Document Workflow Service', type: 'service', x: 45, y: 55 },
  { id: 'light_act', label: 'Light Analysis', type: 'activity', x: 30, y: 75 },
  { id: 'deep_act', label: 'Deep Analysis', type: 'activity', x: 45, y: 75 },
  { id: 'embeddings', label: 'Embeddings', type: 'activity', x: 55, y: 75 },
  { id: 'vectorstore', label: 'VectorStore', type: 'activity', x: 65, y: 75 },
  { id: 'cache_db', label: 'Redis Cache', type: 'database', x: 25, y: 90 },
  { id: 'main_db', label: 'PostgreSQL', type: 'database', x: 35, y: 90 },
  { id: 'graph_db', label: 'Neo4J', type: 'database', x: 45, y: 90 },
  { id: 'semantic_db', label: 'OpenSearch', type: 'database', x: 60, y: 90 },
]);

const connections = ref<Connection[]>([
  { id: 'web_to_gw', from: 'client_web', to: 'api_gw' },
  { id: 'mobile_to_gw', from: 'client_mobile', to: 'api_gw' },
  { id: 'gw_to_contr', from: 'api_gw', to: 'search_contr' },
  { id: 'contr_to_svc', from: 'search_contr', to: 'search_svc' },
  { id: 'gw_to_auth', from: 'api_gw', to: 'auth_svc' },
  { id: 'gw_to_search_contr', from: 'api_gw', to: 'search_contr' },
  { id: 'gw_to_doc_contr', from: 'api_gw', to: 'doc_contr' },
  { id: 'search_contr_to_search_svc', from: 'search_contr', to: 'search_svc' },
  { id: 'doc_contr_to_doc_svc', from: 'doc_contr', to: 'doc_svc' },
  { id: 'doc_svc_to_light_act', from: 'doc_svc', to: 'light_act' },
  { id: 'doc_svc_to_deep_act', from: 'doc_svc', to: 'deep_act' },
  { id: 'doc_svc_to_embeddings', from: 'doc_svc', to: 'embeddings' },
  { id: 'doc_svc_to_vectorstore', from: 'doc_svc', to: 'vectorstore' },
  { id: 'doc_svc_to_main_db', from: 'doc_svc', to: 'main_db' },
  { id: 'vectorstore_to_graph_db', from: 'vectorstore', to: 'graph_db' },
  { id: 'search_svc_to_cache', from: 'search_svc', to: 'cache_db' },
  { id: 'search_svc_to_main_db', from: 'search_svc', to: 'main_db' },
  { id: 'search_svc_to_graph_db', from: 'search_svc', to: 'graph_db' },
  { id: 'auth_to_db', from: 'auth_svc', to: 'main_db' },
]);

const useCases = ref<UseCase[]>([
  {
    id: 'guest_search',
    title: 'Anonymized Search Query',
    description: 'A non-logged user searches the site. Hits cache directly, avoiding the main database.',
    activeNodes: ['client_web', 'api_gw', 'search_contr','search_svc', 'cache_db'],
    activeConnections: ['web_to_gw', 'gw_to_search_contr', 'search_contr_to_search_svc', 'search_svc_to_cache']
  },
  {
    id: 'user_login',
    title: 'User Login & Authentication',
    description: 'User enters credentials. Gateway redirects to Auth Service, checking against primary database.',
    activeNodes: ['client_mobile', 'api_gw', 'auth_svc', 'main_db'],
    activeConnections: ['mobile_to_gw', 'gw_to_auth', 'auth_to_db']
  },
  {
    id: 'document_analysis',
    title: 'Breakout of Document Analysis Flow',
    description: 'User selects between shallow and deep analysis.',
    activeNodes: ['client_web', 'api_gw', 'doc_contr', 'doc_svc', 'light_act', 'deep_act', 'embeddings', 'vectorstore', 'graph_db', 'main_db'],
    activeConnections: ['web_to_gw', 'gw_to_doc_contr', 'doc_contr_to_doc_svc', 'doc_svc_to_light_act', 'doc_svc_to_deep_act', 
    'doc_svc_to_embeddings', 'doc_svc_to_vectorstore', 'vectorstore_to_graph_db', 'doc_svc_to_main_db']
  },
  {
    id: 'author_search',
    title: 'Author Search & Relationship Exploration',
    description: 'User searches for authors and explores their relationships.',
    activeNodes: ['client_web', 'api_gw', 'search_contr', 'search_svc', 'main_db', 'graph_db'],
    activeConnections: ['web_to_gw', 'gw_to_search_contr', 'search_svc_to_main_db', 'search_svc_to_graph_db', 'search_contr_to_search_svc']
  }
]);

// FIXED: Pulls index 0 out of the array layout to prevent unhandled rendering runtime errors
const activeUseCase = ref<UseCase>(useCases.value[0]);
const telemetryLogs = ref<LogEntry[]>([]);
let logInterval: any = null;

function getNodeById(id: string) {
  return nodes.value.find(n => n.id === id);
}

function isNodeActive(nodeId: string): boolean {
  if (!activeUseCase.value || !activeUseCase.value.activeNodes) return false;
  return activeUseCase.value.activeNodes.includes(nodeId);
}

function isConnectionActive(connId: string): boolean {
  if (!activeUseCase.value || !activeUseCase.value.activeConnections) return false;
  return activeUseCase.value.activeConnections.includes(connId);
}

function generateTelemetryLog() {
  if (!activeUseCase.value || !activeUseCase.value.activeNodes) return;
  const currentNodes = activeUseCase.value.activeNodes;
  const randomNodeId = currentNodes[Math.floor(Math.random() * currentNodes.length)];
  const node = getNodeById(randomNodeId);
  
  if (!node) return;

  const msgs = [
    { message: `[${node.label}] Request payloads processed cleanly.`, type: 'success' },
    { message: `[${node.label}] Connection verification handshake complete.`, type: 'info' },
    { message: `[${node.label}] Latency calculation complete: ${Math.floor(Math.random() * 45) + 5}ms`, type: 'info' }
  ];
  
  const selected = msgs[Math.floor(Math.random() * msgs.length)];
  
  telemetryLogs.value.unshift({
    id: Math.random().toString(),
    timestamp: new Date().toLocaleTimeString(),
    message: selected.message,
    type: selected.type as any
  });

  if (telemetryLogs.value.length > 15) telemetryLogs.value.pop();
}

onMounted(() => {
  logInterval = setInterval(generateTelemetryLog, 1000);
});

onUnmounted(() => {
  clearInterval(logInterval);
});
</script>

<template>
   <div class="bounding-panel">
  <div class=" h-screen w-full bg-slate-950 text-slate-100 font-sans overflow-hidden">
    
    <!-- Sidebar Control Grid -->
    <div class="w-80 border-r border-slate-900 bg-slate-900 p-6 flex flex-col gap-6 shrink-0">
      <div>
        <h2 class="text-xs font-bold uppercase tracking-widest text-slate-500 mb-4">System Use Cases</h2>
        <div class="space-y-2">
          <button 
            v-for="uc in useCases" 
            :key="uc.id"
            @click="activeUseCase = uc; telemetryLogs = []"
            :class="[
              'w-full text-left p-4 rounded-xl text-sm transition-all border font-medium',
              activeUseCase.id === uc.id 
                ? 'bg-blue-600 text-white border-blue-500 shadow-lg' 
                : 'bg-slate-800 text-slate-400 hover:bg-slate-700/50 border-slate-700'
            ]"
          >
            {{ uc.title }}
          </button>
        </div>
      </div>

      <!--text-blue-400-->
      <div class="p-4 rounded-xl bg-slate-800/40 border border-slate-800 mt-auto">
        <h3 class="text-xs font-bold uppercase tracking-wider black mb-2">Flow Scope</h3>
        <p class="text-xs leading-relaxed text-slate-400">{{ activeUseCase.description }}</p>
      </div>
    </div>

    <!-- Layout Canvas Workspace -->
    <div class="flex-1 flex flex-col p-6 gap-6 h-full overflow-hidden">
      
      <!-- Top Canvas View (FIXED height boundary box to enforce visible CSS percentages) -->
      <div class="h-[500px] w-full relative border border-slate-900 rounded-2xl bg-slate-950 overflow-hidden">
        
        <svg class="absolute inset-0 w-full h-full pointer-events-none z-0">
          <g v-for="conn in connections" :key="conn.id">
            <line
              v-if="getNodeById(conn.from) && getNodeById(conn.to)"
              :x1="`${getNodeById(conn.from)!.x}%`"
              :y1="`${getNodeById(conn.from)!.y}%`"
              :x2="`${getNodeById(conn.to)!.x}%`"
              :y2="`${getNodeById(conn.to)!.y}%`"
              :class="[
                'transition-all duration-500',
                isConnectionActive(conn.id) ? 'stroke-blue-500 stroke-[3px]' : 'stroke-slate-800 stroke-[1.5px]'
              ]"
            />
            <!-- Moving Vector Overlay Pulses -->
            <line
              v-if="getNodeById(conn.from) && getNodeById(conn.to) && isConnectionActive(conn.id)"
              :x1="`${getNodeById(conn.from)!.x}%`"
              :y1="`${getNodeById(conn.from)!.y}%`"
              :x2="`${getNodeById(conn.to)!.x}%`"
              :y2="`${getNodeById(conn.to)!.y}%`"
              stroke="#3b82f6"
              stroke-width="3"
              stroke-dasharray="6 12"
              class="animate-dash"
            />
          </g>
        </svg>

        <!-- Dynamic Absolute Coordinate System Element Render Wrapper -->
        <SystemNodeComponent 
          v-for="node in nodes" 
          :key="node.id" 
          :id="node.id" 
          :label="node.label" 
          :type="node.type"
          :isActive="isNodeActive(node.id)"
          class="absolute -translate-x-1/2 -translate-y-1/2 w-40"
          :style="{ left: `${node.x}%`, top: `${node.y}%` }"
        />
      </div>

      <!-- Bottom Log Box border-slate-900-->
      <div class="flex-1 min-h-[150px] border red bg-slate-900/20 rounded-2xl p-4 flex flex-col overflow-hidden">
        <div class="flex items-center gap-2 border-b border-slate-900 pb-2 mb-3 shrink-0">
          <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
          <h3 class="text-xs font-bold uppercase tracking-widest text-slate-400">Live Request Telemetry Logs</h3>
        </div>
        
        <div class="flex-1 overflow-y-auto font-mono text-xs space-y-1.5 pr-2">
          <div v-for="log in telemetryLogs" :key="log.id" class="flex items-start gap-4 text-slate-300">
            <span class="text-slate-600 select-none whitespace-nowrap">{{ log.timestamp }}</span>
            <span :class="log.type === 'success' ? 'text-emerald-400' : 'text-blue-400'">&rarr;</span>
            <span class="flex-1">{{ log.message }}</span>
          </div>
        </div>
      </div>

    </div>
  </div>
  </div>
</template>

<style scoped>
@keyframes dash {
  to {
    stroke-dashoffset: -10;
  }
}
.animate-dash {
  animation: dash 1.2s linear infinite;
}

/* Injects Tailwind CSS strictly for this component and its template */
@import "tailwindcss";

.bounding-panel {
  border: 10px solid gold;
}   
</style>