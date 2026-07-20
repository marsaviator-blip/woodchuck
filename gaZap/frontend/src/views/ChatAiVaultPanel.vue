<template>
    <div class="tailwind-page">
        <!-- <div class="flex h-screen w-full bg-slate-900 text-slate-100 p-4 gap-2 overflow-hidden font-sans select-none" -->
        <div class="flex flex-row items-stretch h-screen w-full bg-slate-900 text-slate-100 p-4 gap-2 overflow-hidden font-sans"
            @mousemove="handleResizeMove" @mouseup="handleResizeEnd" @mouseleave="handleResizeEnd">
            <!-- COLUMN 1: AI Chat Window -->
            <div :style="{ width: `${chatWidth}%` }"
                class="flex flex-col bg-slate-800 rounded-xl border border-slate-700 overflow-hidden relative">
                <!-- Chat Header -->
                <div class="p-4 border-b border-slate-70 0 bg-slate-850 flex justify-between items-center">
                    <h2 class="font -bold text-lg text-emerald-400 flex items-center gap-2"> <span>🤖 </span> AI
                        Assistant
                    </h2> <span class="text-xs text-slate-400">Highlight text to save snippets</span>
                </div>
                <!-- Chat Messages Area -->
                <div ref="chatContainer" class="flex-1 overflow-y-auto p-4 space-y-4" @mouseup="handleTextSelection">
                    <div v-for="msg in chatMessages" :key="msg.id"
                        :class="['flex flex-col w-full rounded-lg p-3 relative group transition-all', 
                                 msg.role === 'user' ? 'ml-auto bg-slate-700/60 border-2 border-blue-500 text-slate-100 shadow-md' : 'bg-slate-70 0 text-slate-100']">
                    <!-- Role Label -->
                        <span class=" text-[10px] uppercase tracking-wider opacity-60 mb-1 font-bold">
                            {{ msg.role }}
                        </span>
                        <!-- Message Content -->
                        <p class="text-sm leading -relaxed whitespace-pre-line">{{ msg.content }}</p>
                        <!-- Quick Action: Add entire message to Vault ( Hover button) -->

                        <button @click="addToVault(msg.content)"
                            class="absolute top-2 right-2 opacity-0 group-hover:opacity-100 transition-opacity bg- slate-900/80 hover:bg-emerald-600 text-white text-xs py -1 px-2 rounded flex items-center gap-1 shadow-lg"
                            title="Save entire message to Vault"> 📥 Save to Vault </button>
                    </div>
                </div>
            <div class="p-4 border-t border-slate-700 bg-slate-850 flex flex-col gap-3 flex-shrink-0">
                <!-- Input Mode Switcher Row -->
                <div class="flex gap-2 text-xs select-none">
                    <button @click="inputMode = 'ai'"
                        :class="['px-2.5 py-1 rounded font-medium transition', inputMode === 'ai' ? 'bg-blue-600 text-white' : 'bg-slate-700 text-slate-400 hover:text-slate-200']">
                        💬 Ask AI Assistant
                    </button>
                    <button @click="inputMode = 'note'"
                        :class="['px-2.5 py-1 rounded font-medium transition', inputMode === 'note' ? 'bg-amber-600 text-white' : 'bg-slate-700 text-slate-400 hover:text-slate-200']">
                        📝 Write My Note
                    </button>
                </div>

                <!-- Core Form Action Frame -->
                <div class="flex gap-2">
                    <input v-model="userInput" @keyup.enter="handleInputSubmit" type="text"
                        :placeholder="inputMode === 'ai' ? 'Ask AI something...' : 'Type a personal note to clip later...'"
                        :class="['flex-1 bg-slate-900 border rounded-lg px-4 py-2 text-sm focus:outline-none transition-colors select-text',
                            inputMode === 'ai' ? 'border-slate-700 focus:border-emerald-500' : 'border-amber-600/50 focus:border-amber-500']" />

                    <button @click="handleInputSubmit" :class="['px-4 py-2 rounded-lg text-sm font-semibold transition select-none text-white',
                        inputMode === 'ai' ? 'bg-emerald-600 hover:bg-emerald-500' : 'bg-amber-600 hover:bg-amber-500']">
                        {{ inputMode === 'ai' ? 'Send' : 'Post' }}
                    </button>
                </div>
            </div>            </div>
                <!-- Chat Input Frame -->


            <!-- Floating "Add Selection" Button (Appears when text is highlighted) -->
            <Transition name="fade-bounce">
                <button v-if="selectionCoords.show"
                    :style="{ top: `${selectionCoords.y}px`, left: `${selectionCoords.x}px` }"
                    @mousedown.prevent.stop="addSelectedToVault"
                    class="save-selection-btn fixed z-50 bg-emerald-500 hover:bg-emerald-400 text-slate-950 font-bold text-xs py-1.5 px-3 rounded-lg shadow-xl flex items-center gap-1 group">
                    <!-- Optional spark icon that bounces slightly on hover -->
                    <span class="group-hover:animate-spin">✨</span> Save Selection
                </button>
            </Transition>
            <!-- Chat Input -->
            <!-- <div class="p-4 border-t border-slate-700 bg-slate-850 flex gap-2 ">
                    <input v-model="userInput" @keyup.enter="sendMessage" type="text" placeholder="Ask AI something..."
                        class="flex-1 bg-slate-90 0 border border-slate-700 rounded-lg px-4 py-2 text-sm focus:outline-none focus:border-emerald-500" />
                    <button @click="sendMessage"
                        class="bg-emerald-600 hover:bg-emerald-500 px-4 py -2 rounded-lg text-sm font-semibold transition">
                        Send </button>
                </div> -->

            <!-- RESIZABLE SPLIT HANDLEBAR -->
            <div class="w-2 hover:w-3 bg-slate-700/50 hover:bg-emerald-500 rounded-full cursor-col-resize transition-all self-stretch my-2 flex items-center justify-center group"
                @mousedown="handleResizeStart">
                <!-- Optional visual drag dots icon -->
                <div class="w-1 h-8 bg-slate-500 group-hover:bg-slate-950 rounded-full opacity-60"></div>
            </div>
            <!-- COLUMN 2: Knowledge Vault -->
            <div :style="{ width: `calc(${100 - chatWidth}% - 4px)` }"
                class="flex flex-col bg-slate-800 rounded-xl border border-slate-700 overflow-hidden flex-shrink-0 h-full">
                <!-- Vault Header -->
                <div class="p-4 border-b border-slate-700 bg-slate-850 flex justify-between items-center">
                    <h2 class="font-bold text-lg text-emerald-400 flex items-center gap-2">
                        <span>🗄️</span>Knowledge Vault
                    </h2>
                    <span class="bg-slate-700 text-xs px-2.5 py-1 rounded-full text-slate-300 font-semibold">
                        {{ vaultItems.length }} Items </span>
                </div>
                <!-- Vault Content -->
                <div class="flex-1 overflow-y-auto p-4 space-y-3 flex flex-col items-stretch w-full select-text">
                    <!-- Empty State -->
                    <div v-if="vaultItems.length === 0"
                        class="h-full flex flex-col items-center justify-center text-slate-500 space-y-2">
                        <span>📥</span>
                        <p class="text-sm">Vault is empty.</p>
                        <p class="text-xs text-center max-w-[250px]">
                            Highlight chat text or click "Save to Vault" on a message to add it here.</p>
                    </div>
                    <!-- Vault Cards -->
                    <div v-for="item in vaultItems" :key="item.id"
                        class="w-full bg-slate-700 border border-slate-700 rounded-lg p-3 hover:border-emerald-500/50 transition-colors group relative">
                        <div class="flex justify-between items-start mb-2">
                            <span class="text-[10px] text-emerald-400 font-mono">{{ item.timestamp }}</span>
                            <button @click="removeFromVault(item.id)"
                                class="text-slate-500 hover:text-red-400 text-xs opacity-0 group-hover:opacity-100 transition-opacity">
                                Delete
                            </button>
                        </div>
                        <p class="text-sm text-slate-200 whitespace-pre-line">{{ item.content }}</p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script setup lang="ts">
