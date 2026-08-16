// components/ChatStream.ts

interface ChatStreamOptions {
  promptText: string;
  sessionId: string;
  emit: (eventName: string, payload?: any) => void;
}

const startChatStream = ({ promptText, sessionId, emit }: ChatStreamOptions) => {
  const url = `http://localhost:3007/api/chat/gemini?prompt=${encodeURIComponent(promptText)}&sessionId=${encodeURIComponent(sessionId)}`;
  const eventSource = new EventSource(url);

  eventSource.onmessage = (event) => {
    if (event.data === '[DONE]' || event.data === 'done') {
      eventSource.close();
      emit('stream-complete');
      return;
    }
    
    // Pass the raw chunk safely back to our workspace handler
    emit('stream-chunk', event.data); 
  };

  eventSource.onerror = (error) => {
    eventSource.close();
    emit('stream-error', error);
    emit('stream-complete');
  };
};

export default {
  startChatStream
};
