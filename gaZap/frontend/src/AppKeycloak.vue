<script setup lang="ts">
import { onMounted, ref } from 'vue';
// import LeftPanel from './components/LeftPanel.vue';
// import RightPanel from './components/RightPanel.vue';
// import { RouterLink, RouterView } from 'vue-router'
// import HelloWorld from './components/HelloWorld.vue'
import ControllerDialog from './components/ControllerDialog.vue'

onMounted(() => {
  console.log('The component is now mounted!')
  // You can now safely access the DOM element
  openDialog();
});

// Create a ref for the dialog component
const myDialog = ref<InstanceType<typeof ControllerDialog> | null>(null)

const openDialog = () => {
  myDialog.value?.open()
}

const handleDialogClose = () => {
  console.log('Dialog was closed')
}

const executeAction = () => {
  alert('Action executed!')
  myDialog.value?.close()
}
</script>

<template>
  <div class="app-container">
    <h1>gaZap - getting the data you need</h1>
    <p></p>

    <button @click="openDialog">Open Dialog</button>

    <!-- Use the Component and pass content inside -->
    <ControllerDialog 
      ref="myDialog" 
      title="Confirm Action"
      @close="handleDialogClose"
    >
      <!-- Overriding the footer slot with custom buttons -->
      <template #footer>
        <button @click="myDialog?.close()">Cancel</button>
        <button @click="executeAction" class="confirm-btn">Confirm</button>
      </template>
    </ControllerDialog>
  </div>
</template>

<style>
.app-container {
  padding: 40px;
  font-family: sans-serif;
}

.confirm-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

 
/* this is set in main.ts */
#app {
  /* width: 50%; */
  /* width: 100vh; */
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  /* flex: 1;
  padding: 0;
  margin: 0; */
  background-color:rgb(20, 82, 82);
      border: 2px solid blue;

}

</style>
