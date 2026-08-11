// src/services/api.ts

const API_BASE_URL = 'http://localhost:3007/api';

// Enforce strict layout shapes for network safety
export interface PromptResponse {
  reply: string;
}

/**
 * Sends a text prompt payload to the Bun backend runtime.
 * @param promptText - The string raw input from the workspace template.
 * @returns A promise resolving to the strict PromptResponse type.
 */
export async function sendPromptToBun(promptText: string): Promise<PromptResponse> {
  const response = await fetch(`${API_BASE_URL}/prompt`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ prompt: promptText }),
  });

  if (!response.ok) {
    throw new Error(`API error: Server returned status code ${response.status}`);
  }

  // Cast the untyped response payload to your strict contract
  return response.json() as Promise<PromptResponse>;
}
