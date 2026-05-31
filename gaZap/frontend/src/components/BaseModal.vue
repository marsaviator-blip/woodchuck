<template>
  <teleport to="body">
    <!-- Using method="dialog" ensures it closes natively when forms are submitted -->
    <dialog ref="dialogRef" class="modal-box"  max-width="500px" @close="emit('close')">
      <div class="modal-content">
        <slot></slot>
      </div>
    </dialog>
  </teleport>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
const props = defineProps({
  isOpen: {
    type: Boolean,
    required: true
  }    
  // listData: {
  //   type: Array,
  //   required: true,
  //   default: () => []
  //}
});

const emit = defineEmits(['close']);
const dialogRef = ref(null);

watch(() => props.isOpen, (newVal) => {
  const dialog = dialogRef.value;
  if (!dialog) return;

  if (newVal) {
    if (dialog.open) return;
    dialog.showModal(); // Native API opens it as a modal (traps focus, adds backdrop)
  } else {
    if (!dialog.open) return;
    dialog.close(); // Native API closes it
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
