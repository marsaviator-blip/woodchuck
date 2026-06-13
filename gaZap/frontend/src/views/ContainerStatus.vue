<template>
      <div>
      <h3>Container List</h3>
      <div 
        v-for="container in containers" 
        :key="container.id" 
        class="card"
      >
        <div class="card-header">
          <h3>{{ container.name?.replace('/', '') || 'Unnamed' }}</h3>
          <span :class="['badge', container.status.toLowerCase()]">
            {{ container.status }}
          </span>
        </div>
        <div class="card-body">
          <p><strong>Image:</strong> {{ container.image }}</p>
          <p><strong>Status:</strong> {{ container.status }}</p>
          <!-- <p><strong>Created:</strong> {{ new Date(container.created * 1000).toLocaleDateString() }}</p> -->
          
          <!-- <div v-if="container.ports && container.ports.length > 0">
            <strong>Ports:</strong>
            <ul>
              <li v-for="(port, index) in container.ports" :key="index">
                {{ port.publicPort }} ➡️ {{ port.privatePort }} ({{ port.type }})
              </li>
            </ul>
          </div> -->
        </div>
      </div>
    </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue';
import { useContainerStatus } from '@/services/status-check';
import type { ContainerInfo } from '@/types/containers';

const error = ref<string | null>(null);

const props = defineProps({
  isModalOpen: {
    type: Boolean,
    required: true
  }    
});

const emit = defineEmits(['close']);
const dialogRef = ref(null);
const containers = ref<ContainerInfo[]>([]);
const containerStatus = useContainerStatus();

onMounted(async() => {
  try {
    const containerData = await containerStatus.getStatus();
    containers.value = containerData; 
    console.log('Fetched container status:');
  } catch (err) {
    console.error('Error fetching container status:', err);
    error.value = 'Failed to fetch container status';
  } finally {
    // You can now safely access the DOM element
    //dialogRef.value?.showModal();
  }
});
</script>

<style scoped>
/* The backdrop can be styled natively or via CSS */
dialog::backdrop {
  background: rgba(0, 0, 0, 0.5);
}

dialog {
  border: 5px solid blueviolet;
  border-radius: 8px;
  padding: 10;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
}
</style>
