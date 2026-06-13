<template>
  <div v-if="isOpen" class="dialog-overlay">
    <div class="dialog-content">
      <slot></slot>
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