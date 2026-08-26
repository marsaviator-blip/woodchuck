<template>
  <!-- <div class="tw-scope flex w-full h-full max-h-full min-h-0 bg-[#0b0d12] text-[#f1f5f9] font-mono overflow-hidden"> -->
  <div class="tw-scope flex w-full h-full max-h-full overflow-hidden bg-[#0b0d12] text-[#f1f5f9] font-mono">

    <!-- CONTENT MESH SPLIT WRAPPER -->
    <!-- <main class="flex-1 flex flex-row h-full max-h-full min-h-0 overflow-hidden relative" @mousemove="handleSplitResize" -->
    <main class="flex-1 flex flex-row h-full max-h-full overflow-hidden relative" 
      @mouseup="stopSplitResize" @mouseleave="stopSplitResize">
      
      <!-- COMPONENTIZED LEFT PANE -->
      <WorkspaceFeed :width="leftPaneWidth" :activityStream="activityStream" :activeItemId="activeItem?.id"
        :isStreaming="isStreaming" :streamingPrompt="streamingPrompt" :streamingBuffer="streamingBuffer"
        @select-card="activeItem = $event" @open-modal="openModal" @send-to-workspace="addCardToStack"
        @submit-pipeline="handleInputSubmission" />

      <!-- DRAGGABLE SPLIT SASH / RESIZER BAR -->
      <div @mousedown="startSplitResize"
        class="w-[4px] h-full bg-[#1e293b] hover:bg-[#6366f1] cursor-col-resize flex-shrink-0 transition-colors select-none">
      </div>

      <!-- COMPONENTIZED RIGHT PANE -->
      <WorkspaceStack :stack="workspaceStack" :focusedId="focusedStackItemId" @clear-stack="clearAllStack"
        @focus-item="focusedStackItemId = $event" @remove-item="removeFromStack" />
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
import { ref, onMounted } from 'vue'
import WorkspaceFeed from '../components/WorkspaceFeed.vue'
import WorkspaceStack from '../components/WorkspaceStack.vue'
import Api from '../services/api'

// UI Layout Sizing Calculations
const leftPaneWidth = ref(360)
const isResizing = ref(false)
const fixedSidebarWidth = 208 // Set directly to match Tailwind w-52 nav panel

