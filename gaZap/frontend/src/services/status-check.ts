import type { ContainerInfo } from '@/types/containers';
import { ref } from 'vue';
//import axios from 'axios';

const status = ref<ContainerInfo[] | null>(null);
const rawStatus = ref([]);
const isLoading = ref(true);
const error = ref(null);

export function useContainerStatus() {
  const getStatus = async () => {
    try {
      console.log('Fetching container status from backend...');
      const response = await fetch('/api/gaZap/status/containers/status', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });
//      console.log('Raw response:', response);
      // Check if the HTTP response is successful
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Parse the JSON data
      const data: ContainerInfo[] = await response.json();
      console.log('Received data:', data);
      
      // Update the reactive state
      status.value = data;
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

export function useContainerRawStatus() {
  const getRawStatus = async () => {
    try {
      console.log('Fetching raw container status from backend...');
      const response = await fetch('/api/gaZap/status/containers/rawStatus');
      console.log('Raw response:', response);
      // Check if the HTTP response is successful
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      // Parse the JSON data
      const data = await response.json();
      console.log('Received data:', data);
      
      // Update the reactive state
      rawStatus.value = data;
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
    getRawStatus,
  };
}