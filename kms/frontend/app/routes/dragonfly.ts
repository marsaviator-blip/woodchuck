// routes/dragonfly.ts
import { Elysia, t } from 'elysia';
import { dragonfly } from './db/dragonfly'; // Import the shared client [1]

export const dragonflyRoutes = new Elysia({ prefix: '/api/stream' })
  
  // 1. ORIGINAL STREAM INGESTION PATH
.post('/push', async ({ body, set }) => {
    try {
      // 1. Extract the content along with an identifier (like sessionId or a unique id)
      const { content, sessionId } = body as { content: string; sessionId?: string };

      const finalizedTextString = Array.isArray(content) 
        ? content.join('') 
        : content;

      // 2. Generate a safe fallback key if no explicit session id was provided
      const uniqueId = sessionId || `fallback-${Date.now()}`;
      const targetKey = `workspace:ai:responses:${uniqueId}`;

      // 3. Save the clean text directly under the unique key name
      await dragonfly.set(targetKey, finalizedTextString);
      
      console.log(`Response text successfully saved to Dragonfly under key: ${targetKey}`);
      return { success: true, key: targetKey };
    } catch (error) {
      console.error("Dragonfly save failed:", error);
      set.status = 500;
      return { success: false };
    }
  }, {
    // FIXED: Ensured prompt, content, and type are all explicitly defined
    body: t.Object({ 
      prompt: t.String(), 
      content: t.String(), 
      type: t.String() 
    })
  })


  // 2. NEW: CACHE AN EXTRACTED CATEGORY (From Embeddings)
  .post('/category/cache', async ({ body, error }) => {
    try {
      const { categoryId, name, properties } = body;
      const key = `theory:category:${categoryId}`;

      // Cache as an in-memory hash structure natively
      await dragonfly.hset(key, {
        id: categoryId,
        name: name,
        properties: JSON.stringify(properties),
        cachedAt: Date.now().toString()
      });

      return { success: true, message: `Category ${name} cached safely.` };
    } catch (err: any) {
      return error(500, { error: err.message });
    }
  }, {
    body: t.Object({
      categoryId: t.String(),
      name: t.String(),
      properties: t.Any()
    })
  })

  // 3. NEW: DEVELOP & CACHE MORPHISMS (Arrows between Categories)
  .post('/morphism/cache', async ({ body, error }) => {
    try {
      const { morphismId, sourceCategoryId, targetCategoryId, transformRules } = body;
      const key = `theory:morphism:${morphismId}`;

      // Store the structural mapping configuration data matrix
      await dragonfly.hset(key, {
        id: morphismId,
        source: `theory:category:${sourceCategoryId}`, // Point to Source Domain
        target: `theory:category:${targetCategoryId}`, // Point to Target Codomain
        transform: JSON.stringify(transformRules),
        compositionSafe: 'true'
      });

      return { success: true, message: `Morphism map initialized successfully.` };
    } catch (err: any) {
      return error(500, { error: err.message });
    }
  }, {
    body: t.Object({
      morphismId: t.String(),
      sourceCategoryId: t.String(),
      targetCategoryId: t.String(),
      transformRules: t.Any()
    })
  });
