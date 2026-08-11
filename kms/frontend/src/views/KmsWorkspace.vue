<template>
  <!-- CORE WORKSPACE CONTAINER (Forces exact viewport height metrics) -->
  <div class="tw-scope flex w-screen h-dvh bg-[#0b0d12] text-[#f1f5f9] font-mono overflow-hidden [height:100vh]">
    
    <!-- 1. LEFT SIDEBAR NAVIGATION PANE (98px Width) -->
    <aside class="w-[98px] bg-[#11141d] border-r border-[#1e293b] flex flex-col p-2 items-center flex-shrink-0">
      <div class="text-[9px] font-bold tracking-widest text-[#6366f1] uppercase mb-6 text-center">🧬 KMS</div>
      <nav class="flex flex-col gap-2 w-full">
        <button class="w-full text-center text-[10px] font-semibold py-2 rounded bg-indigo-600/10 text-indigo-400 border-l-2 border-indigo-500 rounded-l-none">
          📝 Act
        </button>
        <button class="w-full text-center text-[10px] font-semibold py-2 text-[#64748b] hover:bg-[#1e293b] hover:text-[#f8fafc] transition-colors">
          📊 Log
        </button>
        <button class="w-full text-center text-[10px] font-semibold py-2 text-[#64748b] hover:bg-[#1e293b] hover:text-[#f8fafc] transition-colors">
          ⚙️ Cfg
        </button>
      </nav>
    </aside>

    <!-- MAIN INTERACTIVE CONTENT AREA -->
    <main 
      class="flex-1 flex flex-row min-h-0 overflow-hidden relative" 
      @mousemove="handleSplitResize" 
      @mouseup="stopSplitResize" 
      @mouseleave="stopSplitResize"
    >
      
      <!-- ==================== LEFT HALF: LIVE WORKSPACE FEED ==================== -->
      <section 
        :style="{ width: leftPaneWidth + 'px' }" 
        class="flex flex-col h-[790px] bg-[#0b0d12] border-r border-[#1e293b] overflow-hidden flex-shrink-0"
      >
        <!-- Pane Header -->
        <div class="px-3 py-2 border-b border-[#1e293b] bg-[#11141d] flex-shrink-0">
          <h3 class="text-[9px] font-semibold uppercase tracking-wider text-[#94a3b8]">Live Activity Workspace</h3>
        </div>

        <!-- Chronological Activity Cards Stream (Strict Internal Scrolling Panel) -->
        <!-- <div class="p-3 flex flex-col gap-2 overflow-y-auto flex-1 bg-[#090a0e] min-h-0"> -->
        <div class="p-3 flex flex-col gap-2 overflow-y-auto flex-1 bg-[#090a0e] min-h-0 ">
          <div 
            v-for="card in activityStream" 
            :key="card.id" 
            class="relative border rounded p-2 transition-all group flex-shrink-0"
            :class="{
              'bg-[#121620] border-[#1e293b]': card.type === 'note',
              'bg-[#0f172a] border-indigo-900/30': card.type === 'prompt',
              'bg-[#111827] border-emerald-950/30': card.type === 'response'
            }"
          >
            <!-- Micro Badge metadata label -->
            <span 
              class="absolute top-1.5 right-1.5 text-[7px] font-bold uppercase tracking-wider px-1 py-0.2 rounded border"
              :class="{
                'text-amber-400 bg-amber-400/5 border-amber-500/20': card.type === 'note',
                'text-indigo-400 bg-indigo-400/5 border-indigo-500/20': card.type === 'prompt',
                'text-emerald-400 bg-emerald-400/5 border-emerald-500/20': card.type === 'response'
              }"
            >
              {{ card.type }}
            </span>

            <!-- High Density Micro Text Output Box -->
            <p class="text-[11px] text-[#cbd5e1] leading-normal pr-10 whitespace-pre-wrap selection:bg-indigo-500/30">{{ card.content }}</p>

            <!-- Dense Hover UI Action bar -->
            <div class="mt-1 pt-1 border-t border-[#1e293b]/20 flex items-center justify-between opacity-0 group-hover:opacity-100 transition-opacity">
              <span class="text-[8px] text-[#64748b]">{{ card.timestamp }}</span>
              <button 
                @click="curateCardSelection(card)"
                class="bg-indigo-600 hover:bg-indigo-500 text-white text-[8px] font-bold px-1.5 py-0.5 rounded transition-colors"
              >
                Select
              </button>
            </div>
          </div>
        </div>

        <!-- PANE BOTTOM INPUT FOOTER MODULE (Naturally pushed to baseline) -->
        <div class="p-2.5 border-t border-[#1e293b] bg-[#11141d] flex-shrink-0 flex flex-col gap-1.5">
          
          <!-- Toggle Selector Segment Switch -->
          <div class="flex bg-[#0b0d12] p-0.5 rounded border border-[#1e293b] self-start">
            <button 
              @click="inputType = 'note'"
              class="text-[8px] font-bold uppercase tracking-wider px-2 py-0.5 rounded transition-all"
              :class="inputType === 'note' ? 'bg-[#1e293b] text-amber-400 shadow-sm' : 'text-[#64748b] hover:text-[#cbd5e1]'"
            >
              📝 Note
            </button>
            <button 
              @click="inputType = 'prompt'"
              class="text-[8px] font-bold uppercase tracking-wider px-2 py-0.5 rounded transition-all"
              :class="inputType === 'prompt' ? 'bg-[#1e293b] text-indigo-400 shadow-sm' : 'text-[#64748b] hover:text-[#cbd5e1]'"
            >
              🤖 Prompt
            </button>
          </div>

          <!-- Compact Input Box & Action Button layout -->
          <div class="flex gap-2 items-end">
            <textarea 
              v-model="inputBuffer" 
              @input="handleLiveTyping"
              :placeholder="inputType === 'note' ? 'Log data observation...' : 'Query AI...'"
              class="flex-1 h-[42px] bg-[#0b0d12] border border-[#1e293b] rounded p-1.5 text-[11px] text-[#f1f5f9] resize-none focus:outline-none focus:border-indigo-500 line-tight transition-colors"
            ></textarea>
            <button 
              @click="submitInputPipeline" 
              class="text-[9px] font-bold px-3 rounded h-[30px] transition-colors flex-shrink-0"
              :class="inputType === 'note' ? 'bg-amber-600 hover:bg-amber-500 text-white' : 'bg-indigo-600 hover:bg-indigo-500 text-white'"
            >
              {{ inputType === 'note' ? 'Save' : 'Send' }}
            </button>
          </div>
        </div>
      </section>

      <!-- ==================== DRAGGABLE SPLIT SASH / RESIZER BAR ==================== -->
      <div 
        @mousedown="startSplitResize" 
        class="w-[4px] h-full bg-[#1e293b] hover:bg-[#6366f1] cursor-col-resize flex-shrink-0 transition-colors select-none"
      ></div>

      <!-- ==================== RIGHT HALF: FUTURE KNOWLEDGE VAULT ==================== -->
      <section class="flex-1 flex flex-col h-full bg-[#090a0f] overflow-hidden">
        <!-- Pane Header -->
        <div class="px-3 py-2 border-b border-[#1e293b] bg-[#11141d] flex-shrink-0">
          <h3 class="text-[9px] font-semibold uppercase tracking-wider text-[#94a3b8]">Staging & Relationship Matrix</h3>
        </div>
        
        <!-- Dense Right Body Panel (Independently Scrollable) -->
        <div class="p-3 flex flex-col gap-2 overflow-y-auto flex-1 max-h-full min-h-0">
          <div class="text-[10px] text-[#64748b] border border-dashed border-[#334155] p-2 rounded bg-[#1e293b]/10 leading-normal">
            Right pane design pipeline placeholder step.
          </div>
          <div v-for="n in 20" :key="n" class="p-2 border border-[#1e293b] bg-[#11141d] rounded text-[10px] text-[#94a3b8]">
            Staging Object Node Blueprint Placeholder Allocation Block #{{ n }}
          </div>
        </div>
      </section>

    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';
