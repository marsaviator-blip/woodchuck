<script setup lang="ts">
import { onMounted, ref } from 'vue';
// import LeftPanel from './components/LeftPanel.vue';
// import RightPanel from './components/RightPanel.vue';
// import { RouterLink, RouterView } from 'vue-router'
// import HelloWorld from './components/HelloWorld.vue'
//import ControllerDialog from './components/ControllerDialog.vue'
import { useContainerStatus } from '@/services/status-check';
import BaseDialog from './components/BaseDialog.vue';

const containers = ref([]);
const containerStatus = useContainerStatus();
const loading = ref(false);

onMounted(async() => {
  console.log('The component is now mounted!')
  try {
    const statusData = await containerStatus.getStatus();
    containers.value = statusData;
    console.log('Fetched container status:', statusData);
  } catch (err) {
    console.error('Error fetching container status:', err);
  }
  // You can now safely access the DOM element
  //openDialog();
});

const isDialogOpen = ref(false);

const handleDialogClose = () => {
  console.log('Dialog was closed!');
};
</script>

<template>
  <div class="p-8">
    <button 
      @click="isDialogOpen = true"
      class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 font-medium"
    >View gaZap Container Status</button>

    <!-- Dialog bound via v-model -->
    <BaseDialog v-model:is-open="isDialogOpen" :list-data="containers" @close="handleDialogClose">
      <template #header>
        <h2>Container Status</h2>
      </template>

      <p>This is dynamic content passed into the dialog slot!</p>

      <template #footer>
        <button 
          @click="isDialogOpen = false"
          class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
        >
          Finished viewing
        </button>
      </template>
    </BaseDialog>
  </div>
</template>