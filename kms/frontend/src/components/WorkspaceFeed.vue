<template>
  <section 
    :style="{ width: width + 'px' }"
    class="flex flex-col h-full max-h-full bg-[#0b0d12] border-r border-[#1e293b] overflow-hidden flex-shrink-0 min-h-0"
  >
    <!-- Pane Header -->
    <div class="px-3 py-2 border-b border-[#1e293b] bg-[#11141d] flex-shrink-0">
      <h3 class="text-[9px] font-semibold uppercase tracking-wider text-[#94a3b8]">Live Activity Workspace</h3>
    </div>

    <!-- Cards Scroll Container -->
    <div ref="scrollContainer" class="flex-1 overflow-y-auto p-2 space-y-2 custom-scrollbar min-h-0">
      <div v-for="card in activityStream" :key="card.id"
        class="group relative bg-slate-700/40 hover:bg-slate-700 border rounded-lg p-2.5 transition-all duration-150 shadow-sm cursor-pointer"
        :class="activeItemId === card.id ? 'border-indigo-500 bg-slate-700' : 'border-slate-700'"
        @click="$emit('select-card', card)" 
        @dblclick="$emit('open-modal', card)"
      >
        <!-- Card Header -->
        <div class="flex items-center justify-between mb-1.5 select-none h-4">
          <div class="flex items-center space-x-1.5">
            <span class="text-[9px] font-bold px-1.5 py-0.5 rounded uppercase tracking-tight" :class="badgeClass(card.type)">
              {{ card.type }}
            </span>
            <span class="text-[9px] text-slate-500 font-mono">{{ card.date }}</span>
          </div>

          <!-- Action Trigger -->
          <button
            class="opacity-0 group-hover:opacity-100 flex items-center justify-center p-1 rounded bg-indigo-600 hover:bg-indigo-500 text-white shadow transition-all duration-150 select-none"
            title="Highlight text inside card to send a snippet, or click directly to send all."
            @click.stop="handleSendToWorkspace(card)"
          >
            <svg xmlns="http://w3.org" class="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5">
              <path stroke-linecap="round" stroke-linejoin="round" d="M13 5l7 7-7 7M5 5l7 7-7 7" />
            </svg>
          </button>
        </div>

        <!-- Title -->
        <!-- <h3 class="font-bold text-xs mb-0.5 text-slate-200 group-hover:text-white line-clamp-1 selection:bg-indigo-500/40 tracking-tight">
          {{ card.title }}
        </h3> -->

        <!-- Clamped Content -->
        <p class="text-[11px] text-slate-400 line-clamp-5 leading-snug break-words selection:bg-indigo-500/40 font-sans">
          {{ card.content }}
        </p>
      </div>

      <!-- DYNAMIC STREAMING RESPONSE NODE (Active while Bun is pumping data) -->
      <div v-if="isStreaming" class="relative border border-indigo-500/40 rounded p-2 bg-[#11131f] flex-shrink-0 animate-pulse mt-2">
        <span class="absolute top-1.5 right-1.5 text-[7px] font-bold uppercase tracking-wider px-1 py-0.2 rounded border text-indigo-400 bg-indigo-500/10 border-indigo-500/30">
          streaming
        </span>
        <div class="text-[8px] text-[#64748b] mb-1 uppercase tracking-tight font-sans">
          Responding to: <span class="text-[#94a3b8] italic">"{{ streamingPrompt?.slice(0, 30) }}..."</span>
        </div>
        <p class="text-[11px] text-[#cbd5e1] leading-normal pr-10 whitespace-pre-wrap">
          {{ streamingBuffer }}<span class="inline-block w-1.5 h-3.5 bg-indigo-500 ml-0.5 align-middle animate-ping"></span>
        </p>
      </div>
    </div>

    <!-- PANE BOTTOM INPUT FOOTER MODULE (Fast to bottom frame) -->
    <div class="p-2.5 border-t border-[#1e293b] bg-[#11141d] flex-shrink-0 flex flex-col gap-1.5">
      <div class="flex bg-[#0b0d12] p-0.5 rounded border border-[#1e293b] self-start">
        <button @click="localInputType = 'note'"
          class="text-[8px] font-bold uppercase tracking-wider px-2 py-0.5 rounded transition-all"
          :class="localInputType === 'note' ? 'bg-[#1e293b] text-amber-400 shadow-sm' : 'text-[#64748b] hover:text-[#cbd5e1]'">
          📝 Note
        </button>
        <button @click="localInputType = 'prompt'"
          class="text-[8px] font-bold uppercase tracking-wider px-2 py-0.5 rounded transition-all"
          :class="localInputType === 'prompt' ? 'bg-[#1e293b] text-indigo-400 shadow-sm' : 'text-[#64748b] hover:text-[#cbd5e1]'">
          🤖 Prompt
        </button>
      </div>

      <div class="flex gap-2 items-end">
        <textarea 
          v-model="localInputBuffer" 
          :placeholder="localInputType === 'note' ? 'Log data observation...' : 'Query AI...'"
          class="flex-1 h-[42px] bg-[#0b0d12] border border-[#1e293b] rounded p-1.5 text-[11px] text-[#f1f5f9] resize-none focus:outline-none focus:border-indigo-500 line-tight transition-colors"
        ></textarea>
        <button 
          @click="handleSubmit" 
          :disabled="isStreaming"
          class="text-[9px] font-bold px-3 rounded h-[30px] transition-colors flex-shrink-0" 
          :class="[
            localInputType === 'note' ? 'bg-amber-600 hover:bg-amber-500 text-white' : 'bg-indigo-600 hover:bg-indigo-500 text-white',
            isStreaming ? 'opacity-40 cursor-not-allowed' : ''
          ]"
        >
          {{ localInputType === 'note' ? 'Save' : 'Send' }}
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'

const props = defineProps({
  width: Number,
  activityStream: Array,
  activeItemId: [Number, String],
  isStreaming: Boolean,
  streamingPrompt: String,
  streamingBuffer: String
})

const emit = defineEmits(['select-card', 'open-modal', 'send-to-workspace', 'submit-pipeline'])

const localInputBuffer = ref('')
const localInputType = ref('note')
const scrollContainer = ref(null)

const scrollToBottom = async () => {
  await nextTick()
  if (scrollContainer.value) {
    scrollContainer.value.scrollTo({
      top: scrollContainer.value.scrollHeight,
      behavior: 'smooth'
    })
  }
}

watch(() => props.activityStream.length, () => {
  scrollToBottom()
})

watch(() => props.streamingBuffer, () => {
  if (props.isStreaming) {
    scrollToBottom()
  }
})

const handleSendToWorkspace = (card) => {
  const selection = window.getSelection()
  const selectedText = selection.toString().trim()
  
  emit('send-to-workspace', { card, selectedText })
  selection.removeAllRanges()
}

const handleSubmit = () => {
  if (!localInputBuffer.value.trim()) return
  emit('submit-pipeline', { 
    buffer: localInputBuffer.value, 
    type: localInputType.value 
  })
  localInputBuffer.value = ''
}

const badgeClass = (type) => {
  switch (type) {
    case 'note': return 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
    case 'prompt': return 'bg-purple-500/20 text-purple-400 border border-purple-500/30'
    case 'response': return 'bg-indigo-500/20 text-indigo-400 border border-indigo-500/30'
    default: return 'bg-slate-500/20 text-slate-400'
  }
}
</script>
