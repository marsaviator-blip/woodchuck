<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue' // --- STATE --- 
const messages = ref([
    { id: 1, sender: 'AI', text: 'Hello! I analyzed the data. The conversion rate increased by 15% after we implemented the new onboarding flow.' },
    { id: 2, sender: 'User', text: 'That is great news. What was the exact period of the test?' },
    { id: 3, sender: 'AI', text: 'The test ran from October 1st to October 14th. We should document this in our Q4 reports under "Onboarding Optimizations".' },
])
const vaultItems = ref([
    {
        id: 1,
        content: 'Q4 Goal: Increase onboarding conversion by 10%',
        date: '10/24/2023'
    }
])
const newMessage = ref('') // For the text selection popup 
const selectionCoords = ref({ x: 0, y: 0 })
const selectedText = ref('')
const showSelectionMenu = ref(false)
// --- METHODS --- // Send a new chat message 
const sendMessage = () => {
    if (!newMessage.value.trim()) return messages.value.push({
        id: Date.now(),
        sender: 'User',
        text: newMessage.value
    })
    newMessage.value = ''
}
// Add whole message to Vault 
const addToVault = (text: string) => {
    if (!text) return vaultItems.value.unshift({
        id: Date.now(),
        content: text,
        date: new Date().toLocaleDateString()
    })
}
// Delete item from Vault 
const deleteFromVault = (id) => {
    vaultItems.value = vaultItems.value.filter(item => item.id !== id)
}
// Handle Text Selection 
const handleTextSelection = (event: { clientX: number; clientY: number }) => {
    const selection = window.getSelection()
    const text = selection.toString().trim()
    if (text.length > 0) {
        selectedText.value = text
        // Position the popup near the mouse cursor 
        selectionCoords.value = {
            x: event.clientX,
            y: event.clientY - 40 // slightly above cursor
        }
        showSelectionMenu.value = true
    } else {
        showSelectionMenu.value = false
    }
}
// Add selected text and close popup 
const saveSelectedText = () => {
    addToVault(selectedText.value)
    showSelectionMenu.value = false
    window.getSelection().removeAllRanges() // Clear highlight 
}
// Close selection popup if clicking elsewhere 
// const closePopupOnOutsideClick = (e: { target: { closest: (arg0: string) => any } }) => {
//     if (!e.target.closest('.selection-popup')) {
//         showSelectionMenu.value = false
//     }
// }
// Close selection popup if clicking elsewhere 
const closePopupOnOutsideClick = (e: MouseEvent) => {
    const target = e.target as HTMLElement
    if (target && !target.closest('.selection-popup')) {
        showSelectionMenu.value = false
    }
}

