<template>
    <form @submit.prevent="handleSubmit" class="login-form">
        <div>
            <label for="species">Species:</label>
            <input type="text" id="species" v-model="formData.species" required />
        </div>
        <div>
            <p>Selected options: {{ multiValue.species }}</p>
            <VueMultiselect v-model="multiValue" :options="options" :multiple="true" :close-on-select="true"
                placeholder="Pick one or more species or all" label="name" track-by="name" />
        </div>
        <div class="locationContainer">
            <div class="containerHeader">
                <div>Location: {{ checkedLocations }}</div>
            </div>
            <div class="locationGroup">
                <div class="checkbox-group">
                    <label for="PA">
                        <input type="checkbox" id="PA" value="PA" v-model="checkedLocations" />
                        Denver, PA
                    </label>
                </div>
                <div class="checkbox-group">
                    <label for="MD">
                        <input type="checkbox" id="MD" value="MD" v-model="checkedLocations" />
                        Hanover, MD
                    </label>
                </div>
            </div>
        </div>
        <div class="lengthContainer">
            <div class="containerHeader">
                <p>Lengths</p>
            </div>
            <div class="lengthGroup">
                <div class=" radio-group">
                    <label for="anyLength">
                        <input type="radio" id="anyLength" value="anyLength" v-model="selectedOption" name="options">
                        any length
                    </label>
                </div>
                <div class="radio-group">
                    <label for="lessThan4">
                        <input type="radio" id="lessThan4" value="less than 4 foot" v-model="selectedOption"
                            name="options">
                        less than 4 foot
                    </label>
                </div>
                <div class="radio-group">
                    <label for="lessThan6">
                        <input type="radio" id="lessThan6" value="less than 6 foot" v-model="selectedOption"
                            name="options">
                        less than 6 foot
                    </label>
                </div>
                <div class="radio-group">
                    <label for="lessThan8">
                        <input type="radio" id="lessThan8" value="less than 8 foot" v-model="selectedOption"
                            name="options">
                        less than 8 foot
                    </label>
                </div>
                <div class="radio-group">
                    <label for="8 foot">
                        <input type="radio" id="8foot" value="8foot" v-model="selectedOption" name="options">
                        8 foot
                    </label>
                </div>
                <div class="radio-group">
                    <label for="16">
                        <input type="radio" id="16foot" value="16foot" v-model="selectedOption" name="options">
                        16 foot
                    </label>
                </div>
                <div class="radio-group">
                    <label for="32foot">
                        <input type="radio" id="32foot" value="32foot" v-model="selectedOption" name="options">
                        32 foot
                    </label>
                </div>
            </div>
        </div>
        <button type="submit">Search</button>
    </form>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'; // Import reactive for complex data types
import VueMultiselect from '@suadelabs/vue3-multiselect';

// Define form data as a reactive object
const formData = reactive({
    species: '',
    checkedLocations: [], // This will hold the values of the checked checkboxes
    selectedOption: '', // This will hold the value of the selected radio button 
});

const multiValue = ref([]); // Must be an array for multiple selection
const options = ref([
    { name: 'any oak', species: 'any oak' },
    { name: 'white oak', species: 'white oak' },
    { name: 'red oak', species: 'red oak' },
    { name: 'chestnut oak', species: 'chestnut oak' },
    { name: 'scarlet oak', species: 'scarlet oak' },
    { name: 'poplar', species: 'poplar' },
    { name: 'pine', species: 'pine' },

]);

const checkedLocations = ref([]);
const selectedOption = ref(null);

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
    formData.species = '';
    //formData.password = '';
};
</script>

<style scoped>
.login--form {
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


.containerHeader {
    background-color: green;
}

.locationContainer {
    display: flex;
    flex-direction: column;
    border: 2px solid green;
    border-radius: 10px;
    width: 800px;
}

.locationGroup {
    display: flex;
    flex-direction: row;
    gap: 1rem;
    width: 160px;
    align-items: center;
}

.checkbox-group {
    display: flex;
    gap: 0.5rem;

}

.sizing {
    display: flex;
   gap: 1rem;
}

.lengthContainer {
    border: 2px solid green;
    border-radius: 10px;
    width: 160px;
}

.lengthGroup {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    background-color: lightseagreen;
    margin: 3px;
}

.radio-group {
    display: flex;
    flex-direction: column;
    width: 150px;
    margin-left: 5px;
}
</style>