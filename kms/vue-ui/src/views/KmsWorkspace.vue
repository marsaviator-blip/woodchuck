<template>
  <div class="tw-scope flex w-full h-full max-h-full overflow-hidden bg-[#0b0d12] text-[#f1f5f9] font-mono">

    <!-- CONTENT MESH SPLIT WRAPPER -->
    <main class="flex-1 flex flex-row h-full max-h-full overflow-hidden relative" 
      @mouseup="stopSplitResize" @mouseleave="stopSplitResize">
      
      <!-- COMPONENTIZED LEFT PANE -->
      <WorkspaceFeed :width="leftPaneWidth" :activityStream="activityStream" :activeItemId="activeItem?.id"
        :isStreaming="isStreaming" :streamingPrompt="streamingPrompt" :streamingBuffer="streamingBuffer"
        :isThinking="isThinking"
        @select-card="activeItem = $event" 
        @open-modal="openModal" 
        @submit-input="handleInputSubmission"
        @save-card="handleCardSessionToggle" 
        @save-session="handleSaveSession" />

      <!-- DRAGGABLE SPLIT SASH / RESIZER BAR -->
      <div @mousedown="startSplitResize"
        class="w-[4px] h-full bg-[#1e293b] hover:bg-[#6366f1] cursor-col-resize flex-shrink-0 transition-colors select-none">
      </div>

      <!-- COMPONENTIZED RIGHT PANE -->
      <div class="flex-1 h-full max-h-full overflow-hidden min-h-0 bg-[#11141d]">
        <WorkspaceSynthesis :markedCards="activeMarkedCards" />
      </div>
    </main>

    <!-- IMMERSIVE VIEW MODAL WINDOW CONTAINER -->
    <div v-if="isModalOpen"
      class="fixed inset-0 z-50 flex items-center justify-center p-10 bg-black/70 backdrop-blur-sm"
      @click.self="isModalOpen = false">
      <div
        class="bg-[#11141d] border border-[#1e293b] rounded-2xl w-full h-full max-w-5xl max-h-[85vh] flex flex-col shadow-2xl overflow-hidden">
        <div class="px-6 py-4 bg-[#161a26] border-b border-[#1e293b] flex items-center justify-between">
          <h2 class="text-md font-bold text-white truncate">{{ modalItem.title }}</h2>
          <button @click="isModalOpen = false"
            class="text-slate-400 hover:text-white transition-colors text-sm">✕</button>
        </div>
        <div
          class="flex-1 p-6 overflow-y-auto font-mono text-xs text-[#cbd5e1] bg-[#0d0f16] whitespace-pre-wrap leading-relaxed">
          {{ modalItem.content }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import WorkspaceFeed from '../components/WorkspaceFeed.vue'
import WorkspaceSynthesis from '../components/WorkspaceSynthesis.vue'
import Api from '../services/api.js'

// UI Layout Sizing Calculations
const leftPaneWidth = ref(360)
const isResizing = ref(false)
//const fixedSidebarWidth = 208 

const activeMarkedCards = computed(() => {
  return activityStream.value ? activityStream.value.filter(card => card.isMarkedForSession) : []
})

onMounted(() => {
  const fixedSidebarWidth = 256
  const availableWorkspaceWidth = window.innerWidth - fixedSidebarWidth
  leftPaneWidth.value = availableWorkspaceWidth / 2
  window.addEventListener('mousemove', handleGlobalSplitResize)
  window.addEventListener('mouseup', stopSplitResize)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleGlobalSplitResize)
  window.removeEventListener('mouseup', stopSplitResize)
})

const startSplitResize = (e) => {
  isResizing.value = true
  document.body.style.cursor = 'col-resize'
  document.body.style.userSelect = 'none'
}

const stopSplitResize = () => {
  isResizing.value = false
  document.body.style.cursor = ''
  document.body.style.userSelect = ''
}

const handleGlobalSplitResize = (e: MouseEvent) => {
  if (!isResizing.value) return
  const fixedSidebarWidth = 256
  const newWidth = e.clientX - fixedSidebarWidth
  const minWidth = 240
  const maxWidth = window.innerWidth - 300

  if (newWidth >= minWidth && newWidth <= maxWidth) {
    leftPaneWidth.value = newWidth
  }
}

