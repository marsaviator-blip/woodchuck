<template>
  <div class="posts-container">
    <h2>Latest Blog Posts</h2>

    <!-- Loading State -->
    <div v-if="isLoading">Loading posts...</div>

    <!-- Error State -->
    <div v-else-if="error" class="error">
      Oops! Something went wrong: {{ error.message }}
    </div>

    <!-- Data Display -->
    <ul v-else>
      <li v-for="post in posts" :key="post.id">
        <h3>{{ post.title }}</h3>
        <p>{{ post.body }}</p>
      </li>
    </ul>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';

// 1. Reactive state variables
const posts = ref([]);
const isLoading = ref(true);
const error = ref(null);

// 2. Fetch function
onMounted(async () => {
  try {
    const response = await fetch('http://localhost:8089/gaZap/status/');
    
    // Check if the HTTP response is successful
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    // Parse the JSON data
    const data = await response.json();
    
    // Update the reactive state
    posts.value = data;
  } catch (err) {
    // Capture any network or parsing errors
    error.value = err;
    console.error('Error fetching data:', err);
  } finally {
    // Always hide the loading state
    isLoading.value = false;
  }
});
</script>

<style scoped>
.posts-container {
  max-width: 600px;
  margin: 0 auto;
  font-family: sans-serif;
}
.error {
  color: red;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  background: #f4f4f4;
  margin-bottom: 15px;
  padding: 15px;
  border-radius: 5px;
}
</style>
