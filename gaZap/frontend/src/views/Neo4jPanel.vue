<template>
  <div class="panel">
    <h3>🌐 Graph Database Snapshot</h3>
    <button @click="fetchGist" :disabled="loading">
      {{ loading ? 'Querying...' : 'Refresh DB Gist' }}
    </button>

    <div v-if="error" class="error">{{ error }}</div>

    <div v-if="stats" class="grid-summary">
      <div class="card"><strong>Total Nodes:</strong> {{ stats.totalNodes }}</div>
      <div class="card"><strong>Total Edges:</strong> {{ stats.totalEdges }}</div>
    </div>

    <h4>Node Labels Found:</h4>
    <ul>
      <li v-for="(count, label) in stats?.labels" :key="label">
        <strong>{{ label }}:</strong> {{ count }} items
      </li>
    </ul>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import neo4j from 'neo4j-driver';

const stats = ref(null);
const loading = ref(false);
const error = ref(null);

// const fetchGist = async () => {
//   loading.value = true;
//   error.value = null;
  
//   // Replace with your actual Neo4j credentials
//   const driver = neo4j.driver('bolt://localhost:7687', neo4j.auth.basic('neo4j', 'password'));
//   const session = driver.session();

//   try {
//     // Queries meta data summarizing what is inside the graph store
//     const query = `
//       CALL apoc.meta.stats() 
//       YIELD nodeCount, relCount, labels
//       RETURN nodeCount, relCount, labels
//     `;
//     const result = await session.run(query);
//     const record = result.records[0];
//     stats.value = {
//       totalNodes: record.get('nodeCount').toNumber(),
//       totalEdges: record.get('relCount').toNumber(),
//       labels: record.get('labels')
//     };
//   } catch (err) {
//     error.value = 'Failed to fetch Neo4j gist: ' + err.message;
//   } finally {
//     await session.close();
//     await driver.close();
//     loading.value = false;
//   }
// };

const fetchGist = async () => {
  loading.value = true;
  error.value = null;
  try {
    const response = await fetch('http://localhost:8089/api/graph/gist');
    if (!response.ok) throw new Error('Server error code: ' + response.status);
    stats.value = await response.json();
  } catch (err) {
    error.value = 'Failed to fetch Neo4j gist via Spring: ' + err.message;
  } finally {
    loading.value = false;
  }
};

onMounted(fetchGist);
</script>
<style scoped>
.panel { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.grid-summary { display: flex; gap: 20px; margin: 20px 0; }
.card { background: #e8f4fd; padding: 15px; border-radius: 6px; flex: 1; }
.error { color: red; margin: 10px 0; }
button { padding: 10px 15px; background: #2ecc71; color: white; border: none; border-radius: 4px; cursor: pointer; }
</style>