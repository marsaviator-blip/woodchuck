// src/routes/chat.ts
import { Elysia, t, sse } from 'elysia';
import { GoogleGenAI } from '@google/genai';

const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });

export const chatRoutes = new Elysia()
  .get('/chat/gemini', async function* ({ query }) {
    const prompt = query.prompt;
    const sessionId = query.sessionId;
    const cardId = query.cardId;

    // --- PHASE 1: STREAM INITIAL RESPONSE IMMEDIATELY ---
    const responseStream = await ai.models.generateContentStream({
      model: 'gemini-2.5-flash',
      contents: [prompt],
    });

    let fullCompiledResponse = ""; 

    for await (const chunk of responseStream) {
      const textChunk = chunk.text;
      if (textChunk) {
        fullCompiledResponse += textChunk; 
        
        // Push initial tokens straight into the Vue app instantly
        yield sse({ 
          data: JSON.stringify({ 
            type: 'chunk', 
            sessionId: sessionId, 
            cardId: cardId, 
            text: textChunk 
          }) 
        });
      }
    }

    // --- PHASE 2: CONCURRENTLY INITIATE FOLLOW-ON ANALYSIS ---
    // The initial piece has finished rendering on the client screen.
    // Immediately build the thinking prompt and hand it off to the second LLM stream.
    const thinkingPrompt = `
      Review the following research text. Generate exactly 2 deeply directed, 
      critical follow-up questions that will push a researcher to find hidden concepts.
      Return the response as a clean, simple JSON array of strings.
      
      TEXT TO REVIEW:
      ${fullCompiledResponse}
    `;

    try {
      // FIX: Changed from generateContent to generateContentStream to prevent blocking
      const thinkingStream = await ai.models.generateContentStream({
        model: "gemini-2.5-flash",
        contents: [thinkingPrompt],
        config: { responseMimeType: "application/json" } 
      });

      let fullPromptsJsonBuffer = "";

      for await (const thinkChunk of thinkingStream) {
        const thinkText = thinkChunk.text;
        if (thinkText) {
          fullPromptsJsonBuffer += thinkText;

          // Yield partial prompts to the UI as they are generated
          yield sse({
            data: JSON.stringify({
              type: 'prompts-partial', // Signal to frontend that summary/prompts are forming
              sessionId: sessionId,
              cardId: cardId,
              text: thinkText
            })
          });
        }
      }

      // Final safety pass: emit a clean finalized event payload to flush state locks
      yield sse({ 
        data: JSON.stringify({
          type: 'prompts',
          sessionId: sessionId,
          cardId: cardId,
          prompts: JSON.parse(fullPromptsJsonBuffer.trim() || "[]")
        }) 
      });

    } catch (err) {
      console.error("Failed to generate early directed-thinking prompts:", err);
      yield sse({ 
        data: JSON.stringify({ 
          type: 'error', 
          message: "Could not build thinking guides." 
        }) 
      });
    }
  }, {
    query: t.Object({ 
      prompt: t.String(),
      sessionId: t.String(),
      cardId: t.String()
    })
  });
