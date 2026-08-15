import { Elysia, t } from 'elysia';

export const cacheRoutes = new Elysia({ prefix: '/cache' })
  .get('/:key', async ({ params }) => {
    const { key } = params;
    // TODO: await dragonflyClient.get(key)
    return { key, value: "cached_data_from_dragonfly" };
  });
