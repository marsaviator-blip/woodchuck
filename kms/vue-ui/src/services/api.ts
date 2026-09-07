export interface ChatStreamOptions {
  promptText: string;
  sessionId: string;
  cardId: string;                          
  onChunk: (text: string) => void;         
  onPromptsPartial: (text: string) => void; 
  onPrompts: (prompts: string[]) => void;  
  onComplete: () => void;                  
  onError: (error: any) => void; 
}

const ApiService: any = {
  async startChatStream(options: ChatStreamOptions) {
    try {
      const targetUrl = 'http://localhost:3007/api/chat/gemini';

      const response = await fetch(targetUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          prompt: options.promptText,
          sessionId: options.sessionId,
          cardId: options.cardId
        })
      });

      if (!response.body) throw new Error('Streaming connection payload missing');
      
      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      while (true) {
        const { value, done } = await reader.read();
        if (done) break;

        buffer += decoder.decode(value, { stream: true });

        // SSE standard separates full message blocks using double standard newlines
        const parts = buffer.split(/\r?\n\r?\n/);
        buffer = parts.pop() || '';

        for (const part of parts) {
          const trimmedPart = part.trim();
          if (!trimmedPart) continue;

          // Process and group the event data chunks safely
          const dataLines = trimmedPart.split(/\r?\n/).filter(l => l.startsWith('data:'));
          if (dataLines.length === 0) continue;

          // FIX: Do NOT call .trim() on the overall concatenated data stream payload.
          // This ensures spaces and code-indent line blocks are preserved exactly.
          const dataPayload = dataLines.map(l => l.replace(/^data:\s?/, '')).join('\n');
          if (!dataPayload) continue;

          let payload;
          try {
            payload = JSON.parse(dataPayload);
          } catch (err) {
            options.onError(err);
            continue;
          }

          if (payload.type === 'chunk' && payload.text) {
            options.onChunk(payload.text);
          } else if (payload.type === 'prompts-partial') {
            options.onPromptsPartial(payload.text);
          } else if (payload.type === 'prompts') {
            options.onPrompts(payload.prompts);
          } else if (payload.type === 'error') {
            options.onError(payload.message);
          }
        }
      }

      // Final dynamic sweep of any residual stream tokens left in the buffer pipe
      if (buffer.trim().startsWith('data:')) {
        const remainingPayload = buffer.replace(/^data:\s?/, '').trim();
        try {
          const finalPayload = JSON.parse(remainingPayload);
          if (finalPayload.type === 'chunk' && finalPayload.text) {
            options.onChunk(finalPayload.text);
          }
        } catch (e) {
          // Suppress trailing parse noise on clean connection closures
        }
      }

    } catch (err) {
      options.onError(err);
    } finally {
      // FIX: Defensive validation safeguards against mismatched callback properties
      if (typeof options.onComplete === 'function') {
        options.onComplete();
      } else if (typeof (options as any).onEnd === 'function') {
        (options as any).onEnd();
      }
    }
  }
};

ApiService.saveToDragonfly = async function (payload: { content: string; sessionId?: string; prompt?: string; type?: string; cardId?: string; cardType?: string; user?: string }) {
  try {
    const targetUrl = 'http://localhost:3007/api/stream/push';
    const res = await fetch(targetUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        prompt: payload.prompt || '',
        content: payload.content,
        type: payload.type || 'note',
        sessionId: payload.sessionId || undefined,
        cardId: payload.cardId || undefined,
        cardType: payload.cardType || undefined,
        user: payload.user || undefined
      })
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(`Dragonfly save failed: ${res.status} ${text}`);
    }

    return await res.json();
  } catch (err) {
    throw err;
  }
}

export default ApiService;

ApiService.saveSession = async function (payload: { sessionId: string; user: string; cards: any[]; metadata?: any }) {
  try {
    console.log("front end calling backend save session");
    const targetUrl = 'http://localhost:3007/api/session/save';
    const res = await fetch(targetUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });

    if (!res.ok) {
      const text = await res.text();
      throw new Error(`Save session failed: ${res.status} ${text}`);
    }

    return await res.json();
  } catch (err) {
    throw err;
  }
}

