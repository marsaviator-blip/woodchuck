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
        @submit-pipeline="handleInputSubmission"
        @toggle-session-mark="handleCardSessionToggle" />

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
import Api from '../services/api'

// UI Layout Sizing Calculations
const leftPaneWidth = ref(360)
const isResizing = ref(false)
const fixedSidebarWidth = 208 // Set directly to match Tailwind w-52 nav panel

const activeMarkedCards = computed(() => {
  return activityStream.value ? activityStream.value.filter(card => card.isMarkedForSession) : []
})

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
  { id: 1, type: 'note', title: 'C++ Buffer Mapping', date: 'Aug 14', 
  content: 'Direct engine memory optimization bypassing typical serialization boundaries.',
  isMarkedForSession: false 
},
  { id: 2, type: 'prompt', title: 'Refactor Loop Speed Metrics', date: 'Aug 13', 
  content: 'Hook execution paths directly into active Bun web sockets loops.',
  isMarkedForSession: true
 }
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

// const handleInputSubmission = ({ buffer, type }) => {
//   const now = new Date()
//   const exactTimeStr = now.toTimeString()
  
//   if (type === 'note') {
//     activityStream.value.push({
//       id: Date.now(),
//       type: type,
//       title: 'Manual Work Log Entry',
//       date: exactTimeStr,
//       content: buffer
//     })
//     return 
//   }
  
//   isStreaming.value = true
//   streamingPrompt.value = buffer
//   streamingBuffer.value = '' 
  
//   activityStream.value.push({
//     id: `prompt-${Date.now()}`,
//     type: 'prompt',
//     title: 'User Prompt Query',
//     date: exactTimeStr,
//     content: buffer
//   })

//   const aiResponseId = `response-${Date.now()}`
//   const targetIndex = activityStream.value.length
  
//   activityStream.value.push({
//     id: aiResponseId,
//     type: 'ai-response', 
//     title: 'Gemini Assistant',
//     date: exactTimeStr,
//     content: ''
//   })

//   // Define a variable outside or inside the method scope to hold the dynamic card ID
 let guideCardId: string | null = null;
 let rawPromptsTextBuffer = "";

// Api.startChatStream({
//   promptText: buffer,
//   sessionId: 'session_researcher_alpha',
//   cardId: aiResponseId,
  
//   // --- PHASE 1: Streams text tokens directly to Card #1 ---
//   onChunk: (textChunk: string) => {
//     streamingBuffer.value += textChunk;
//     const targetCard = activityStream.value.find(item => item.id === aiResponseId);
//     if (targetCard) {
//       targetCard.content += textChunk;
//     }
//   },

//   // --- NEW: Streams the JSON string tokens to Card #2 in real-time ---
//   onPromptsPartial: (partialTextChunk: string) => {
//     rawPromptsTextBuffer += partialTextChunk;

//     // If card #2 doesn't exist yet, spawn it immediately so the user sees it formatting
//     if (!guideCardId) {
//       guideCardId = `system-guide-${Date.now()}`;
//       activityStream.value.push({
//         id: guideCardId,
//         type: 'system_prompt',
//         title: 'System Cognitive Guide (Generating...)',
//         date: exactTimeStr,
//         content: '💡 DEEPER DIRECTED THINKING PATHWAYS:\n\nParsing critical pathways...'
//       });
//     }

//     // Keep the card visually updating with an incremental "thinking indicator" or the raw buffer
//     const guideCard = activityStream.value.find(item => item.id === guideCardId);
//     if (guideCard) {
//       // Stripping raw JSON brackets on the fly for cleaner real-time reading if desired,
//       // or keeping a generic loader until final parsing runs below
//       guideCard.content = `💡 DEEPER DIRECTED THINKING PATHWAYS:\n\nStructuring follow-up vectors...`;
//     }
//   },

//   // --- PHASE 2 COMPLETE: Overwrites Card #2 with beautifully formatted final data ---
//   onPrompts: (questionsArray: string[]) => {
//     const formattedGuideContent = 
//       `💡 DEEPER DIRECTED THINKING PATHWAYS:\n\n` +
//       `1. ${questionsArray[0] || 'Analyze underlying conceptual bounds.'}\n\n` +
//       `2. ${questionsArray[1] || 'Map structural analogies to past targets.'}`;

//     // Find the card we spawned during the partial phase and update its title and finalized content
//     const guideCard = activityStream.value.find(item => item.id === guideCardId);
//     if (guideCard) {
//       guideCard.title = 'System Cognitive Guide';
//       guideCard.content = formattedGuideContent;
//     } else {
//       // Fallback fallback if the stream was so blindingly fast it finished instantly
//       activityStream.value.push({
//         id: `system-guide-${Date.now()}`,
//         type: 'system_prompt',
//         title: 'System Cognitive Guide',
//         date: exactTimeStr,
//         content: formattedGuideContent
//       });
//     }
//   },
  
//   onComplete: () => {
//     isStreaming.value = false;
//     guideCardId = null; // Clear workspace scope state pointer for next run
//     rawPromptsTextBuffer = "";
//   },