import { sendPromptToBun } from '../services/api' 
// PANE WIDTH RESIZING RESIZE LOGIC
const leftPaneWidth = ref(500); 
const isResizing = ref(false);
  
const startSplitResize = (e) => {
  isResizing.value = true;
  document.body.style.cursor = 'col-resize';
  e.preventDefault();
};

const handleSplitResize = (e) => {
  if (!isResizing.value) return;
  const calculatedWidth = e.clientX - 98; // Adjusted width mapping constraint offsets
  if (calculatedWidth > 200 && calculatedWidth < (window.innerWidth - 200)) {
    leftPaneWidth.value = calculatedWidth;
  }
};

const stopSplitResize = () => {
  if (isResizing.value) {
    isResizing.value = false;
    document.body.style.cursor = 'default';
  }
};

// INPUT REGISTRY WORKSPACE LAYER STATE
const inputType = ref('note');
const inputBuffer = ref('');
const sessionId = ref('session_researcher_alpha');
const isSending = ref<boolean>(false)

const activityStream = ref([
  { id: '1', type: 'note', content: 'Materials evaluation: Structural testing sequence using generic Titanium-6Al-4V specimens.', timestamp: '10:14 AM' },
  { id: '2', type: 'prompt', content: 'Calculate the expected tensile breakdown limit under multi-hop stress matrices.', timestamp: '10:15 AM' },
  { id: '3', type: 'response', content: 'Based on stress-tensor calculus, macro-fractures emerge at ~950 MPa inside unified boundaries.', timestamp: '10:15 AM' }
]);

