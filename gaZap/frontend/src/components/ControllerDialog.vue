<script setup>
import { ref, watch } from 'vue';

// Define the properties the dialog expects
const props = defineProps({
  isOpen: Boolean
});

// Define the events the dialog will emit back to the parent
const emit = defineEmits(['close', 'confirm']);

// Create a reference to the native <dialog> element
const dialogRef = ref(null);

// Watch the isOpen prop to open or close the native dialog
watch(() => props.isOpen, (newVal) => {
  if (newVal) {
    dialogRef.value?.showModal(); // Opens as a modal
  } else {
    dialogRef.value?.close(); // Closes the dialog
  }
});

// Function to handle backdrop clicks
const handleBackdropClick = (event) => {
  if (event.target === dialogRef.value) {
    emit('close');
  }
};
</script>

<template>
  <!-- Native HTML dialog element -->
  <dialog 
    ref="dialogRef" 
    @click="handleBackdropClick"
    @close="emit('close')"
  >
    <div class="dialog-content">
      <slot name="header">
        <h3>Default Header</h3>
      </slot>
      
      <main>
        <slot>
          <p>Default dialog content goes here.</p>
        </slot>
      </main>

      <footer>
        <slot name="footer">
          <button @click="emit('close')">Cancel</button>
          <button @click="emit('confirm')">Confirm</button>
        </slot>
      </footer>
    </div>
  </dialog>
</template>

<style scoped>
dialog {
  border: none;
  border-radius: 8px;
  padding: 0;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

/* Backdrop styling */
dialog::backdrop {
  background-color: rgba(0, 0, 0, 0.5);
}

.dialog-content {
  padding: 24px;
  min-width: 300px;
}

header {
  margin-top: 0;
}

footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}
</style>
