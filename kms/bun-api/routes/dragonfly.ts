// routes/dragonfly.ts
import { Elysia, t } from 'elysia';
import { dragonfly } from './db/dragonfly'; // Import the shared client [1]

export const dragonflyRoutes = new Elysia({ prefix: '/stream' })
  
  // 1. ORIGINAL STREAM INGESTION PATH
  .post('/push', async ({ body, set }) => {
    try {
      // 1. Extract the content along with identifiers (sessionId and optional cardId)
      const { content, sessionId, cardId, cardType, user } = body as { content: string; sessionId?: string; cardId?: string; cardType?: string; user?: string };

      const finalizedTextString = Array.isArray(content)
        ? content.join('')
        : content;

      // 2. Build the storage key in the required format:
      //    kms:<session id>:<user>:<card type>:<card id>
      const sid = sessionId || `session-${Date.now()}`;
      const uname = user || 'unknown';
      const ctype = cardType || (body.type as string) || 'note';
      const cid = cardId || `card-${Date.now()}`;

      const targetKey = `kms:${sid}:${uname}:${ctype}:${cid}`;

      // 3. Save the clean text and metadata under the unique key name, but only if it doesn't already exist.
      //    Store a JSON object so the `type` and other metadata are preserved.
      const valueObj = {
        content: finalizedTextString,
        prompt: (body as any).prompt || '',
        type: ctype,
        sessionId: sid,
        cardId: cid,
        user: uname,
        savedAt: new Date().toISOString()
      };

      // Use Redis SET NX for atomic 'set if not exists' behavior so repeated toggles won't overwrite.
      const setResult = await dragonfly.set(targetKey, JSON.stringify(valueObj), 'NX');

      if (setResult === null) {
        // Key already existed; ignore the duplicate save request
        console.log(`Dragonfly key already exists, ignoring save for key: ${targetKey}`);
        return { success: true, key: targetKey, skipped: true };
      } else {
        console.log(`Response object successfully saved to Dragonfly under key: ${targetKey}`);
        return { success: true, key: targetKey, skipped: false };
      }
    } catch (error) {
      console.error('Dragonfly save failed:', error);
      set.status = 500;
      return { success: false };
    }
  }, {
    // FIXED: Ensured prompt, content, type, and optional cardId are explicitly defined
    body: t.Object({
      prompt: t.String(),
      content: t.String(),
      type: t.String(),
      cardId: t.Optional(t.String()),
      cardType: t.Optional(t.String()),
      user: t.Optional(t.String())
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
