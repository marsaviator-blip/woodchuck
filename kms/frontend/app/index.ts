import { Elysia } from 'elysia';
import { cors } from '@elysiajs/cors';
import { chatRoutes } from './routes/chat';
import { embeddingRoutes } from './routes/embeddings';
import { mathRoutes } from './routes/math';
import { storageRoutes } from './routes/storage';
import { cacheRoutes } from './routes/cache';

const SERVER_PORT = 3007;

// const CORS_HEADERS = {
//   "Access-Control-Allow-Origin": "*", 
//   "Access-Control-Allow-Methods": "POST, GET, OPTIONS",
//   "Access-Control-Allow-Headers": "Content-Type, Authorization",
//   "Content-Type": "application/json"
// };

const app = new Elysia()
  .use(
    cors({
      origin: 'http://localhost:3006', 
      methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
      allowedHeaders: ['Content-Type', 'Authorization'],
    })
  )
  // Group all backend endpoints into a clean global nesting root
  .group('/api', (api) => 
    api
      .use(chatRoutes)        // Mounts to /api/chat/gemini
      .use(embeddingRoutes)   // Mounts to /api/embeddings/llama
      .use(mathRoutes)        // Mounts to /api/math/compute
      .use(storageRoutes)     // Mounts to /api/storage/upload
      .use(cacheRoutes)       // Mounts to /api/cache/:key
  )
  .listen(SERVER_PORT);

// ADD THIS TEMPORARY LOG BLOCK HERE:
console.log("----------------------------------------");
console.log("📍 ACTIVE BUN SERVER ROUTES:");
app.routes.forEach(route => {
  console.log(`   [${route.method}] http://localhost:3007${route.path}`);
});
console.log(`🚀 Backend cluster online at ${app.server?.hostname}:${app.server?.port}`);
