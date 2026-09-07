// src/db/migrate.ts
import { Client } from 'pg';

const DATABASE_URL = process.env.DATABASE_URL || 'postgres://postgres:postgres@localhost:5432/postgres';

const sqlScript = `
-- 1. ENABLE EXTENSIONS
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "vector";

-- 2. USERS TABLE
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. WORKSPACE SESSIONS TABLE
CREATE TABLE IF NOT EXISTS workspace_sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL DEFAULT 'Untitled Research Session',
    summary TEXT,
    summary_embedding vector(1536), 
    minio_bucket VARCHAR(63) NOT NULL, 
    minio_key VARCHAR(1024) NOT NULL UNIQUE, 
    file_size_bytes BIGINT DEFAULT 0,
    total_cards_count INT NOT NULL DEFAULT 0,
    marked_notes_count INT NOT NULL DEFAULT 0,
    marked_prompts_count INT NOT NULL DEFAULT 0,
    marked_ai_responses_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. PERFORMANCE TUNING & VECTOR INDEXES
CREATE INDEX IF NOT EXISTS idx_workspace_sessions_user_id ON workspace_sessions(user_id);

CREATE INDEX IF NOT EXISTS idx_workspace_sessions_embedding 
ON workspace_sessions USING hnsw (summary_embedding vector_cosine_ops);
`;

async function runMigration() {
  const client = new Client({
    connectionString: DATABASE_URL
  });
  
  try {
    await client.connect();
    console.log('📡 Connecting to pgvector engine layer...');
    
    await client.query(sqlScript);
    console.log('✅ pgvector Schema migration executed successfully!');
    
    const checkVector = await client.query("SELECT extname FROM pg_extension WHERE extname = 'vector';");
    if (checkVector.rows.length > 0) {
      console.log('🚀 Verified: pgvector extension is live and active.');
    }
  } catch (err) {
    console.error('❌ Migration failed:', err);
  } finally {
    await client.end();
  }
}

runMigration();
