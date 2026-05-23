import { ref, onMounted } from 'vue';
import axios from 'axios';

const linkStatus = ref(null);
const isLoading = ref(true);
const targetUrl = 'https://localhost:nnnn'';

onMounted(async () => {
  try {
    // You may need to bypass CORS errors on the frontend 
    // by using a server-side proxy
    const response = await axios.get(targetUrl, {
      validateStatus: (status) => status < 500 // Accept 404s without throwing an error
    });
    
    linkStatus.value = response.status;
  } catch (error) {
    if (error.response) {
      linkStatus.value = error.response.status; // e.g., 404, 500
    } else {
      linkStatus.value = 'Network Error or Blocked by CORS';
    }
  } finally {
    isLoading.value = false;
  }
});
