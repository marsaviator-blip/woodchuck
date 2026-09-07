import { Elysia, t } from 'elysia';

export const storageRoutes = new Elysia({ prefix: '/storage' })
  .post('/upload', async ({ body }) => {
    // TODO: Use AWS S3 SDK pointed to your MinIO instance endpoint
    return { status: "success", message: "File pushed to MinIO bucket" };
  });
