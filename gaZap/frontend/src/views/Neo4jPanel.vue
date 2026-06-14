<template>
  <div class="neo4j-panel">
    <h3>Neo4j Graph Database Metrics</h3>
    
    <!-- Loading State -->
    <div v-if="isLoading" class="loading-state">
      Retrieving graph data...
    </div>

    <!-- Metrics Display -->
    <div v-else class="metrics-grid">
      <div class="metric-card">
        <span class="label">Total Nodes</span>
        <span class="value">{{ metrics.totalNodes.toLocaleString() }}</span>
      </div>
      
      <!-- Visual Divider -->
      <span class="vertical-divider"></span>
      
      <div class="metric-card">
        <span class="label">Total Edges</span>
        <span class="value">{{ metrics.totalEdges.toLocaleString() }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getGraphCounts, type GraphStats } from '../services/graphCalls' // Verify this path matches your folder structure

// Component state reactives
const isLoading = ref<boolean>(true)
const metrics = ref<GraphStats>({ totalNodes: 0, totalEdges: 0 })

// Fetch graph infrastructure details on layout lifecycle mount
onMounted(async () => {
  try {
    metrics.value = await getGraphCounts()
  } finally {
    isLoading.value = false
  }
})
</script>

<style scoped>
.neo4j-panel {
  padding: 20px;
  background-color: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.metrics-grid {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-top: 15px;
}

.metric-card {
  display: flex;
  flex-direction: column;
}

.label {
  font-size: 0.875rem;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.value {
  font-size: 1.875rem;
  font-weight: 700;
  color: #111827;
}

.vertical-divider {
  width: 1px;
  height: 40px;
  background-color: #d1d5db;
}

.loading-state {
  color: #9ca3af;
  font-style: italic;
  margin-top: 15px;
}
</style>
