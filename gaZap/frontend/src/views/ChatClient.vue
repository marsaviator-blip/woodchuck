<template>
  <div class="chat-container">
    <h2>Spring AI Chat Room</h2>
    
    <div class="messages" ref="chatWindow">
      <div 
        v-for="(msg, index) in messages" 
        :key="index" 
        :class="['message', msg.role]"
      >
        <strong>{{ msg.role === 'user' ? 'You' : 'AI' }}:</strong>
        <p>{{ msg.text }}</p>
      </div>
    </div>

    <div class="input-area">
      <input 
        v-model="userInput" 
        @keydown.enter="sendMessage" 
        placeholder="Ask something..." 
        :disabled="isStreaming"
      />
      <button @click="sendMessage" :disabled="isStreaming || !userInput.trim()">
        {{ isStreaming ? 'Thinking...' : 'Send' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue';

const userInput = ref('');
const messages = ref([]);
const isStreaming = ref(false);
const chatWindow = ref(null);

const scrollToBottom = async () => {
  await nextTick();
  if (chatWindow.value) {
    chatWindow.value.scrollTop = chatWindow.value.scrollHeight;
  }
};

const sendMessage = async () => {
  if (!userInput.value.trim() || isStreaming.value) return;

  const prompt = userInput.value;
  messages.value.push({ role: 'user', text: prompt });
  userInput.value = '';
  scrollToBottom();

  // Initialize placeholder for the incoming AI stream
  const aiMessageIndex = messages.value.push({ role: 'assistant', text: '' }) - 1;
  isStreaming.value = true;

  try {
    const response = await fetch(`http://localhost:8080/api/chat/stream?message=${encodeURIComponent(prompt)}`);
    
    if (!response.body) throw new Error('ReadableStream not supported by response.');
    
    const reader = response.body.getReader();
    const decoder = new TextDecoder();

    while (true) {
      const { value, done } = await reader.read();
      if (done) break;

      // Decode the raw chunk
      const chunk = decoder.decode(value, { stream: true });
      
      // Parse SSE data format ("data: content\n\n")
      const lines = chunk.split('\n');
      for (const line of lines) {
        if (line.startsWith('data:')) {
          const textChunk = line.replace('data:', '').trim();
          messages.value[aiMessageIndex].text += textChunk + ' ';
          scrollToBottom();
        }
      }
    }
  } catch (error) {
    console.error('Streaming error:', error);
    messages.value[aiMessageIndex].text = 'Error communicating with AI service.';
  } finally {
    isStreaming.value = false;
  }
};
</script>

<style scoped>
.chat-container {
  max-width: 600px;
  margin: 2rem auto;
  border: 1px solid #ccc;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  height: 500px;
  font-family: sans-serif;
}
.messages {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
  background-color: #f9f9f9;
}
.message {
  margin-bottom: 1rem;
  padding: 0.5rem 0.8rem;
  border-radius: 6px;
}
.user {
  background-color: #e3f2fd;
  align-self: flex-end;
}
.assistant {
  background-color: #f1f1f1;
  align-self: flex-start;
}
.input-area {
  display: flex;
  border-top: 1px solid #ccc;
  padding: 0.5rem;
}
input {
  flex: 1;
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}
button {
  margin-left: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:disabled {
  background-color: #cccccc;
}
</style>
