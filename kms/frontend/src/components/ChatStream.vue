<!-- components/ChatStream.vue -->
<script setup>
const emit = defineEmits(['stream-chunk', 'stream-complete', 'stream-error'])

const startChatStream = (promptText) => {
  const url = `http://localhost:3007/api/chat/gemini?prompt=${encodeURIComponent(promptText)}`;
  const eventSource = new EventSource(url);

  eventSource.onmessage = (event) => {
    // Bun stream safety: Check for explicit termination strings
    if (event.data === '[DONE]' || event.data === 'done') {
      eventSource.close();
      emit('stream-complete');
      return;
    }
    
    // Pass the raw chunk safely up to KmsWorkspace
    emit('stream-chunk', event.data); 
  };

  eventSource.onerror = (error) => {
    // Force immediate connection closure so it stops pulsing/retrying
    eventSource.close();
    
    // Always trigger complete on error so your UI can gracefully 
    // commit whatever chunks it managed to pull down before dropping out.
    emit('stream-complete');
  };
};

defineExpose({
  startChatStream
});
</script>

