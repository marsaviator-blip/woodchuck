// routes/chat.ts
import { Elysia, t, sse } from 'elysia';
import { GoogleGenAI } from '@google/genai';
import Redis from 'ioredis';

const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });

// Direct database client connection (Lightning fast, stays in-memory)
const dragonfly = new Redis({
  host: '127.0.0.1',
  port: 6379, 
});

export const chatRoutes = new Elysia({ prefix: '/chat' })
  .get('/gemini', async function* ({ query }) {
    const { prompt, sessionId } = query;

    // 1. Open the live stream channel from Google
    const responseStream = await ai.models.generateContentStream({
      model: 'gemini-2.5-flash',
      contents: [prompt],
    });

    let fullCompiledResponse = ""; // Internal server string buffer

    // 2. Loop over incoming tokens
    for await (const chunk of responseStream) {
      const textChunk = chunk.text;
      if (textChunk) {
        // Collect the full response text natively on the server side
        fullCompiledResponse += textChunk; 
        
        // Push the chunk live down the network line to Vue instantly
        yield sse({ data: textChunk });
      }
    }

    // 3. SECURE SIDE EFFECT: Run once the generator loop completes cleanly
    if (fullCompiledResponse.trim().length > 0) {
  const sessionCacheKey = `kms:session:${sessionId}:record`;
  
  // 2. Package the exchange as a readable data node string
  const historyPayload = JSON.stringify({
    prompt: prompt,
    response: fullCompiledResponse,
    timestamp: new Date().toLocaleTimeString('en-US', { hour12: false })
  });

  // 3. Right-push onto the Dragonfly array list
  await dragonfly.rpush(sessionCacheKey, historyPayload);
  console.log(`💾 Sequentially appended exchange node to Dragonfly: ${sessionCacheKey}`);
     }
  }, {
    query: t.Object({ 
      prompt: t.String(),
      sessionId: t.String() 
    })
  });
