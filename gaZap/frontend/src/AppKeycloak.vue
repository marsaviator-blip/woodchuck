<script setup lang="ts">
import { onMounted, ref } from 'vue';
// import LeftPanel from './components/LeftPanel.vue';
// import RightPanel from './components/RightPanel.vue';
// import { RouterLink, RouterView } from 'vue-router'
// import HelloWorld from './components/HelloWorld.vue'
//import ControllerDialog from './components/ControllerDialog.vue'
import { useContainerRawStatus, useContainerStatus } from '@/services/status-check';
import type { ContainerInfo } from '@/types/containers';
//import BaseDialog from './components/BaseDialog.vue';
import BaseModal from './components/BaseModal.vue';

const isModalOpen = ref(false);

const containers = ref<ContainerInfo[]>([]);
const containersRaw = ref([]);
const containerStatus = useContainerStatus();
const containerRawStatus = useContainerRawStatus();
const isLoading = ref(false);

onMounted(async() => {
  console.log('The component is now mounted!')
  isLoading.value = true;
  try {
    const statusData = await containerRawStatus.getRawStatus();
    containersRaw.value = statusData;
    console.log('Fetched container raw status:');
  } catch (err) {
    console.error('Error fetching container status:', err);
  }
  try {
    const containerData = await containerStatus.getStatus();
    containers.value = containerData; 
    console.log('Fetched container status:');
  } catch (err) {
    console.error('Error fetching container status:', err);
  }
  // You can now safely access the DOM element
  //openDialog();
  isLoading.value = false;
});

// const isDialogOpen = ref(false);
// const isDialogOpenRaw = ref(false);

// const handleDialogClose = () => {
//   console.log('Dialog was closed!');
// };
</script>

<template>
  <h1 class="text-3xl font-bold mb-4">All things gaZap</h1>
  <div>
    <button @click="isModalOpen = true">gaZap Container Information</button>

    <BaseModal :isOpen="isModalOpen" @close="isModalOpen = false">
      <div class="p-6">
        <p></p>
        <h2>Containers necessary for gaZap</h2>
        <p></p>
        <p v-if="isLoading">Loading items...</p>
        <ul>
        <li v-for="(item, index) in containers ?? []" :key="item.id">
          {{ item.name }} - {{ item.status }}
        </li>
      </ul>
        <button @click="isModalOpen = false">Close</button>
      </div>
    </BaseModal>
  </div>
  <!-- <div class="p-8">
    <button 
      @click="isDialogOpenRaw = true"
      class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 font-medium"
    >View gaZap Raw Container Status</button>
    <button 
      @click="isDialogOpen = true"
      class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 font-medium"
    >View gaZap Container Status</button>


    <BaseDialog v-model:is-open="isDialogOpenRaw" :list-data="containersRaw" @close="handleDialogClose">
      <template #header>
        <h2>RawContainer Status</h2>
      </template>

      <p>This is dynamic content passed into the dialog slot!</p>

      <template #footer>
        <button 
          @click="isDialogOpenRaw = false"
          class="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-600"
        >
          Finished viewing
        </button>
      </template>
    </BaseDialog>
    <BaseDialog v-model:is-open="isDialogOpen" :list-data="containers" @close="handleDialogClose">
      <template #header>
        <h2>RawContainer Status</h2>
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
  </div> -->
</template>