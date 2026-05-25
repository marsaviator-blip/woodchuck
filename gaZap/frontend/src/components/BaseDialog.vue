<template>
  <dialog ref="dialogEl" class="dialog-box" @click="onClickOutside">
    <div class="dialog-content" @click.stop>
      <header>
        <h3><slot name="title">Default Title</slot></h3>
        <button class="close-btn" @click="close">✖</button>
      </header>
      <main>
        <slot>Default body text goes here.</slot>
      </main>
    </div>
  </dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const dialogEl = ref<HTMLDialogElement | null>(null)

// Open the dialog modally
const open = () => {
  dialogEl.value?.showModal()
}

// Close the dialog
const close = () => {
  dialogEl.value?.close()
}

// Close when clicking the backdrop (outside the modal content)
const onClickOutside = (event: MouseEvent) => {
  const target = event.target as HTMLElement
  if (target && target.classList.contains('dialog-box')) {
    close()
  }
}

// Expose these methods to parent components
defineExpose({
  open,
  close
})
</script>

<style scoped>
.dialog-box {
  border: none;
  border-radius: 8px;
  padding: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* Backdrop dimming effect */
.dialog-box::backdrop {
  background-color: rgba(0, 0, 0, 0.4);
}

.dialog-content {
  padding: 20px;
  min-width: 300px;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.close-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1.2rem;
}
</style>
