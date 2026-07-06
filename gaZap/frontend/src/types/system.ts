// src/types/system.ts
export interface SystemNode {
  id: string;
  label: string;
  type: 'client' | 'gateway' | 'controller' | 'service' | 'activity' | 'database';
  x: number;
  y: number;
}

export interface Connection {
  id: string;
  from: string;
  to: string;
}

export interface UseCase {
  id: string;
  title: string;
  description: string;
  activeNodes: string[];
  activeConnections: string[];
}

export interface LogEntry {
  id: string;
  timestamp: string;
  message: string;
  type: 'info' | 'success' | 'warn';
}