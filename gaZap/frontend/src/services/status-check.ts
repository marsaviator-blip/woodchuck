import { ref } from 'vue';
//import axios from 'axios';

const posts = ref([]);
const isLoading = ref(true);
const error = ref(null);

export function useContainerStatus() {
  const getStatus = async () => {
    try {
      console.log('Fetching container status from backend...');
      const response = await fetch('http://localhost:8089/gaZap/status/containers');
      console.log('Raw response:', response);
      // Check if the HTTP response is successful
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Parse the JSON data
      const data = await response.json();
      console.log('Received data:', data);
      
      // Update the reactive state
      posts.value = data;
      return posts;
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