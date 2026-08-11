// Shared type used by both Vue and Bun
export interface ActivityCard {
  id: number;
  type: 'note' | 'prompt' | 'response';
  content: string;
  timestamp: string;
}
