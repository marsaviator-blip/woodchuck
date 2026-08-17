// src/utils/api.ts

export interface ChatStreamOptions {
  promptText: string;
  sessionId: string;
  onChunk: (chunk: string) => void;     // Called every time a piece of text arrives
  onComplete: () => void;                // Called when the AI finishes sending everything
  onError?: (error: any) => void;        // Optional error handling
}

/**
 * Connects the Vue display layers to the Bun backend streaming endpoint.
 */
export const startChatStream = ({ promptText, sessionId, onChunk, onComplete, onError }: ChatStreamOptions): EventSource => {
  const url = `http://localhost:3007/api/chat/gemini?prompt=${encodeURIComponent(promptText)}&sessionId=${encodeURIComponent(sessionId)}`;
  const eventSource = new EventSource(url);

  eventSource.onmessage = (event) => {
    // If the backend signals it is done, close the connection and notify the view
    if (event.data === '[DONE]' || event.data === 'done') {
      eventSource.close();
      onComplete();
      return;
    }
    
    // Hand the raw text chunk directly to the Vue component's callback
    onChunk(event.data); 
  };

  eventSource.onerror = (error) => {
    eventSource.close();
    if (onError) {
      onError(error);
    }
    onComplete(); // Ensure the UI leaves the "loading/streaming" state even if it errors
  };

  return eventSource;
};
export default {
  startChatStream
};