onMounted(() => {
  // Updated to 256 to precisely map the fixed w-64 panel inside views/MainLayout.vue
  const fixedSidebarWidth = 256
  const availableWorkspaceWidth = window.innerWidth - fixedSidebarWidth

  // Set left pane to exactly half of the available workspace area
  leftPaneWidth.value = availableWorkspaceWidth / 2
  window.addEventListener('mousemove', handleGlobalSplitResize)
  window.addEventListener('mouseup', stopSplitResize)
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

  // Calculate the cursor position minus the physical left sidebar offset width
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

// Bun Streaming Process Simulation Placeholders
const isStreaming = ref(false)
const streamingPrompt = ref('Refactor memory array offsets')
const streamingBuffer = ref('Processing underlying native buffers inside C++ workspace layer...')

const activityStream = ref([
  { id: 1, type: 'note', title: 'C++ Buffer Mapping', date: 'Aug 14', content: 'Direct engine memory optimization bypassing typical serialization boundaries.' },
  { id: 2, type: 'prompt', title: 'Refactor Loop Speed Metrics', date: 'Aug 13', content: 'Hook execution paths directly into active Bun web sockets loops.' }
])

// Stacking Manipulation Core Rules
const addCardToStack = ({ card, selectedText }) => {
  let contentToPush = card.content
  let titleToUse = card.title

  if (selectedText && selectedText.length > 0) {
    contentToPush = selectedText
    titleToUse = `[Excerpt] ${selectedText.slice(0, 22)}...`
  }

  const uniqueId = `${card.id}-${Date.now()}`
  workspaceStack.value.push({
    id: uniqueId,
    title: titleToUse,
    type: card.type,
    content: contentToPush
  })
  focusedStackItemId.value = uniqueId
}

const removeFromStack = (id) => {
  workspaceStack.value = workspaceStack.value.filter(item => item.id !== id)
}
const clearAllStack = () => {
  workspaceStack.value = []
  focusedStackItemId.value = null
}

const handleInputSubmission = ({ buffer, type }) => {
  const now = new Date()
  const exactTimeStr = now.toTimeString()
  
  if (type === 'note') {
    activityStream.value.push({
      id: Date.now(),
      type: type,
      title: 'Manual Work Log Entry',
      date: exactTimeStr,
      content: buffer
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
    content: buffer
  })

  const aiResponseId = `response-${Date.now()}`
  const targetIndex = activityStream.value.length
  
  activityStream.value.push({
    id: aiResponseId,
    type: 'ai-response', 
    title: 'Gemini Assistant',
    date: exactTimeStr,
    content: ''
  })

  // Api.startChatStream({
  //   promptText: buffer,
  //   sessionId: 'session_researcher_alpha',
  //   cardId: aiResponseId, // FIX: Passed required cardId parameter to satisfy Elysia schemas
    
  //   onChunk: (textChunk: string) => {
  //     // Keep separate system-wide standalone buffer synced
  //     streamingBuffer.value += textChunk

  //     // 5. Locate the exact pre-rendered AI card inside the array to stream text
  //     const targetCard = activityStream.value.find(item => item.id === aiResponseId)
  //     if (targetCard) {
  //       targetCard.content += textChunk
  //     }
  //   },

  //   onPrompts: (questionsArray: string[]) => {
  //     // 6. THE SPLIT LOGIC: Spawn a distinct second card for the system guide prompts
  //     const systemGuideCardId = `system-guide-${Date.now()}`
      
  //     const formattedGuideContent = 
  //       `💡 DEEPER DIRECTED THINKING PATHWAYS:\n\n` +
  //       `1. ${questionsArray[0] || 'Analyze underlying conceptual bounds.'}\n\n` +
  //       `2. ${questionsArray[1] || 'Map structural analogies to past targets.'}`;

  //     activityStream.value.push({
  //       id: systemGuideCardId,
  //       type: 'system_prompt', // Distinct type parameter matching visual design templates
  //       title: 'System Cognitive Guide',
  //       date: exactTimeStr,
  //       content: formattedGuideContent
  //     })
  //   },
    
  //   onComplete: () => {
  //     isStreaming.value = false
  //   },

  //   onError: (err) => {
  //     console.error("KMS Stream Interface Encountered an issue:", err)
  //     isStreaming.value = false
  //   }
  // })}
  
  // Define a variable outside or inside the method scope to hold the dynamic card ID
let guideCardId: string | null = null;
let rawPromptsTextBuffer = "";

Api.startChatStream({
  promptText: buffer,
  sessionId: 'session_researcher_alpha',
  cardId: aiResponseId,
  
  // --- PHASE 1: Streams text tokens directly to Card #1 ---
  onChunk: (textChunk: string) => {
    streamingBuffer.value += textChunk;
    const targetCard = activityStream.value.find(item => item.id === aiResponseId);
    if (targetCard) {
      targetCard.content += textChunk;
    }
  },

  // --- NEW: Streams the JSON string tokens to Card #2 in real-time ---
  onPromptsPartial: (partialTextChunk: string) => {
    rawPromptsTextBuffer += partialTextChunk;

    // If card #2 doesn't exist yet, spawn it immediately so the user sees it formatting
    if (!guideCardId) {
      guideCardId = `system-guide-${Date.now()}`;
      activityStream.value.push({
        id: guideCardId,
        type: 'system_prompt',
        title: 'System Cognitive Guide (Generating...)',
        date: exactTimeStr,
        content: '💡 DEEPER DIRECTED THINKING PATHWAYS:\n\nParsing critical pathways...'
      });
    }

    // Keep the card visually updating with an incremental "thinking indicator" or the raw buffer
    const guideCard = activityStream.value.find(item => item.id === guideCardId);
    if (guideCard) {
      // Stripping raw JSON brackets on the fly for cleaner real-time reading if desired,
      // or keeping a generic loader until final parsing runs below
      guideCard.content = `💡 DEEPER DIRECTED THINKING PATHWAYS:\n\nStructuring follow-up vectors...`;
    }
  },

  // --- PHASE 2 COMPLETE: Overwrites Card #2 with beautifully formatted final data ---
  onPrompts: (questionsArray: string[]) => {
    const formattedGuideContent = 
      `💡 DEEPER DIRECTED THINKING PATHWAYS:\n\n` +
      `1. ${questionsArray[0] || 'Analyze underlying conceptual bounds.'}\n\n` +
      `2. ${questionsArray[1] || 'Map structural analogies to past targets.'}`;

    // Find the card we spawned during the partial phase and update its title and finalized content
    const guideCard = activityStream.value.find(item => item.id === guideCardId);
    if (guideCard) {
      guideCard.title = 'System Cognitive Guide';
      guideCard.content = formattedGuideContent;
    } else {
      // Fallback fallback if the stream was so blindingly fast it finished instantly
      activityStream.value.push({
        id: `system-guide-${Date.now()}`,
        type: 'system_prompt',
        title: 'System Cognitive Guide',
        date: exactTimeStr,
        content: formattedGuideContent
      });
    }
  },
  
  onComplete: () => {
    isStreaming.value = false;
    guideCardId = null; // Clear workspace scope state pointer for next run
    rawPromptsTextBuffer = "";
  },

  onError: (err) => {
    console.error("KMS Stream Interface Encountered an issue:", err);
    isStreaming.value = false;
    guideCardId = null;
    rawPromptsTextBuffer = "";
  }
});
}
</script>