import { ref, onMounted, onUnmounted, nextTick } from 'vue';

// --- STATE --- 
const userInput = ref('');
const chatContainer = ref<HTMLElement | null>(null);
const selectedText = ref('');
const selectionCoords = ref({ show: false, x: 0, y: 0 });

// Split Panel State (Defaults to 50% if no prior cache exists)
const chatWidth = ref(50);
const isResizing = ref(false);

// Mock Data Configurations
const chatMessages = ref([
    { id: 1, role: 'assistant', content: "Hello! I am your AI assistant. Ask me anything, and you can easily save my responses (or specific snippets of them) directly into your Knowledge Vault on the right." },
    { id: 2, role: 'user', content: "Can you give me a quick summary of photosynthesis?" },
    { id: 3, role: 'assistant', content: "Photosynthesis is the process used by plants to convert light energy into chemical energy. \n\nKey formula: Carbon Dioxide + Water + Light -> Glucose + Oxygen. \n\nThis happens inside the Chloroplasts." }
]);
const vaultItems = ref([
    { id: 1, content: "Initial test note: You can delete this.", timestamp: "10:00:00 AM" }
]);

// --- SCROLL UTILITY ---
const scrollToBottom = async () => {
    await nextTick();
    if (chatContainer.value) {
        chatContainer.value.scrollTop = chatContainer.value.scrollHeight;
    }
};

// --- CHAT METHODS ---
// const sendMessage = () => {
//     if (!userInput.value.trim()) return;

//     chatMessages.value.push({
//         id: Date.now(), role: 'user', content: userInput.value
//     });

//     const prompt = userInput.value;
//     userInput.value = '';
//     scrollToBottom();

//     setTimeout(() => {
//         chatMessages.value.push({
//             id: Date.now() + 1,
//             role: 'assistant',
//             content: `Here is some information about "${prompt}". This is a simulated AI response. Feel free to highlight any part of this text to save it to your vault.`
//         });
//         scrollToBottom();
//     }, 800);
// };