let socket = null;

onMounted(() => {
  socket = new WebSocket('ws://localhost:3007');
  socket.onopen = () => console.log('✅ Connected to Bun Backend Matrix on Port 3007');
  socket.onclose = () => console.warn('❌ Severed socket communication pipeline.');
});

onUnmounted(() => {
  if (socket) socket.close();
});

const handleLiveTyping = () => {
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify({
      type: 'USER_TYPING',
      sessionId: sessionId.value,
      text: inputBuffer.value
    }));
  }
};

const curateCardSelection = (card) => {
  const selectedText = window.getSelection().toString().trim();
  if (selectedText.length > 0) {
    alert(`Curating Highlighted Selection:\n"${selectedText}"`);
  } else {
    alert(`Curating Entire ${card.type.toUpperCase()} Card Content.`);
  }
};

// const submitInputPipeline = () => {
//   if (!inputBuffer.value.trim()) return;

//   const timestamp = new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  
//   activityStream.value.push({
//     id: Date.now().toString(),
//     type: inputType.value,
//     content: inputBuffer.value,
//     timestamp: timestamp
//   });

//   if (inputType.value === 'prompt') {
//     setTimeout(() => {
//       activityStream.value.push({
//         id: (Date.now() + 1).toString(),
//         type: 'response',
//         content: `Simulated native background verification response block analyzing your parameter input string context matching session criteria.`,
//         timestamp: timestamp
//       });
//     }, 800);
//   }

//   inputBuffer.value = '';
// };
const submitInputPipeline = async () => {
  const text = inputBuffer.value.trim()
  if (!text) return

  // 1. Instantly log the user's action into the UI stream
  activityStream.value.push({
    id: Date.now(),
    type: inputType.value,
    content: text,
    timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
  })

  // Clear input field immediately for crisp user experience
  inputBuffer.value = ''

  // 2. Route prompts directly to your Bun server
  if (inputType.value === 'prompt') {
    isSending.value = true
    try {
      // Execute your clean api.ts wrapper function
      const data = await sendPromptToBun(text)


      // 3. Append the real backend response to the stream
      activityStream.value.push({
        id: Date.now() + 1,
        type: 'response',
        content: data.reply,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      })
    } catch (error: any) {
      console.error('API Error:', error)
      // Visual error fallback inside the UI card stream
      activityStream.value.push({
        id: Date.now() + 1,
        type: 'response',
        content: `⚠️ System Link Error: Could not reach backend engine. (${error.message})`,
        timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
      })
    } finally {
      isSending.value = false
    }
  }
}

</script>
