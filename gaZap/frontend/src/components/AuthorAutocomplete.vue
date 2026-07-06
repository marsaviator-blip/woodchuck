<!-- src/components/AuthorAutocomplete.vue -->
<template>
  <div class="autocomplete-wrapper">
    <input
      type="text"
      v-model="searchQuery"
      @input="onInput"
      placeholder="Type author name..."
      class="search-input"
    />
    
    <ul v-if="suggestions.length > 0" class="suggestions-list">
      <li 
        v-for="author in suggestions" 
        :key="author.id" 
        @click="selectAuthor(author)"
      >
        {{ author.name }} <span class="meta">({{ author.paperCount }} papers)</span>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import neo4j from 'neo4j-driver';

const emit = defineEmits(['selected']);
const searchQuery = ref('');
const suggestions = ref([]);
let debounceTimeout = null;

const onInput = () => {
  clearTimeout(debounceTimeout);
  if (!searchQuery.value.trim()) {
    suggestions.value = [];
    return;
  }
  // Debounce API calls to prevent flooding Neo4j on every keystroke
  debounceTimeout = setTimeout(searchGraph, 300);
};

// const searchGraph = async () => {
//   const driver = neo4j.driver('bolt://localhost:7687', neo4j.auth.basic('neo4j', 'password'));
//   const session = driver.session();

//   try {
//     // Regex/Contains search matching the typed string against author nodes
//     const query = `
//       MATCH (a:Author)
//       WHERE a.name CONTAINS $searchString
//       RETURN a.name AS name, elementId(a) AS id, count(a) as papers
//       LIMIT 10
//     `;
//     const result = await session.run(query, { searchString: searchQuery.value });
    
//     suggestions.value = result.records.map(rec => ({
//       id: rec.get('id'),
//       name: rec.get('name'),
//       paperCount: rec.get('papers').toNumber()
//     }));
//   } catch (err) {
//     console.error(err);
//   } finally {
//     await session.close();
//     await driver.close();
//   }
// };

const selectAuthor = (author) => {
  searchQuery.value = author.name;
  suggestions.value = [];
  emit('selected', author);
};

const searchGraph = async () => {
  try {
    const url = `http://localhost:8089/api/graph/authors/search?query=${encodeURIComponent(searchQuery.value)}`;
    const response = await fetch(url);
    const data = await response.json();
    
    suggestions.value = data.map(record => ({
      id: record.id,
      name: record.name,
      paperCount: record.paperCount
    }));
  } catch (err) {
    console.error('Spring service autocomplete lookup error:', err);
  }
};
</script>

<style scoped>
.autocomplete-wrapper { position: relative; width: 100%; max-width: 400px; }
.search-input { width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
.suggestions-list { position: absolute; width: 100%; background: white; border: 1px solid #ccc; border-top: none; list-style: none; margin: 0; padding: 0; max-height: 200px; overflow-y: auto; z-index: 10; }
.suggestions-list li { padding: 10px; cursor: pointer; }
.suggestions-list li:hover { background: #f0f0f0; }
.meta { font-size: 0.85em; color: #7f8c8d; }
</style>
