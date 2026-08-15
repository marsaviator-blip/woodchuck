<template>
  <section class="flex-1 bg-slate-900 p-6 flex flex-col h-full max-h-full overflow-hidden min-h-0">
    
    <!-- Stack Controls Header -->
    <div v-if="stack.length > 0" class="flex items-center justify-between border-b border-slate-700 pb-4 mb-4 flex-shrink-0">
      <div>
        <h1 class="text-xl font-bold text-white flex items-center gap-2">
          🚀 Active Workspace Stack 
          <span class="text-xs bg-indigo-600/30 text-indigo-400 border border-indigo-500/20 px-2 py-0.5 rounded-full font-mono">
            {{ stack.length }} items
          </span>
        </h1>
        <p class="text-slate-400 text-xs mt-1">Multi-document staging canvas</p>
      </div>
      <button 
        @click="$emit('clear-stack')"
        class="text-[10px] font-bold uppercase tracking-wider px-2.5 py-1.5 rounded border border-rose-500/30 text-rose-400 hover:bg-rose-500/10 transition-colors"
      >
        Clear Stack
      </button>
    </div>

    <!-- Scrollable Document Cards Stack Grid Frame -->
    <div v-if="stack.length > 0" class="flex-1 overflow-y-auto space-y-4 pr-1 custom-scrollbar min-h-0">
      <div 
        v-for="(stackedItem, index) in stack" 
        :key="stackedItem.id"
        class="bg-[#11141d] border rounded-xl overflow-hidden transition-all duration-150 flex flex-col"
        :class="focusedId === stackedItem.id ? 'border-indigo-500 shadow-lg shadow-indigo-500/5' : 'border-[#1e293b]'"
        @click="$emit('focus-item', stackedItem.id)"
      >
        <!-- Stack Card Header Bar -->
        <div class="px-4 py-2.5 bg-[#161a26] border-b border-[#1e293b] flex items-center justify-between select-none">
          <div class="flex items-center space-x-3 min-w-0">
            <span class="text-[9px] font-bold px-1.5 py-0.5 rounded uppercase tracking-wider" :class="badgeClass(stackedItem.type)">
              {{ stackedItem.type }}
            </span>
            <h3 class="text-xs font-semibold text-slate-200 truncate max-w-md">
              {{ stackedItem.title }}
            </h3>
          </div>

          <!-- Actions -->
          <div class="flex items-center space-x-3">
            <span class="text-[10px] text-slate-500 font-mono">#{{ stack.length - index }}</span>
            <button 
              @click.stop="$emit('remove-item', stackedItem.id)"
              class="text-slate-500 hover:text-rose-400 transition-colors p-1"
            >
              <svg xmlns="http://w3.org" class="h-3.5 w-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
                <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
              </svg>
            </button>
          </div>
        </div>

        <!-- Working Input Document Area -->
        <div class="p-2 bg-[#0d0f16] flex flex-col min-h-0">
          <div class="grid relative after:content-[attr(data-replicated-value)'\A'] after:whitespace-pre-wrap after:invisible after:row-start-1 after:col-start-1 after:text-xs after:leading-relaxed after:p-1">
            <textarea 
              v-model="stackedItem.content"
              rows="1"
              @input="$el.parentElement.setAttribute('data-replicated-value', stackedItem.content)"
              class="w-full bg-transparent resize-none outline-none border-none text-[#cbd5e1] font-mono text-xs leading-relaxed focus:ring-0 placeholder-slate-600 row-start-1 col-start-1 p-1 overflow-hidden"
              placeholder="Content empty..."
            ></textarea>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Empty Selection Baseline Frame Layout -->
    <div v-else class="m-auto text-slate-500 text-center select-none">
      <svg xmlns="http://w3.org" class="h-10 w-10 mx-auto text-slate-700 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5">
        <path stroke-linecap="round" stroke-linejoin="round" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
      </svg>
      <p class="text-sm font-semibold">Workspace Stack is Empty</p>
      <p class="text-xs text-slate-600 mt-1">Select activity cards or click the transfer arrow to stack documents</p>
    </div>
  </section>
</template>

<script setup lang="ts">
import { watch, nextTick, onMounted } from 'vue'

const props = defineProps({
  stack: Array,
  focusedId: [String, Number]
})

defineEmits(['clear-stack', 'focus-item', 'remove-item'])

// watch(() => props.stack, async () => {
//   await nextTick()
//   document.querySelectorAll('textarea').forEach(el => {
//     if (el.parentElement && el.value) {
//       el.parentElement.setAttribute('data-replicated-value', el.value)
//     }
//   })
// }, { deep: true, immediate: true })

const refreshTextareaHeights = () => {
  document.querySelectorAll('textarea').forEach(el => {
    if (el.parentElement) {
      el.parentElement.setAttribute('data-replicated-value', el.value || '')
    }
  })
}

onMounted(() => {
  // Safe to watch now because the DOM is fully constructed and ready
  watch(() => props.stack, async () => {
    await nextTick()
    refreshTextareaHeights()
  }, { deep: true, immediate: true })
})


const badgeClass = (type) => {
  switch (type) {
    case 'note': return 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
    case 'prompt': return 'bg-purple-500/20 text-purple-400 border border-purple-500/30'
    case 'response': return 'bg-indigo-500/20 text-indigo-400 border border-indigo-500/30'
    default: return 'bg-slate-500/20 text-slate-400'
  }
}
</script>
