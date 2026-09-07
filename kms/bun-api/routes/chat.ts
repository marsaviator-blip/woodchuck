// // src/routes/chat.ts
// import { Elysia, t } from 'elysia';
// import { GoogleGenAI } from '@google/genai';

// const ai = new GoogleGenAI({ 
//   apiKey: process.env.GEMINI_API_KEY,
//   httpOptions: { retry: 0 }
// });

// const MAX_CONCURRENT_GEMINI_CALLS = 2;
// let activeGeminiCalls = 0;

// const waitForGeminiSlot = async () => {
//   while (activeGeminiCalls >= MAX_CONCURRENT_GEMINI_CALLS) {
//     await new Promise(resolve => setTimeout(resolve, 200));
//   }
//   activeGeminiCalls += 1;
// };

// const releaseGeminiSlot = () => {
//   activeGeminiCalls = Math.max(0, activeGeminiCalls - 1);
// };

// export const chatRoutes = new Elysia()
//   .post('/chat/gemini', async function* (context) {
//     const { body } = context;
//     const { prompt, sessionId, cardId } = body;

//     await waitForGeminiSlot();
    
//     try {
//       // UNIFIED PHASE: Explicit system instructions force the model to 
//       // yield text chunks first and generate follow-up questions at the very tail end.
//       const responseStream = await ai.models.generateContentStream({
//         model: 'gemini-3.5-flash-lite',
//         contents: [prompt],
//         config: {
//           systemInstruction: "You are a professional system engineer. Respond to the user's query normally. At the very end of your response, you MUST provide exactly 2 short, practical follow-up questions wrapped in a clean JSON format on a new line like this: ||PROMPTS:[\"Question 1\", \"Question 2\"]||"
//         }
//       });

//       let fullTextCollected = "";

//       for await (const chunk of responseStream) {
//         const textChunk = chunk.text;
//         if (textChunk) {
//           fullTextCollected += textChunk;

//           // Check if the stream has hit the prompts delimiter separator block yet
//           if (!textChunk.includes('||PROMPTS:')) {
//             yield `data: ${JSON.stringify({
//               type: 'chunk',
//               sessionId,
//               cardId,
//               text: textChunk
//             })}\n\n`;
//           }
//         }
//       }

//       // --- CENTRAL EXTRACTION SEGMENT ---
//       // Safely split out text output blocks from structural json parameters
//       const promptDelimiter = '||PROMPTS:';
//       if (fullTextCollected.includes(promptDelimiter)) {
//         const parts = fullTextCollected.split(promptDelimiter);
//         const jsonPart = parts[1]?.replace('||', '').trim();
        
//         try {
//           const promptsArray = JSON.parse(jsonPart || "[]");
          
//           // Yield the finalized prompts block down the open channel safely prior to exiting loop
//           yield `data: ${JSON.stringify({
//             type: 'prompts',
//             sessionId,
//             cardId,
//             prompts: promptsArray
//           })}\n\n`;
//         } catch (jsonErr) {
//           console.warn("Failed parsing tail-end options schema parameter mapping:", jsonErr);
//           yield `data: ${JSON.stringify({ type: 'prompts', sessionId, cardId, prompts: [] })}\n\n`;
//         }
//       } else {
//         // Fallback placeholder array execution signals to clear front-end loading bars
//         yield `data: ${JSON.stringify({ type: 'prompts', sessionId, cardId, prompts: [] })}\n\n`;
//       }

//     } catch (err: any) {
//       console.error('Failed to generate unified streaming response context:', err);
//       yield `data: ${JSON.stringify({
//         type: 'error',
//         message: err.message || 'Could not build guidance profile structure maps.'
//       })}\n\n`;
//     } finally {
//       releaseGeminiSlot();
//     }
//   }, {
//     body: t.Object({
//       prompt: t.String(),
//       sessionId: t.String(),
//       cardId: t.String()
//     })
//   });


