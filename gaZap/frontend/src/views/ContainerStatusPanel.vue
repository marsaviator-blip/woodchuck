<template>
  <div class="status-panel">
    <h2>Container Status Dashboard</h2>

    <!-- Loading State -->
    <div v-if="loading" class="loading">Loading container states...</div>

    <!-- Error State -->
    <div v-else-if="error" class="error-message">{{ error }}</div>

    <!-- Data Display Grid -->
    <div v-else class="container-grid">
      <div 
        v-for="container in containers" 
        :key="container.id" 
        class="container-card"
      >
        <div class="card-header">
          <h3>{{ container.name }}</h3>
          <!-- Status Badge with Dynamic Color Coding -->
          <span class="status-badge" :class="getStatusClass(container.status)">
            {{ container.status }}
          </span>
        </div>

        <div class="card-body">
          <p><strong>ID:</strong> <code>{{ container.id.substring(0, 12) }}</code></p>
          <p><strong>Image:</strong> {{ container.image }}</p>
          
          <!-- Networks Mapping -->
          <div class="networks-section">
            <strong>Networks:</strong>
            <div v-if="container.networks && container.networks.length" class="network-tags">
              <span 
                v-for="network in container.networks" 
                :key="network" 
                class="network-tag"
              >
                {{ network }}
              </span>
            </div>
            <span v-else class="none-text">None</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useContainerStatus } from '@/services/status-check';
import type { ContainerInfo } from '@/types/containers';

// 2. Strongly type your reactive states
const containers = ref<ContainerInfo[]>([])
const loading = ref<boolean>(true)
const error = ref<string | null>(null)
const containerStatus = useContainerStatus();

// Helper function to safely apply style classes based on status string
const getStatusClass = (status: string): string => {
  const s = status.toLowerCase()
  if (s.includes('up') || s.includes('running')) return 'status-running'
  if (s.includes('exited') || s.includes('stopped')) return 'status-stopped'
  if (s.includes('paused')) return 'status-paused'
  return 'status-unknown'
}


const fetchContainers = async () => {
  try {
    loading.value = true
    
    const containerData = await containerStatus.getStatus();
    if (Array.isArray(containerData)) {
      containers.value = containerData;
    } else if (containerData && Array.isArray(containerData.data)) {
      containers.value = containerData.data;
    } else {
      // If neither is an array, check what you are actually getting
      console.warn('Backend data is not an array:', containerData);
      containers.value = []; 
    }
  } catch (err) {
    error.value = 'Failed to load container data.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchContainers()
})
</script>

<style scoped>
.status-panel {
  padding: 24px;
  font-family: system-ui, -apple-system, sans-serif;
  color: #1a1a1a;
}
.container-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 24px;
}
.container-card {
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  border-bottom: 1px solid #f1f5f9;
  padding-bottom: 12px;
}
.card-header h3 {
  margin: 0;
  font-size: 1.15rem;
}
.card-body p {
  margin: 8px 0;
  font-size: 0.9rem;
}
code {
  background: #f1f5f9;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}
/* Network UI Tags */
.networks-section {
  margin-top: 12px;
  font-size: 0.9rem;
}
.network-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 6px;
}
.network-tag {
  background: #e0f2fe;
  color: #0369a1;
  padding: 2px 8px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 500;
}
/* Status Badge Color Handling */
.status-badge {
  padding: 4px 10px;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}
.status-running { background: #dcfce7; color: #15803d; }
.status-stopped { background: #fee2e2; color: #b91c1c; }
.status-paused  { background: #fef9c3; color: #a16207; }
.status-unknown { background: #f1f5f9; color: #475569; }
.none-text { color: #94a3b8; font-style: italic; margin-left: 4px;}
</style>
