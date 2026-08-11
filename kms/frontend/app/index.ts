// backend/index.ts
import { GoogleGenAI } from "@google/genai"; // 1. Import official Google SDK

const SERVER_PORT = 3007;

// 2. Initialize the Gemini Client
// It automatically reads the GEMINI_API_KEY environment variable
const ai = new GoogleGenAI({ apiKey: process.env.GEMINI_API_KEY });

// Global Cross-Origin Resource Sharing (CORS) Headers for Vue Port (3006)
const CORS_HEADERS = {
  "Access-Control-Allow-Origin": "*", 
  "Access-Control-Allow-Methods": "POST, GET, OPTIONS",
  "Access-Control-Allow-Headers": "Content-Type, Authorization",
  "Content-Type": "application/json"
};

Bun.serve({
  port: SERVER_PORT,
  async fetch(req) {
    const url = new URL(req.url);

    // Handle Browser CORS Preflight Options Check
    if (req.method === "OPTIONS") {
      return new Response("OK", { status: 204, headers: CORS_HEADERS });
    }

    // Main API Chat Route Pipeline
    if (url.pathname === "/api/prompt" && req.method === "POST") {
      try {
        const body = await req.json();
        const userPrompt = body.prompt;

        // Guard against empty string submittals
        if (!userPrompt || userPrompt.trim() === "") {
          return new Response(
            JSON.stringify({ error: "Prompt value cannot be empty." }), 
            { status: 400, headers: CORS_HEADERS }
          );
        }

        console.log(`\n📥 Inbound User Query: "${userPrompt}"`);
        console.log(`🧠 Querying Gemini model engine...`);

        // 3. Trigger the real Live Gemini Chat Completion
        const response = await ai.models.generateContent({
          model: "gemini-3.5-flash", // Blazing fast model perfect for live dashboards
          contents: userPrompt,
          // Optional system instruction to anchor persona behavior:
          config: {
            systemInstruction: "You are a concise, helpful workspace assistant inside a high-density log dashboard application."
          }
        });

        // Extract the raw text string safely from the response payload
        const aiMessage = response.text || "No response generated.";

        console.log(`📤 Outbound Gemini Response: "${aiMessage.slice(0, 50)}..."`);

        // Send the real structural JSON package straight back to your Vue app
        return new Response(
          JSON.stringify({ reply: aiMessage }), 
          { status: 200, headers: CORS_HEADERS }
        );

      } catch (err: any) {
        console.error("❌ Gemini Pipeline Error:", err.message);
        
        const errorMessage = err.message.includes("API key") 
          ? "Backend Configuration Error: Missing your local environment GEMINI_API_KEY."
          : "Internal Server Processing Error executing Gemini completions.";

        return new Response(
          JSON.stringify({ error: errorMessage }), 
          { status: 500, headers: CORS_HEADERS }
        );
      }
    }

    // Route Fallback
    return new Response(
      JSON.stringify({ error: "API Endpoint Route Not Found." }), 
      { status: 404, headers: CORS_HEADERS }
    );
  },
});

console.clear();
console.log(`======================================================`);
console.log(`🚀 Live Gemini Bun Engine Active & Listening On Port 3007`);
console.log(`🔗 Interface Target Gateway: http://localhost:${SERVER_PORT}`);
console.log(`======================================================`);
