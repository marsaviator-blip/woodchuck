// // src/utils/api.ts

// export interface ChatStreamOptions {
//   promptText: string;
//   sessionId: string;
//   cardId: string;                          
//   onChunk: (text: string) => void;         
//   onPromptsPartial: (text: string) => void;  
//   onPrompts: (prompts: string[]) => void;  
//   onComplete: () => void;                  
//   onError: (error: any) => void; // Made required to guarantee stability across your app
// }


// /**
//  * Connects the Vue display layers to the Bun/Elysia backend streaming endpoint.
//  * Optimized to pass through your Vite server proxy.
//  */

// // src/routes/chat.ts
// import { Elysia, t, sse } from 'elysia';
// import { GoogleGenAI } from '@google/genai';

// const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });

// export const chatRoutes = new Elysia()
//   // FIX: Changed to .post to read large payload content structures safely
//   .post('/chat/gemini', async function* ({ body }) {
//     // FIX: Read parameters cleanly out of the validated body payload
//     const prompt = body.prompt;
//     const sessionId = body.sessionId;
//     const cardId = body.cardId;

//     const responseStream = await ai.models.generateContentStream({
//       model: 'gemini-2.5-flash',
//       contents: [prompt],
//     });

//     let fullCompiledResponse = ""; 

//     for await (const chunk of responseStream) {
//       const textChunk = chunk.text;
//       if (textChunk) {
//         fullCompiledResponse += textChunk; 
//         yield sse({ 
//           data: JSON.stringify({ 
//             type: 'chunk', 
//             sessionId: sessionId, 
//             cardId: cardId, 
//             text: textChunk 
//           }) 
//         });
//       }
//     }

//     const thinkingPrompt = `
//       Review the following research text. Generate exactly 2 deeply directed, 
//       critical follow-up questions that will push a researcher to find hidden concepts.
//       Return the response as a clean, simple JSON array of strings.
      
//       TEXT TO REVIEW:
//       ${fullCompiledResponse}
//     `;

//     try {
//       const thinkingStream = await ai.models.generateContentStream({
//         model: "gemini-2.5-flash",
//         contents: [thinkingPrompt],
//         config: { responseMimeType: "application/json" } 
//       });

//       let fullPromptsJsonBuffer = "";

//       for await (const thinkChunk of thinkingStream) {
//         const thinkText = thinkChunk.text;
//         if (thinkText) {
//           fullPromptsJsonBuffer += thinkText;
//           yield sse({
//             data: JSON.stringify({
//               type: 'prompts-partial', 
//               sessionId: sessionId,
//               cardId: cardId,
//               text: thinkText
//             })
//           });
//         }
//       }

//       yield sse({ 
//         data: JSON.stringify({
//           type: 'prompts',
//           sessionId: sessionId,
//           cardId: cardId,
//           prompts: JSON.parse(fullPromptsJsonBuffer.trim() || "[]")
//         }) 
//       });

//     } catch (err) {
//       console.error("Failed to generate thinking prompts:", err);
//       yield sse({ data: JSON.stringify({ type: 'error', message: "Could not build thinking guides." }) });
//     }
//   }, {
//     // FIX: Enforce validation against incoming request body fields
//     body: t.Object({ 
//       prompt: t.String(),
//       sessionId: t.String(),
//       cardId: t.String()
//     })
//   });
// apps/frontend/src/services/api.ts

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

const ApiService = {
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

      while (true) {
        const { value, done } = await reader.read();
        if (done) break;

        const rawText = decoder.decode(value);
        const lines = rawText.split('\n').filter(line => line.startsWith('data: '));

        for (const line of lines) {
          const jsonStr = line.replace('data: ', '').trim();
          if (!jsonStr) continue;

          const payload = JSON.parse(jsonStr);

          if (payload.type === 'chunk') {
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
    } catch (err) {
      options.onError(err);
    } finally {
      options.onComplete();
    }
  }
};

// 📍 FIX: CRITICAL CRUCIAL EXPORT ALIGNMENT FOR VUE-ROUTER
// This must be present to fulfill the "import Api from ..." statement inside your views
export default ApiService;