const handleSplitResize = (e) => {
  if (!isResizing.value) return
  const newWidth = leftPaneWidth.value + e.movementX
  const minWidth = 240
  const maxWidth = window.innerWidth - 300

  if (newWidth >= minWidth && newWidth <= maxWidth) {
    leftPaneWidth.value = newWidth
  }
}

// Global Core Reactive States
const activeItem = ref(null)
const workspaceStack = ref([])
const focusedStackItemId = ref(null)

// Modal Immersive Frame Properties
const isModalOpen = ref(false)
const modalItem = ref({})
const openModal = (card) => {
  modalItem.value = card
  isModalOpen.value = true
}

const handleCardSessionToggle = async (card: any) => {
  card.isMarkedForSession = !card.isMarkedForSession

  try {
    await Api.saveToDragonfly({
      cardId: String(card.id),
      cardType: card.type || 'note',
      title: card.title || 'Untitled Card',
      content: card.content || '',
      sessionId: 'session_researcher_alpha', // Shared user context placeholder
      user: 'developer_alpha',
      prompt: card.isMarkedForSession ? 'STORE' : 'EVICT'
    })
    console.log(`➡️ Card state change pushed to central controller: ${card.id}`)
  } catch (err: any) {
    console.error('❌ Failed to route card toggle transaction to controller:', err)
  }
}


// 1. Rename the ref declaration at the top level
const isSaving = ref<boolean>(false)

const handleSaveSession = async () => {
  const markedCards = activityStream.value.filter(card => card.isMarkedForSession)
  if (markedCards.length === 0) return

  // 2. Set to true to trigger loading wheels or disable buttons
  isSaving.value = true 

  try {
    const result = await Api.saveSession({
      sessionId: `session_${Date.now()}`,
      user: 'developer_alpha',
      cards: markedCards
    })
    
    if (result && result.success) {
      // Handle success...
    }
  } catch (error) {
    console.error(error)
  } finally {
    // 3. Reset to false to restore standard UI interactions
    isSaving.value = false 
  }
}

// Bun Streaming Process Simulation Placeholders
const isStreaming = ref(false)
const isThinking = ref(false)
const streamingPrompt = ref('')
const streamingBuffer = ref('')

const activityStream = ref([
  { id: 1, type: 'note', title: 'C++ Buffer Mapping', date: 'Aug 14', 
    content: 'Direct engine memory optimization bypassing typical serialization boundaries.',
    isMarkedForSession: false 
  },
  { id: 2, type: 'prompt', title: 'Refactor Loop Speed Metrics', date: 'Aug 13', 
    content: 'Hook execution paths directly into active Bun web sockets loops.',
    isMarkedForSession: true
  }
])

const handleInputSubmission = ({ buffer, type }) => {
  const now = new Date()
  const exactTimeStr = now.toTimeString().split(' ')[0]

  isThinking.value = false
  
  if (type === 'note') {
    activityStream.value.push({
      id: Date.now(),
      type: type,
      title: 'Manual Work Log Entry',
      date: exactTimeStr,
      content: buffer,
      isMarkedForSession: false
    })
    return 
  }
  
  isStreaming.value = true
  streamingPrompt.value = buffer
  streamingBuffer.value = '' 
  
  activityStream.value.push({
    id: `prompt-${Date.now()}`,
    type: 'prompt',
    title: 'User Prompt Query',
    date: exactTimeStr,
    content: buffer,
    isMarkedForSession: false
  })

  const aiResponseId = `response-${Date.now()}`
  
  activityStream.value.push({
    id: aiResponseId,
    type: 'ai-response', 
    title: 'Gemini Assistant',
    date: exactTimeStr,
    content: '', 
    isMarkedForSession: false
  })

  // Exact callback schema mapping to prevent API file crashes
  Api.startChatStream({
    promptText: buffer,
    sessionId: 'session_researcher_alpha',
    cardId: aiResponseId,
    onChunk: (text: string) => {
      streamingBuffer.value += text
      const responseCard = activityStream.value.find(card => card.id === aiResponseId)
      if (responseCard) {
        responseCard.content += text
      }
    },
    onPromptsPartial: (text: string) => {},
    onPrompts: (prompts: string[]) => {},
    onComplete: () => {
      isStreaming.value = false
    },
    onError: (error: any) => {
      console.error(error)
      isStreaming.value = false
    }
  })
}
</script>