onMounted(() => {
    document.addEventListener('click', closePopupOnOutsideClick)
})
onUnmounted(() => {
    document.removeEventListener('click', closePopupOnOutsideClick)
})
</script>
<template>
    <div class="bounding-panel">
        <div class="tailwind-page">
            <div class="flex h-screen w-full bg-slate-900 text- slate-100 p-4 gap-4 font-sans">
                <!-- COLUMN 1: CH AT WINDOW -->
                <div class="w-1/2 flex flex-col bg-slate-80 0 rounded-xl border border-slate-700 overflow-hidden">
                    <!-- Header -->
                    <div class="p-4 border-b border-slate-700 bg-slate-800/5 0 flex justify-between items-center">
                        <h2 class="text-lg font-bold text-emerald -400">Chat Assistant</h2> <span
                            class="text-xs text-slate-400 ">Highlight text to save snippets</span>
                    </div>
                    <!-- Messages Area -->
                    <div class="flex-1 p-4 overflow-y-auto space-y-4" @mouseup="handleTextSelection">
                        <div v-for="msg in messages" :key="msg.id"
                            class="group relative flex flex-col p-3 rounded-lg max-w-[85%] transition-all"
                            :class="msg.sender === 'User' ? 'bg-slate-800 ml-auto border border-slate-700' : 'bg-slate-700/50 mr-auto border border-slate-700/50'">

                            <!-- Sender Label -->
                            <span class="text-xs font-semibold mb-1"
                                :class="msg.sender === 'User' ? 'text-blue-400' : 'text-emerald-400'">
                                {{ msg.sender }}
                            </span>

                            <!-- Message Text -->
                            <p class="text-sm leading-relaxed text-slate-200 select-text">{{ msg.text }}</p>

                            <!-- Hover Action: Add entire message to Vault -->
                            <button @click.stop="addToVault(msg.text)" title="Add full message to Vault"
                                class="absolute top-2 right-2 hidden group-hover:flex items-center justify-center bg-slate-600 hover:bg-emerald-600 p-1.5 rounded text-white transition-colors shadow-lg">
                                <svg xmlns="http://w3.org" class="h-4 w-4" fill="none" viewBox="0 0 24 24"
                                    stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M5 5a2 2 0 012-2h10a2 2 0 012 2v16l-7-3.5L5 21V5z" />
                                </svg>
                            </button>
                        </div>
                    </div>
                    <!-- Input Area -->
                    <form @submit.prevent="sendMessage"
                        class="p-4 border-t border-slate-700 bg-slate-800/80 flex gap-2">
                        <input v-model="newMessage" type="text" placeholder="Type your message..."
                            class="flex-1 bg-slate-900 border border-slate-700 rounded-lg px-4 py-2 text-sm focus:outline-none focus:border-emerald-500 text-slate-100" />
                        <button type="submit"
                            class="bg-emerald-600 hover:bg-emerald-5 00 text-white px-4 py-2 rounded-lg text-sm font-semibold transition-colors">
                            Send 
                        </button>
                    </form>
                </div> 
                <!-- COLUMN 2: KNOWLEDGE VAULT -->
                <div class="w-1/2 flex flex-col bg-slate-800 rounded-xl border border-slate-700 overflow-hidden">
                    <!-- Header -->
                    <div class="p-4 border-b border-slate-700 bg-slate-800/50">
                        <h2 class="text-lg font-bold text-amber-4 00">Knowledge Vault</h2>
                    </div> 
                    <!-- Vault Items Area -->
                    <div class="flex-1 p-4 overflow-y-auto space-y-3">
                        <div v-if="vaultItems.length === 0"
                            class="h-full flex flex-col items-center justify-center text-slate-500"> <svg
                                xmlns="http://www.w3.org/2000/ svg" class="h-12 w-12 mb-2" fill="none"
                                viewBox="0 0 2 4 24" stroke="currentColor">
                                <path stroke-linecap="round" stroke-linejoin="round " stroke-width="1.5"
                                    d="M8 4H6a2 2 0 00-2 2v12a2 2 0 002 2h12 a2 2 0 002-2V6a2 2 0 00- 2-2h-2m-4-1v8m0 0l3-3m-3 3L 9 8m-5 5h2.586a1 1 0 01. 707.293l2.414 2.414a1 1 0 0 0.707.293h3.172a1 1 0 00.70 7-.293l2.414-2.414a1 1 0 01.707-.293H20" />
                            </svg>
                            <p class="text-sm">Vault is empty.</p>
                            <p class="text-xs text-slate-600 mt-1">Select text or click bookmark in chat to add.</p>
                        </div>
                        <div v-for="item in vaultItems" :key="item.id" class="p-3 bg-slate-800 border border-slate-700 rounded-lg flex justify-between 
                    items-start gap-3 hover:border-amber-500/50 transition-all">
                            <div class="flex-1">
                                <p class="text-sm text-slate-2 00 whitespace-pre-line">{{ item.content }}</p> <span
                                    class="text-[10px] text-slate-500 mt-2 block">Saved: {{ item.date }}</span>
                            </div>
                            <button @click="deleteFromVault(item.id)"
                                class="text-slate-5 00 hover:text-red-400 p-1 rounded transition-colors"
                                title="Delete from Vault">
                                <svg xmlns="http://www.w3.org/2000 /svg" class="h-4 w-4" fill="none" viewBox="0 0 24 24"
                                    stroke="currentColor">
                                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                                        d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 0 0-1-1h-4a1 1 0 00-1 1v3M4 7 h16" />
                                </svg>
                            </button>
                        </div>
                    </div>
                    <!-- FLOATING SELECTION POPUP -->
                    <div v-if="showSelectionMenu"
                        class="selection-popup fixed z-50 bg-amber-500 hover:bg-amber-400 text-slate-950 px-3 py-1.5 rounded-lg shadow-xl font-semibold text-xs flex items-center gap-1 cursor-pointer transition-transform duration-100 ease-out"
                        :style="{ top: `${selectionCoords.y}px`, left: `${selectionCoords.x}px ` }"
                        @click="saveSelectedText">
                        <svg xmlns="http://www.w3.org/2000/svg" class="h-3.5 w-3.5" viewBox="0 0 20 20"
                            fill="currentColor">
                            <path fill-rule="evenodd"
                                d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11 -2 0v-5H4a1 1 0 110-2h5V 4a1 1 0 011-1z"
                                clip-rule="evenodd" />
                        </svg>
                        <div>Add Selection to Vault </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<style scoped>
/* Custom helper for background colors not default to tailwind */
.bg-slate-750 {
    background-color: #1e293b90;
}

.bounding-panel {
    border: 2px solid gold;
    background: rgba(131, 84, 84, 0.05);
}
</style>
