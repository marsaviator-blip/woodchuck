<template>
  <div class="panel-container">
    <header>
      <h1 style="color: #000000 !important; opacity: 1 !important;">Scholarly Graph Search</h1>
      <p style="color: #000000 !important; opacity: 1 !important;">Search papers, authors, and institutions with pagination.</p>
    </header>

    <!-- Search Controls -->
    <div class="search-section">
      <!-- <div class="filter-group">
        <label v-for="type in ['all', 'paper', 'author', 'institution']" :key="type">
          <input type="radio" v-model="searchType" :value="type" @change="resetAndSearch" />
          {{ type.toUpperCase() }}
        </label>
      </div> -->
<div class="filter-group">
  <label v-for="type in ['all', 'paper', 'author', 'institution']" :key="type">
    <input 
      type="radio" 
      v-model="searchType" 
      :value="type" 
      @change="resetAndSearch" 
    />
    <!-- Wrap text in a span for better layout control -->
    <span class="label-text">{{ type.toUpperCase() }}</span>
  </label>
</div>      
      <div class="search-box">
        <input 
          v-model="query" 
          @input="debounceSearch" 
          placeholder="Type to search entities and relations..." 
        />
        <span v-if="loading">Searching...</span>
      </div>
    </div>

    <!-- Results Display -->
    <main class="results-section">
      <div v-if="results.length === 0 && !loading" class="empty">
        No connections found. Try a different query.
      </div>

      <div v-for="item in results" :key="item.id" class="card">
        <div class="card-header">
          <span class="badge" :class="item.type">{{ item.type }}</span>
          <h3>{{ item.title }}</h3>
        </div>
        
        <div class="relations">
          <p v-if="item.authors?.length">✍️ <strong>Authors:</strong> {{ item.authors.join(', ') }}</p>
          <p v-if="item.institutions?.length">🏢 <strong>Institutions:</strong> {{ item.institutions.join(', ') }}</p>
          <p v-if="item.papers?.length">📄 <strong>Related Papers:</strong> {{ item.papers.join(', ') }}</p>
        </div>
      </div>

      <!-- Pagination Trigger -->
      <div v-if="hasMore && !loading" class="pagination-controls">
        <button @click="loadMore" class="load-more-btn">Load More Results</button>
      </div>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const query = ref('');
const searchType = ref('all');
const results = ref([]);
const loading = ref(false);
const page = ref(0);
const pageSize = ref(10);
const hasMore = ref(false);
let debounceTimer = null;

const resetAndSearch = () => {
  page.value = 0;
  results.value = [];
  hasMore.value = false;
  performSearch();
};

const debounceSearch = () => {
  clearTimeout(debounceTimer);
  debounceTimer = setTimeout(resetAndSearch, 300);
};

const loadMore = () => {
  page.value++;
  performSearch();
};

const performSearch = async () => {
  if (!query.value.trim()) {
    results.value = [];
    hasMore.value = false;
    return;
  }
  
  loading.value = true;
  try {
    const response = await fetch(
      `/api/search?q=${encodeURIComponent(query.value)}&type=${searchType.value}&page=${page.value}&size=${pageSize.value}`
    );
    const data = await response.json();
    
    // Append or overwrite depending on current page
    if (page.value === 0) {
      results.value = data;
    } else {
      results.value = [...results.value, ...data];
    }
    
    // If we received fewer items than requested, we hit the end
    hasMore.value = data.length === pageSize.value;
  } catch (error) {
    console.error("Search failed:", error);
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.panel-container { max-width: 900px; margin: 0 auto; padding: 20px; font-family: sans-serif; }
.search-section { background: #f5f5f5; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
.filter-group { color: blue;margin-bottom: 15px; display: flex; gap: 15px; }
.empty { background: #e8f5e9; color: #1b5e20; }
.search-box input { width: 100%; padding: 12px; font-size: 16px; border: 1px solid #ccc; border-radius: 4px; }
.card { border: 1px solid #0c0b0b; border-radius: 6px; padding: 15px; margin-bottom: 15px; background: white; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: bold; text-transform: uppercase; margin-right: 10px; }
.badge.paper { background: #e3f2fd; color: #0d47a1; }
.badge.author { background: #e8f5e9; color: #1b5e20; }
.badge.institution { background: #fff3e0; color: #e65100; }
.relations p { margin: 5px 0; font-size: 14px; color: #555; }
.pagination-controls { text-align: center; margin-top: 20px; }
.load-more-btn { padding: 10px 20px; font-size: 14px; cursor: pointer; background-color: #0d47a1; color: white; border: none; border-radius: 4px; }
.load-more-btn:hover { background-color: #1565c0; }
</style>
