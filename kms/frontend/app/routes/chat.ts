  // routes/chat.ts
import { Elysia, t, sse } from 'elysia';
import { GoogleGenAI } from '@google/genai';

// Initialize Gemini with your API key
const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });

export const chatRoutes = new Elysia({ prefix: '/chat' })
  .get('/gemini', async function* ({ query }) {
    const { prompt } = query;

    const responseStream = await ai.models.generateContentStream({
      model: 'gemini-2.5-flash',
      contents: [prompt],
    });

    for await (const chunk of responseStream) {
      const textChunk = chunk.text;
      if (textChunk) {
        // ENHANCED: Explicitly dictate event: 'message' to standardise flushing
        yield sse({ data: textChunk, event: 'message' });
      }
    }
  }, {
    query: t.Object({ prompt: t.String() })
  });

