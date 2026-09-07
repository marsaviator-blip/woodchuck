<template>
  <section 
    :style="{ width: width + 'px' }"
    class="flex flex-col h-full max-h-full bg-[#0b0d12] border-r border-[#1e293b] overflow-hidden flex-shrink-0 min-h-0"
  >
    <!-- Pane Header -->
    <div class="px-3 py-1.5 border-b border-[#1e293b] bg-[#11141d] flex-shrink-0 flex justify-between items-center">
      <h3 class="text-[9px] font-semibold uppercase tracking-wider text-[#94a3b8]">Live Activity Workspace</h3>
      <div class="flex items-center gap-2">
        <span class="text-[8px] font-mono bg-slate-800 text-slate-400 px-1.5 py-0.5 rounded border border-slate-700">
          📌 {{ markedCount }} Saved
        </span>
        <button
          v-if="markedCount > 0"
          @click="$emit('save-session')"
          class="text-[9px] px-2 py-0.5 rounded bg-indigo-600 hover:bg-indigo-500 text-white transition-colors"
          title="Save current selected session"
        >
          Save Session
        </button>
      </div>
    </div>

    <!-- Cards Scroll Container -->
    <div ref="scrollContainer" class="flex-1 h-0 overflow-y-auto p-1.5 space-y-1.5 custom-scrollbar min-h-0">
      <div v-for="card in activityStream" :key="card.id"
        class="group relative border rounded-lg p-2.5 transition-all duration-150 shadow-sm cursor-pointer"
        :class="[
          activeItemId === card.id ? 'bg-slate-800/50' : 'bg-slate-700/20 hover:bg-slate-700/40',
          card.isMarkedForSession ? 'border-amber-500/60 shadow-[0_0_10px_rgba(245,158,11,0.05)]' : activeItemId === card.id ? 'border-indigo-500' : 'border-slate-800'
        ]"
        @click="$emit('select-card', card)" 
        @dblclick="$emit('open-modal', card)"
      >
        <!-- Card Header -->
        <div class="flex items-center justify-between mb-1 select-none h-4">
          <div class="flex items-center space-x-1.5">
            <span class="text-[9px] font-bold px-1.5 py-0.5 rounded uppercase tracking-tight" :class="badgeClass(card.type)">
              {{ card.type }}
            </span>
            <span class="text-[9px] text-slate-500 font-mono">{{ card.date }}</span>
          </div>

          <!-- Session Pin Toggle Action -->
          <!-- CHANGED: Replaced the old structural copy-arrow icon with a toggleable session-pin bookmark -->
          <button
            class="flex items-center justify-center p-1 rounded transition-all duration-150 select-none border"
            :class="[
              card.isMarkedForSession 
                ? 'bg-amber-500/20 text-amber-400 border-amber-500/40 opacity-100' 
                : 'opacity-0 group-hover:opacity-100 bg-slate-800 hover:bg-slate-700 text-slate-400 hover:text-white border-slate-700'
            ]"
            title="Toggle inclusion inside current research tracking cohort session"
            @click.stop="$emit('save-card', card)"
          >
            <svg xmlns="http://w3.org" class="h-3 w-3" fill="currentColor" viewBox="0 0 24 24" v-if="card.isMarkedForSession">
              <path d="M17 3H7c-1.1 0-1.99.9-1.99 2L5 21l7-3 7 3V5c0-1.1-.9-2-2-2z"/>
            </svg>
            <svg xmlns="http://w3.org" class="h-3 w-3" fill="none" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2.5" v-else>
              <path stroke-linecap="round" stroke-linejoin="round" d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z" />
            </svg>
          </button>
        </div>

        <!-- Clamped Content View -->
        <p class="text-[11px] text-slate-400 line-clamp-5 leading-tight break-words selection:bg-indigo-500/40 font-sans">
          {{ card.content }}
        </p>
      </div>

      <!-- DYNAMIC STREAMING RESPONSE NODE -->
      <div v-if="isStreaming" class="border border-indigo-500/40 rounded p-2 bg-[#11131f] flex-shrink-0 animate-pulse mt-1.5 relative">
        <span class="absolute top-1.5 right-1.5 text-[7px] font-bold uppercase tracking-wider px-1 py-0.2 rounded border text-indigo-400 bg-indigo-500/10 border-indigo-500/30">
          streaming
        </span>
        <div class="text-[8px] text-[#64748b] mb-1 uppercase tracking-tight font-sans">
          Responding to: <span class="text-[#94a3b8] italic">"{{ streamingPrompt?.slice(0, 30) }}..."</span>
        </div>
        <p class="text-[11px] text-[#cbd5e1] leading-tight pr-10 whitespace-pre-wrap">
          {{ streamingBuffer }}<span class="inline-block w-1.5 h-3.5 bg-indigo-500 ml-0.5 align-middle animate-ping"></span>
        </p>
      </div>

      <!-- THINKING INDICATOR NODE -->
      <div v-if="isThinking" class="border border-sky-500/40 rounded p-2 bg-[#101826] flex-shrink-0 mt-1.5 relative">
        <span class="absolute top-1.5 right-1.5 text-[7px] font-bold uppercase tracking-wider px-1 py-0.2 rounded border text-sky-400 bg-sky-500/10 border-sky-500/30">
          thinking
        </span>
        <div class="text-[8px] text-[#64748b] mb-1 uppercase tracking-tight font-sans">
          Follow-up analysis
        </div>
        <p class="text-[11px] text-[#cbd5e1] leading-tight pr-10 whitespace-pre-wrap">
          Generating deeper prompts and synthesis…
        </p>
      </div>
    </div>

    <!-- PANE BOTTOM INPUT FOOTER MODULE -->
    <div class="p-1.5 border-t border-[#1e293b] bg-[#11141d] flex-shrink-0 flex flex-col gap-1">
      <div class="flex bg-[#0b0d12] p-0.5 rounded border border-[#1e293b] self-start">
        <button @click="localInputType = 'note'"
          class="text-[7.5px] font-bold uppercase tracking-wider px-1.5 py-0.5 rounded transition-all"
          :class="localInputType === 'note' ? 'bg-[#1e293b] text-amber-400 shadow-sm' : 'text-[#64748b] hover:text-[#cbd5e1]'">
          📝 Note
        </button>
        <button @click="localInputType = 'prompt'"
          class="text-[7.5px] font-bold uppercase tracking-wider px-1.5 py-0.5 rounded transition-all"
          :class="localInputType === 'prompt' ? 'bg-[#1e293b] text-indigo-400 shadow-sm' : 'text-[#64748b] hover:text-[#cbd5e1]'">
          🤖 Prompt
        </button>
      </div>

      <div class="flex gap-1.5 items-end">
        <textarea 
          v-model="localInputBuffer" 
          :placeholder="localInputType === 'note' ? 'Log data observation...' : 'Query AI...'"
          class="flex-1 h-[38px] bg-[#0b0d12] border border-[#1e293b] rounded p-1 text-[11px] text-[#f1f5f9] resize-none focus:outline-none focus:border-indigo-500 line-tight transition-colors"
        ></textarea>
        <button 
          @click="handleSubmit" 
          :disabled="isStreaming"
          class="text-[9px] font-bold px-2.5 rounded h-[38px] transition-colors flex-shrink-0 flex items-center justify-center" 
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
import { ref, computed, watch, nextTick } from 'vue'

