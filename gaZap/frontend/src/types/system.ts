// src/types/system.ts
export interface SystemNode {
  id: string;
  label: string;
  type: 'client' | 'gateway' | 'service' | 'database';
  description: string;
}

export interface Connection {
  from: string;
  to: string;
  label: string;
}

export interface UseCase {
  id: string;
  title: string;
  description: string;
  activeNodes: string[];       // Nodes highlighted in this view
  activeConnections: string[]; // Connections highlighted in this view
}
