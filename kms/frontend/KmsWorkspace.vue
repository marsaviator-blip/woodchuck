<template>
  <!-- CORE WORKSPACE CONTAINER -->
  <div class="flex w-screen h-screen bg-[#0b0d12] text-[#f1f5f9] font-mono overflow-hidden">
    
    <!-- 1. LEFT SIDEBAR NAVIGATION PANE -->
    <aside class="w-[260px] bg-[#11141d] border-r border-[#1e293b] flex flex-col p-6">
      <div class="text-sm font-bold tracking-widest text-[#6366f1] uppercase mb-10">🧬 KMS</div>
      <nav class="flex flex-col gap-2">
        <button class="w-full text-left text-xs font-semibold px-4 py-3 rounded bg-indigo-600/10 text-indigo-400 border-l-4 border-indigo-500 rounded-l-none">
          📝 Active Session
        </button>
        <button class="w-full text-left text-xs font-semibold px-4 py-3 rounded text-[#64748b] hover:bg-[#1e293b] hover:text-[#f8fafc] transition-colors">
          📊 Category Logs
        </button>
        <button class="w-full text-left text-xs font-semibold px-4 py-3 rounded text-[#64748b] hover:bg-[#1e293b] hover:text-[#f8fafc] transition-colors">
          ⚙️ Settings
        </button>
      </nav>
    </aside>

    <!-- MAIN INTERACTIVE CONTENT AREA -->
    <main class="flex-1 flex flex-col h-full">
      <!-- 2. SPLIT CONTENT PANE LAYER -->
      <div class="grid grid-cols-2 h-full">
        
        <!-- LEFT HALF: LIVE WORKSPACE FEED -->
        <section class="flex flex-col h-full bg-[#0b0d12] border-r border-[#1e293b]">
          <div class="px-6 py-5 border-b border-[#1e293b] bg-[#11141d]">
            <h3 class="text-xs font-semibold uppercase tracking-wider text-[#94a3b8]">Live Feed (Notes & AI Prompts)</h3>
          </div>
          <div class="p-6 flex flex-col gap-6 overflow-y-auto flex-1">
            <!-- Scratchpad input block -->
            <textarea 
              v-model="activePrompt" 
              placeholder="Type a research note or AI prompt..."
              class="w-full h-[180px] bg-[#11141d] border border-[#1e293b] rounded-log p-4 text-sm text-[#f1f5f9] resize-none focus:outline-none focus:border-indigo-500 line-tight transition-colors"
            ></textarea>
            <button @click="sendToGemini" class="bg-[#4f46e5] hover:bg-[#4338ca] text-white text-xs font-bold px-6 py-3 rounded-md self-start transition-colors">
              Stream to Gemini
            </button>

            <!-- Real-time Gemini Streaming Area -->
            <div class="bg-[#11141d] border border-[#1e293b] p-5 rounded-md leading-relaxed text-sm" v-if="streamResponse">
              <strong class="block mb-2 text-indigo-300 text-xs uppercase tracking-wide">Gemini Response:</strong>
              <p class="text-[#cbd5e1]">{{ streamResponse }}</p>
            </div>
          </div>
        </section>

        <!-- RIGHT HALF: NEAR-TERM KNOWLEDGE VAULT -->
        <section class="flex flex-col h-full bg-[#090a0f]">
          <div class="px-6 py-5 border-b border-[#1e293b] bg-[#11141d]">
            <h3 class="text-xs font-semibold uppercase tracking-wider text-[#94a3b8]">Near-Term Knowledge Vault</h3>
          </div>
          <div class="p-6 flex flex-col gap-6 overflow-y-auto flex-1">
            <div class="text-xs text-[#64748b] leading-normal border border-dashed border-[#334155] p-4 rounded-md bg-[#1e293b]/20">
              Drag items here to curate them into your active session.
            </div>

            <!-- GHOST LINKS INTERACTIVE LAYER -->
            <div class="flex flex-col gap-3">
              <h4 class="text-xs uppercase tracking-wider text-[#94a3b8] mb-2">Suggested Category Connections (Llama + C++ Engine)</h4>
              <div v-for="link in ghostLinks" :key="link.id" class="border border-dashed border-[#6366f1] bg-indigo-500/[0.02] hover:bg-indigo-500/[0.05] p-5 rounded-md flex flex-col gap-3 transition-colors">
                <span class="font-semibold text-[#818cf8] text-sm">🔗 {{ link.source }} ➔ {{ link.target }}</span>
                <span class="text-[10px] text-[#64748b] bg-[#11141d] px-2 py-1 rounded border border-[#1e293b] self-start">{{ link.type }}</span>
                <div class="flex gap-3 mt-1">
                  <button @click="approveLink(link.id)" class="bg-[#065f46] text-[#34d399] border border-[#047857] hover:bg-[#047857] hover:text-white text-xs font-bold px-4 py-1.5 rounded transition-all">
                    Concur
                  </button>
                  <button @click="rejectLink(link.id)" class="bg-[#991b1b] text-[#fca5a5] border border-[#b91c1c] hover:bg-[#b91c1c] hover:text-white text-xs px-3 py-1.5 rounded transition-all">
                    ✕
                  </button>
                </div>
              </div>
            </div>
          </div>
        </section>

      </div>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const activePrompt = ref('');
const streamResponse = ref('');

const ghostLinks = ref([
  { id: 'g1', source: 'Titanium-Alloy-Note', target: 'Stress-Tensor-Math', type: 'DEPENDS_ON' },
  { id: 'g2', source: 'Laboratory-Protocol-A', target: 'Materials-Hardening', type: 'PREREQUISITE' }
]);

const sendToGemini = () => {
  streamResponse.value = "Streaming words from cloud API into Dragonfly cache...";
};

const approveLink = (id) => {
  ghostLinks.value = ghostLinks.value.filter(link => link.id !== id);
  console.log(`Link ${id} hard-committed to system ledger tables.`);
};

const rejectLink = (id) => {
  ghostLinks.value = ghostLinks.value.filter(link => link.id !== id);
  console.log(`Link ${id} rejected. Mathematical boundaries updated.`);
};
</script>
