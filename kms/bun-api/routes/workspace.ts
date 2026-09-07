// routes/workspace.ts
import { Elysia } from 'elysia';
import { executeCohortAnalysis } from '../services/cppBridge'; 

export const workspaceRoutes = new Elysia()
  .post('/workspace/refresh', async ({ body }) => {
    // 1. Hand off the work to the C++ bridge layer
    const resultData = await executeCohortAnalysis(body);
    
    // 2. Return the data to Vue
    return resultData;
  });
