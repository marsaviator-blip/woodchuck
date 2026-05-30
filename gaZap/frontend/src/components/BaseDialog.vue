<template>
  <div v-if="isOpen" class="dialog-overlay">
    <div class="dialog-content">
      <h3>Container List</h3>
      <ul>
        <li v-for="(item, index) in listData" :key="index">
          {{ item }} <!-- Assuming item is an object, you can format it as needed -->
        </li>
      </ul>
      <button @click="$emit('update:isOpen', false)">Close</button>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps({
  isOpen: {
    type: Boolean,
    required: true
  },
  listData: {
    type: Array,
    required: true,
    default: () => []
  }
});

defineEmits(['update:isOpen']);
</script>
<style>
</style>