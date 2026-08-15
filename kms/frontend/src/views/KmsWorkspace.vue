<template>
  <!-- <div class="tw-scope flex w-screen h-screen max-h-screen bg-[#0b0d12] text-[#f1f5f9] font-mono overflow-hidden"> -->
  <div class="tw-scope flex w-full h-full max-h-full min-h-0 bg-[#0b0d12] text-[#f1f5f9] font-mono overflow-hidden">

    <!-- LEFT NAVIGATION BAR -->
    <aside
      class="w-[98px] bg-[#11141d] border-r border-[#1e293b] flex flex-col p-2 items-center flex-shrink-0 h-full max-h-full min-h-0">
      <!-- <aside class="w-[98px] bg-[#11141d] border-r border-[#1e293b] flex flex-col p-2 items-center flex-shrink-0 h-full select-none"> -->
      <div class="text-[9px] font-bold tracking-widest text-[#6366f1] uppercase mb-6 text-center">🧬 KMS</div>
      <nav class="flex flex-col gap-2 w-full">
        <button
          class="w-full text-center text-[10px] font-semibold py-2 rounded bg-indigo-600/10 text-indigo-400 border-l-2 border-indigo-500 rounded-l-none">📝
          Act</button>
        <button
          class="w-full text-center text-[10px] font-semibold py-2 text-[#64748b] hover:bg-[#1e293b] hover:text-[#f8fafc] transition-colors">📊
          Log</button>
        <button
          class="w-full text-center text-[10px] font-semibold py-2 text-[#64748b] hover:bg-[#1e293b] hover:text-[#f8fafc] transition-colors">⚙️
          Cfg</button>
      </nav>
    </aside>

    <!-- CONTENT MESH SPLIT WRAPPER -->
    <main class="flex-1 flex flex-row h-full max-h-full min-h-0 overflow-hidden relative" @mousemove="handleSplitResize"
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
<ChatStream 
  ref="chatStreamRef"
  @stream-chunk="handleIncomingChunk"
  @stream-complete="handleStreamCompletion"
/></template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import WorkspaceFeed from '../components/WorkspaceFeed.vue'
import WorkspaceStack from '../components/WorkspaceStack.vue'
import ChatStream from '../components/ChatStream.vue'

// UI Layout Sizing Calculations
const leftPaneWidth = ref(360)
const isResizing = ref(false)

onMounted(() => {
  const fixedSidebarWidth = 98
  const availableWorkspaceWidth = window.innerWidth - fixedSidebarWidth

  // Set left pane to exactly half of the available workspace area
  leftPaneWidth.value = availableWorkspaceWidth / 2
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
//const activityStream = ref([])

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
const chatStreamRef = ref(null)

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
  const exactTimeStr = now.toTimeString().split(' ')[0]
  if (type === 'note') {
    activityStream.value.push({
      id: Date.now(),
      type: type,
      title: type === 'note' ? 'Manual Work Log Entry' : 'Custom Prompt Workspace Execution',
      date: exactTimeStr,
      content: buffer
    })
    return // Exits early! No need for an "else" block anymore
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
  chatStreamRef.value?.startChatStream(buffer)
}

const handleIncomingChunk = (chunk) => {
  // If the Bun backend sends SSE updates with escaped literal breaks (\n), clean them
  const formattedChunk = chunk.replace(/\\n/g, '\n')
  streamingBuffer.value += formattedChunk
}

// 3. Triggered safely when ChatStream finishes or shuts down connection rules
const handleStreamCompletion = () => {
  // Turn off the pulsing streaming node layout
  isStreaming.value = false
  
  // If we didn't receive any content text, push a fallback warning card
  const finalContent = streamingBuffer.value.trim() || 'No data payload returned from the AI engine.'
  const completionTime = new Date().toTimeString().split(' ')[0]

  // SAVE THE AI RESPONSE TO A SEPARATE CARD AT THE BOTTOM
  activityStream.value.push({
    id: `response-${Date.now()}`,
    type: 'response',
    title: `AI Response: ${streamingPrompt.value.slice(0, 20)}...`,
    date: completionTime,
    content: finalContent
  })
}</script>
