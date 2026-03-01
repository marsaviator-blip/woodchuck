<template>
  <form @submit.prevent="handleSubmit" class="login-form">
    <div>
      <label for="species">Species:</label>
      <input type="text" id="species" v-model="formData.species" required />
    </div>
    <div>
      <label for="location">Location:</label>
      <input type="text" id="location" v-model="formData.password" required />
    </div>
    <button type="submit">Search</button>
  </form>
</template>

<script setup>
import { reactive } from 'vue'; // Import reactive for complex data types

// Define form data as a reactive object
const formData = reactive({
  email: '',
  password: ''
});

// Handle the form submission logic
const handleSubmit = () => {
  // Access form data directly from the reactive object
  console.log('Form submitted with data:', { ...formData });

  // Here you would typically make an API call (e.g., using fetch or axios)
  // to an authentication endpoint
  
  // Example server call placeholder:
  /*
  fetch('/api/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(formData),
  })
  .then(response => response.json())
  .then(data => console.log('Success:', data))
  .catch((error) => console.error('Error:', error));
  */

  // Clear the form fields after submission (optional)
  formData.email = '';
  formData.password = '';
};
</script>

<style scoped>
.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  max-width: 300px;
  margin: 0 auto;
}

.login-form div {
  display: flex;
  flex-direction: column;
}
</style>