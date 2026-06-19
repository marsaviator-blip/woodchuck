<template>
  <div class="card">
    <DataTable 
      :value="authors" 
      lazy 
      paginator 
      :rows="rows" 
      :totalRecords="totalRecords" 
      :loading="loading"
      @page="onPage($event)"
    >
      <Column field="id" header="ID"></Column>
      <Column field="name" header="Name"></Column>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

const authors = ref([]);
const totalRecords = ref(0);
const rows = ref(10);
const first = ref(0);
const loading = ref(false);

const loadLazyData = async () => {
  loading.value = true;
  
  // Calculate Spring Boot page index from PrimeVue row index
  const pageIndex = first.value / rows.value;

  try {
    const response = await fetch(`http://localhost:8089/api/authors?page=${pageIndex}&size=${rows.value}`);
    const data = await response.json();
    
    // Map Spring's Page properties to PrimeVue properties
    authors.value = data.content; 
    totalRecords.value = data.totalElements; 
  } catch (error) {
    console.error("Failed to load data", error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadLazyData();
});

const onPage = (event) => {
  first.value = event.first;
  rows.value = event.rows;
  loadLazyData();
};
</script>