//   onError: (err) => {
//     console.error("KMS Stream Interface Encountered an issue:", err);
//     isStreaming.value = false;
//     guideCardId = null;
//     rawPromptsTextBuffer = "";
//   }
// });
const handleInputSubmission = ({ buffer, type }) => {
  const now = new Date()
  const exactTimeStr = now.toTimeString().split(' ')[0]
  
  // 1. Handle regular note logs
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
  
  // 2. Set streaming UI states
  isStreaming.value = true
  streamingPrompt.value = buffer
  streamingBuffer.value = '' 
  rawPromptsTextBuffer = "" // Reset internal parsing block
  
  // 3. Append the user prompt to the activity stream array first
  activityStream.value.push({
    id: `prompt-${Date.now()}`,
    type: 'prompt',
    title: 'User Prompt Query',
    date: exactTimeStr,
    content: buffer,
    isMarkedForSession: false
  })

  // 4. Pre-generate the unique AI Response Card ID ahead of execution loops
  const aiResponseId = `response-${Date.now()}`
  
  // 5. Append the initial empty placeholder shell object into the array
  activityStream.value.push({
    id: aiResponseId,
    type: 'ai-response', 
    title: 'Gemini Assistant',
    date: exactTimeStr,
    content: '', // Fed incrementally via onChunk
    isMarkedForSession: false
  })

  // 6. Connect cleanly to your streaming API service layer
  Api.startChatStream({
    promptText: buffer,
    sessionId: 'session_researcher_alpha',
    cardId: aiResponseId,
    
    // Updates Card 1 (The initial text response)
    onChunk: (textChunk: string) => {
      streamingBuffer.value += textChunk
      
      // FIX: Find the pre-rendered card by ID inside our stream array
      const targetCard = activityStream.value.find(item => item.id === aiResponseId)
      if (targetCard) {
        targetCard.content += textChunk
      }
    },

    // Updates Card 2 Progressive JSON Generation
    onPromptsPartial: (partialTextChunk: string) => {
      rawPromptsTextBuffer += partialTextChunk;

      if (!guideCardId) {
        guideCardId = `system-guide-${Date.now()}`;
        activityStream.value.push({
          id: guideCardId,
          type: 'system_prompt',
          title: 'System Cognitive Guide (Generating...)',
          date: exactTimeStr,
          content: '💡 DEEPER DIRECTED THINKING PATHWAYS:\n\nStructuring follow-up vectors...',
          isMarkedForSession: false
        });
      }

      const cleanStreamDisplay = rawPromptsTextBuffer
        .replace(/[\[\]"']/g, '') // Strips raw braces out of stream view
        .replace(/\\n/g, '\n')
        .trim();

      const guideCard = activityStream.value.find(item => item.id === guideCardId);
      if (guideCard && cleanStreamDisplay.length > 0) {
        guideCard.content = `💡 DEEPER DIRECTED THINKING PATHWAYS:\n\n${cleanStreamDisplay}`;
      }
    },

    // Finalizes Card 2 with clean styling layout arrays
    onPrompts: (questionsArray: string[]) => {
      const formattedGuideContent = 
        `💡 DEEPER DIRECTED THINKING PATHWAYS:\n\n` +
        `1. ${questionsArray[0] || 'Analyze underlying conceptual bounds.'}\n\n` +
        `2. ${questionsArray[1] || 'Map structural analogies to past targets.'}`;

      const guideCard = activityStream.value.find(item => item.id === guideCardId);
      if (guideCard) {
        guideCard.title = 'System Cognitive Guide';
        guideCard.content = formattedGuideContent;
      }
    },
    
    onComplete: () => {
      isStreaming.value = false
      guideCardId = null
      rawPromptsTextBuffer = ""
    },

    onError: (err) => {
      console.error("KMS Stream Interface Encountered an issue:", err)
      isStreaming.value = false
      guideCardId = null
      rawPromptsTextBuffer = ""
    }
  })
}


// Inside views/KmsWorkspace.vue script setup
// const handleCardSessionToggle = (cardId) => {
//   const targetedCard = activityStream.value.find(item => item.id === cardId)
//   if (targetedCard) {
//     // Elegant boolean flipping mechanism
//     targetedCard.isMarkedForSession = !targetedCard.isMarkedForSession
    
//     // TRIGGER BACKEND BACKGROUND UPDATE PATHS
//     // Since we know what card is checked, we can transmit the revised array cohort list
//     // down to Bun/Elysia to automatically refresh the Right-hand aggregate topic summaries!
//     syncWorkspaceAnalyticsSummary()
//   }
// }
const handleCardSessionToggle = (cardId: number | string) => {
  // Find the clicked card inside our master reactive stream array
  const targetedCard = activityStream.value.find(item => item.id === cardId)
  
  if (targetedCard) {
    // Invert the boolean value (true becomes false, false becomes true)
    targetedCard.isMarkedForSession = !targetedCard.isMarkedForSession
    
    console.log(`Card ${cardId} session status updated to: ${targetedCard.isMarkedForSession}`)
    
    // Proactive step: You can now extract all currently marked card IDs to send to Bun!
    const activeCardIds = activityStream.value
      .filter(c => c.isMarkedForSession)
      .map(c => c.id)
      
    // Next step: pass activeCohortIds to Elysia backend to populate your Right Pane dashboards
  }
}
</script>