// src/routes/chat.ts
import { Elysia, t } from 'elysia';
import { GoogleGenAI } from '@google/genai';

const ai = new GoogleGenAI({ 
  apiKey: process.env.GEMINI_API_KEY,
  httpOptions: { retry: 0 }
});

const MAX_CONCURRENT_GEMINI_CALLS = 2;
let activeGeminiCalls = 0;

const waitForGeminiSlot = async () => {
  while (activeGeminiCalls >= MAX_CONCURRENT_GEMINI_CALLS) {
    await new Promise(resolve => setTimeout(resolve, 200));
  }
  activeGeminiCalls += 1;
};

const releaseGeminiSlot = () => {
  activeGeminiCalls = Math.max(0, activeGeminiCalls - 1);
};

export const chatRoutes = new Elysia()
  .post('/chat/gemini', async function* (context) {
    const { body } = context;
    const { prompt, sessionId, cardId } = body;

    await waitForGeminiSlot();
    
    try {
      // UNIFIED PHASE: Explicit system instructions force the model to 
      // yield text chunks first and generate the category/topic/prompts metadata block at the very tail end.
      const responseStream = await ai.models.generateContentStream({
        model: 'gemini-3.5-flash-lite',
        contents: [prompt],
        config: {
          systemInstruction: "You are a professional system engineer. Respond to the user's query normally. At the very end of your response, you MUST categorize the interaction. Provide exactly 2 short, practical follow-up questions, along with a high-level category and specific topic, wrapped in a clean JSON format on a new line like this: ||METADATA:{\"category\": \"Category Name\", \"topic\": \"Topic Name\", \"prompts\": [\"Question 1\", \"Question 2\"]}||"
        }
      });

      let fullTextCollected = "";

      for await (const chunk of responseStream) {
        const textChunk = chunk.text;
        if (textChunk) {
          fullTextCollected += textChunk;

          // Check if the stream has hit the metadata delimiter separator block yet
          if (!textChunk.includes('||METADATA:')) {
            yield `data: ${JSON.stringify({
              type: 'chunk',
              sessionId,
              cardId,
              text: textChunk
            })}\n\n`;
          }
        }
      }

      // --- CENTRAL EXTRACTION SEGMENT ---
      // Safely split out text output blocks from structural json parameters
      const metadataDelimiter = '||METADATA:';
      if (fullTextCollected.includes(metadataDelimiter)) {
        const parts = fullTextCollected.split(metadataDelimiter);
        const jsonPart = parts[1]?.replace('||', '').trim();
        
        try {
          const parsedMetadata = JSON.parse(jsonPart || "{}");
          
          // Yield the finalized metadata block (category, topic, prompts) down the channel straight to the UI
          yield `data: ${JSON.stringify({
            type: 'metadata',
            sessionId,
            cardId,
            category: parsedMetadata.category || "General",
            topic: parsedMetadata.topic || "Discussion",
            prompts: parsedMetadata.prompts || []
          })}\n\n`;
        } catch (jsonErr) {
          console.warn("Failed parsing tail-end metadata options schema parameter mapping:", jsonErr);
          yield `data: ${JSON.stringify({ type: 'metadata', sessionId, cardId, category: "General", topic: "Discussion", prompts: [] })}\n\n`;
        }
      } else {
        // Fallback placeholder block execution signals to clear front-end loading bars
        yield `data: ${JSON.stringify({ type: 'metadata', sessionId, cardId, category: "General", topic: "Discussion", prompts: [] })}\n\n`;
      }

    } catch (err: any) {
      console.error('Failed to generate unified streaming response context:', err);
      yield `data: ${JSON.stringify({
        type: 'error',
        message: err.message || 'Could not build guidance profile structure maps.'
      })}\n\n`;
    } finally {
      releaseGeminiSlot();
    }
  }, {
    body: t.Object({
      prompt: t.String(),
      sessionId: t.String(),
      cardId: t.String()
    })
  });

