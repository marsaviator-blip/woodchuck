// routes/db/dragonfly.ts
import Redis from 'ioredis';

// Single shared client instance for the entire Bun app lifecycle
export const dragonfly = new Redis({
  host: 'localhost',
  port: 6379
});