const addToVault = (text: string) => {
    if (!text || !text.trim()) return;
    vaultItems.value.unshift({
        id: Date.now(),
        content: text.trim(),
        timestamp: new Date().toLocaleTimeString()
    });
    clearSelection();
};

const removeFromVault = (id: number) => {
    vaultItems.value = vaultItems.value.filter(item => item.id !== id);
};

// --- SPLITTER RESIZE METHODS WITH LOCALSTORAGE ---
const handleResizeStart = (e: MouseEvent) => {
    e.preventDefault();
    isResizing.value = true;
    clearSelection(); // Hide selection button immediately when starting a resize drag
};

const handleResizeMove = (e: MouseEvent) => {
    if (!isResizing.value) return;
    const containerWidth = window.innerWidth;
    let newPercentage = (e.clientX / containerWidth) * 100;

    if (newPercentage < 20) newPercentage = 20;
    if (newPercentage > 80) newPercentage = 80;

    chatWidth.value = newPercentage;
};

const handleResizeEnd = () => {
    if (isResizing.value) {
        isResizing.value = false;
        // BULLET 3: Save panel configuration state smoothly into LocalStorage cache
        localStorage.setItem('workspace-chat-width', String(chatWidth.value));
    }
};

// --- FLOATING SELECTION LOGIC ---
const handleTextSelection = async () => {
    await nextTick();
    const selection = window.getSelection();

    // Safety check: Exit if selection is dropped or collapsed
    if (!selection || selection.isCollapsed) {
        clearSelection();
        return;
    }

    const text = selection.toString().trim();
    if (!text) {
        clearSelection();
        return;
    }

    selectedText.value = text;

    // BULLET 1: Viewport target calculation
    const range = selection.getRangeAt(0);
    const rect = range.getBoundingClientRect();

    // Calculate precise layout coordinates relative directly to the screen viewport bounds
    selectionCoords.value = {
        show: true,
        x: rect.left + (rect.width / 2) - 65, // Centers the button horizontally over highlighted area
        y: rect.top - 42 // Floats button slightly above the selection zone
    };
};

const addSelectedToVault = () => {
    if (selectedText.value) {
        addToVault(selectedText.value);
    }
};

const clearSelection = () => {
    selectedText.value = '';
    selectionCoords.value.show = false;
};

// Global mousedown listener cleans up state safely
const handleGlobalMouseDown = (e: MouseEvent) => {
    const target = e.target as HTMLElement;
    if (target && target.closest('.save-selection-btn')) return;
    clearSelection();
};

// Automatically clear the floating selection box if the user scrolls the chat logs
const handleChatScroll = () => {
    if (selectionCoords.value.show) {
        clearSelection();
    }
};

onMounted(() => {
    window.addEventListener('mousedown', handleGlobalMouseDown);

    // Retain workspace configuration on mount reload
    const savedWidth = localStorage.getItem('workspace-chat-width');
    if (savedWidth) {
        chatWidth.value = parseFloat(savedWidth);
    }

    // Scroll listeners guard coordinates from detaching during view updates
    chatContainer.value?.addEventListener('scroll', handleChatScroll);
});

onUnmounted(() => {
    window.removeEventListener('mousedown', handleGlobalMouseDown);
    chatContainer.value?.removeEventListener('scroll', handleChatScroll);
});
// --- STATE CONFIGURATIONS ---
const inputMode = ref<'ai' | 'note'>('ai'); // Tracks current input selection window

// --- CORE INPUT MANAGEMENT METHODS ---
const handleInputSubmit = () => {
    if (!userInput.value.trim()) return;

    if (inputMode.value === 'note') {
        // Mode 1: Route text as a personal highlighted note entry
        chatMessages.value.push({
            id: Date.now(),
            role: 'note',
            content: userInput.value.trim()
        });
        userInput.value = '';
        scrollToBottom();
    } else {
        // Mode 2: Standard AI pipeline submission
        sendMessage();
    }
};

// Modified native AI sender mechanism
const sendMessage = () => {
    chatMessages.value.push({
        id: Date.now(), 
        role: 'user', 
        content: userInput.value
    });
    
    const prompt = userInput.value; 
    userInput.value = '';
    scrollToBottom();
    
    setTimeout(() => {
        chatMessages.value.push({
            id: Date.now() + 1,
            role: 'assistant',
            content: `Here is some information about "${prompt}". This is a simulated AI response. Feel free to highlight any part of this text to save it to your vault.`
        });
        scrollToBottom();
    }, 800);
};

</script>

<style scoped>
/* Custom Scrollbar for sleek UI */
::-webkit-scrollbar {
    width: 6px;
}

::-webkit-scrollbar-track {
    background: transparent;
}

::-webkit-scrollbar-thumb {
    background: #475569;
    border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
    background: #10b981;
}

/* BULLET 2: Vue Transition rules for smooth fade-in and scale bounce */
.fade-bounce-enter-active,
.fade-bounce-leave-active {
    transition: transform 0.2s cubic-bezier(0.34, 1.56, 0.64, 1), opacity 0.15s ease;
}

.fade-bounce-enter-from,
.fade-bounce-leave-to {
    opacity: 0;
    transform: scale(0.85) translateY(8px);
}
</style>