const props = defineProps({
  width: Number,
  activityStream: Array,
  activeItemId: [Number, String],
  isStreaming: Boolean,
  isThinking: Boolean,
  streamingPrompt: String,
  streamingBuffer: String
})

// ADDED: Emits a structural toggle handler event up to parent memory state managers
const emit = defineEmits(['select-card', 'open-modal', 'save-card', 'submit-input', 'save-session'])

const localInputBuffer = ref('')
const localInputType = ref('note')
const scrollContainer = ref(null)

// Computed indicator tracking total active cohort members
const markedCount = computed(() => {
  return props.activityStream ? props.activityStream.filter(c => c.isMarkedForSession).length : 0
})

const scrollToBottom = async () => {
  await nextTick()
  if (scrollContainer.value) {
    scrollContainer.value.scrollTo({
      top: scrollContainer.value.scrollHeight,
      behavior: 'smooth'
    })
  }
}

watch(() => props.activityStream?.length, () => {
  scrollToBottom()
})

watch(() => props.streamingBuffer, () => {
  if (props.isStreaming) {
    scrollToBottom()
  }
})

const handleSubmit = () => {
  if (!localInputBuffer.value.trim()) return
  emit('submit-input', { 
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
    case 'system_prompt': return 'bg-sky-500/20 text-sky-400 border border-sky-500/30'
    default: return 'bg-slate-500/20 text-slate-400'
  }
}
</script>
