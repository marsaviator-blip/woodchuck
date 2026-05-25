import { ref } from 'vue';
//import axios from 'axios';

const posts = ref([]);
const isLoading = ref(true);
const error = ref(null);

export function useContainerStatus() {
  const getStatus = async () => {
    try {
      const response = await fetch('http://localhost:8089/gaZap/status/container');
      
      // Check if the HTTP response is successful
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Parse the JSON data
      const data = await response.json();
      
      // Update the reactive state
      posts.value = data;
      return data;
    } catch (err) {
      // Capture any network or parsing errors
      error.value = err;
      console.error('Error fetching data:', err);
    } finally {
      // Always hide the loading state
      isLoading.value = false;
    }
  }; 

  return { 
    getStatus,
  };
}