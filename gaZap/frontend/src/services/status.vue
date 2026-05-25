<script setup lang="ts">
import { ref, onMounted } from 'vue'

const data = ref(null)
const error = ref(null)
const isLoading = ref(true)

async function fetchData() {
  try {
    const response = await fetch('https://jsonplaceholder.typicode.com/posts/1')
    
    // Check if the response is okay (status 200-299)
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }
    
    // Parse the JSON body
    data.value = await response.json()
  } catch (err) {
    error.value = err.message
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<template>
  <div v-if="isLoading">Loading data...</div>
  
  <div v-else-if="error">Error: {{ error }}</div>
  
  <div v-else-if="data">
    <h1>{{ data.title }}</h1>
    <p>{{ data.body }}</p>
  </div>
</template>
