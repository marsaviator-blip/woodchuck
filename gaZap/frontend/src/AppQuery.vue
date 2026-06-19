<script setup lang="ts">
// import { ref } from 'vue';
// import { RouterLink, RouterView } from 'vue-router'
// import LeftPanel from './components/LeftPanel.vue';
// import RightPanel from './components/RightPanel.vue';
// import SearchPanel from './components/SearchPanel.vue'
</script>

<template>
  <div class="app-container">
    <nav class="sidebar">
      <h2>Document Understanding</h2>
      <router-link to="/">📊 Dashboard</router-link>
      <hr>
      <router-link to="/neo4j">🌐 Neo4j Explorer</router-link>
      <router-link to="/authors">🔍 Author lookup</router-link>
      <router-link to="/search">🔍 Search</router-link>
      <router-link to="/scholarlySearch">🔍 Search relationships</router-link>
      <hr>
      <router-link to="/container-status">🔍 Container Status</router-link>
      <router-link to="/container-instructions">📋 Container Instructions</router-link >
      <hr>
      <router-link to="/setup">🔍 gaZap Setup</router-link>
      <router-link to="/setup-d3">🔍 gaZap Setup D3</router-link>
      <router-link to="/instructions">🔍 gaZap Instructions</router-link>
    </nav>
    <main class="content-panel">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app-container { display: flex; height: 100vh; font-family: sans-serif; }
.sidebar { width: 240px; background: #2c3e50; padding: 20px; display: flex; flex-direction: column; gap: 15px; }
.sidebar a { color: #ecf0f1; text-decoration: none; padding: 10px; border-radius: 4px; }
.sidebar a.router-link-active { background: #3498db; }
.content-panel { flex: 1; padding: 30px; background: #f8f9fa; overflow-y: auto; }
</style>