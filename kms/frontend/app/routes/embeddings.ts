import { Elysia, t } from 'elysia';

export const embeddingRoutes = new Elysia({ prefix: '/embeddings' })
  .post('/llama', async ({ body }) => {
    const { text } = body;
    
    // TODO: Invoke Llama vector model here
    const mockVector = Array.from({ length: 5 }, () => Math.random());
    
    return {
      text,
      embedding: mockVector
    };
  }, {
    body: t.Object({
      text: t.String()
    })
  });
