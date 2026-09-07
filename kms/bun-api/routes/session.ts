// src/routes/session.ts
import { Elysia, t } from 'elysia';
import { S3Client, PutObjectCommand } from '@aws-sdk/client-s3';
import { Client as PgClient } from 'pg';

const S3_ENDPOINT = process.env.MINIO_ENDPOINT || 'http://localhost:9000';
const S3_REGION = process.env.MINIO_REGION || 'us-east-1';
const S3_BUCKET = process.env.MINIO_BUCKET || 'kms-session';
const S3_ACCESS_KEY = process.env.MINIO_ACCESS_KEY || process.env.MINIO_KEY || 'postgres';
const S3_SECRET = process.env.MINIO_SECRET || 'postgres';

const PG_CONN = process.env.PG_CONN || process.env.DATABASE_URL || 'postgres://postgres:postgres@localhost:5432/postgres';

const s3client = new S3Client({
  endpoint: S3_ENDPOINT,
  region: S3_REGION,
  credentials: {
    accessKeyId: S3_ACCESS_KEY,
    secretAccessKey: S3_SECRET
  },
  forcePathStyle: true
});

const pg = new PgClient({ connectionString: PG_CONN });
pg.connect()
  .then(() => console.log('📡 Connected to pgvector metadata engine.'))
  .catch(err => console.warn('⚠️ PostgreSQL connection failed:', err.message || err));

export const sessionRoutes = new Elysia({ prefix: '/session' })
  .post('/save', async ({ body, set }) => {
    console.log("➡️ [API INTERCEPT] Processing incoming save session request payload.");

    try {
      const { sessionId, user, cards } = body;

      if (!cards || cards.length === 0) {
        set.status = 400;
        return { success: false, error: 'Cannot save an empty card collection.' };
      }

      const totalCardsCount = cards.length;
      const markedNotesCount = cards.filter((c: any) => c.type === 'note').length;
      const markedPromptsCount = cards.filter((c: any) => c.type === 'prompt').length;
      const markedAiResponsesCount = cards.filter((c: any) => c.type === 'ai-response').length;

      const sessionTitle = `Session - ${new Date().toLocaleDateString()}`;
      const sessionSummary = `User archived workspace session containing ${totalCardsCount} cards.`;

      const bundle = {
        sessionId,
        user,
        cards,
        createdAt: new Date().toISOString()
      };

      const bundleString = JSON.stringify(bundle, null, 2);
      const fileSizeBytes = Buffer.byteLength(bundleString);

      const fallbackType = cards[0]?.type || 'note';
      const baseKey = `kms:${sessionId}:${user}:${fallbackType}`;
      const key = `${baseKey}.session.json`;

      // --- 1. STREAM TO MINIO ---
      const putCommand = new PutObjectCommand({
        Bucket: S3_BUCKET,
        Key: key,
        Body: bundleString,
        ContentType: 'application/json'
      });

      await s3client.send(putCommand);
      console.log(`📥 Snapshot file archived successfully to MinIO: ${key}`);

      // --- 2. PERSIST METADATA TO POSTGRESQL (workspace_sessions Table) ---
      if (pg) {
        try {
          let targetUserId: string;
          const userCheck = await pg.query('SELECT id FROM users LIMIT 1');
          
          if (userCheck.rows.length === 0) {
            // Create a seed user profile if none exist for testing
            const userInsert = await pg.query("INSERT INTO users (email) VALUES ('developer@workspace.local') RETURNING id;");
            targetUserId = userInsert.rows[0].id; // ✅ Fixed array syntax
          } else {
            targetUserId = userCheck.rows[0].id;  // ✅ Fixed array syntax
          }

          const insertQuery = `
            INSERT INTO workspace_sessions (
              user_id, title, summary, summary_embedding, 
              minio_bucket, minio_key, file_size_bytes,
              total_cards_count, marked_notes_count, marked_prompts_count, marked_ai_responses_count,
              created_at, updated_at
            ) VALUES ($1, $2, $3, null, $4, $5, $6, $7, $8, $9, $10, NOW(), NOW())
            RETURNING id;
          `;
          
          const params = [
            targetUserId, 
            sessionTitle, 
            sessionSummary, 
            S3_BUCKET, 
            key, 
            fileSizeBytes,
            totalCardsCount, 
            markedNotesCount, 
            markedPromptsCount, 
            markedAiResponsesCount
          ];

          const pgResult = await pg.query(insertQuery, params);
          console.log(`✅ Success! Metadata logged inside workspace_sessions with ID: ${pgResult.rows[0].id}`);
        } catch (pgErr: any) {
          console.error('❌ PostgreSQL Insertion Failure:', pgErr.message || pgErr);
        }
      }

      return { success: true, sessionId, minioKey: key };

    } catch (err: any) {
      console.error('❌ Endpoint pipeline runtime abort:', err.message || err);
      set.status = 500;
      return { success: false, error: err.message || String(err) };
    }
  }, {
    body: t.Object({
      sessionId: t.String(),
      user: t.String(),
      cards: t.Array(t.Any())
    })
  });
