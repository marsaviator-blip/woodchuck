// src/db/check-db.ts
import { Client } from 'pg';

const client = new Client({
  connectionString: 'postgres://postgres:postgres@localhost:5432/postgres'
});

async function runCheck() {
try {

      await client.connect();
    console.log('📡 Connected to pgvector metadata layer...\n');

    const result = await client.query(`
      SELECT 
        id, 
        title, 
        total_cards_count as cards, 
        minio_key, 
        created_at 
      FROM workspace_sessions 
      ORDER BY created_at DESC 
      LIMIT 5;
    `);

    if (result.rows.length === 0) {
      console.log('❓ Connected successfully, but the workspace_sessions table is EMPTY.');
    } else {
      console.log(`✅ Success! Found ${result.rows.length} metadata records in PostgreSQL:`);
      console.table(result.rows); // Prints an automatic table structure
    }

  } catch (err: any) {
    console.error('❌ Database connection or query error:', err.message || err);
  } finally {
    await client.end();
  }
}

runCheck();
