<!-- src/views/SearchPanel.vue -->
<template>
  <div class="panel">
    <h3>🔍 Index Search & Filters</h3>
    <p>Use the search panel below to isolate records assigned to specific creators.</p>
    
    <AuthorAutocomplete @selected="handleSelection" />

    <div v-if="selectedAuthor" class="selection-details">
      <h4>Target Profile Loaded:</h4>
      <p><strong>ID:</strong> {{ selectedAuthor.id }}</p>
      <p><strong>Display Name:</strong> {{ selectedAuthor.name }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import AuthorAutocomplete from '../components/AuthorAutocomplete.vue';

const selectedAuthor = ref(null);

const handleSelection = (author) => {
  selectedAuthor.value = author;
};
</script>

<style scoped>
.panel { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.selection-details { margin-top: 30px; padding: 15px; background: #f1f2f6; border-left: 4px solid #3498db; }
</style>
