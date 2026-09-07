export interface UserRecord {
  id: string; // UUID
  email: string;
  createdAt: Date;
}

export interface WorkspaceSessionMetadata {
  id: string; // UUID matches database primary key
  userId: string; // UUID reference linking user ownership
  title: string;
  summary?: string;
  
  // Storage paths matching your MinIO destination bucket
  minioBucket: string; 
  minioKey: string; // Format suggestion: "users/{userId}/sessions/{sessionId}.json"
  fileSizeBytes: number;
  
  // Breakdown counts cached directly for quick frontend previews
  totalCardsCount: number;
  markedNotesCount: number;
  markedPromptsCount: number;
  markedAiResponsesCount: number;
  
  createdAt: Date;
  updatedAt: Date;
}

// Payload contract received by Elysia from your Vue frontend's ApiService.saveSession call
export interface SaveSessionPayload {
  userId: string;
  title?: string;
  summary?: string;
  cards: Array<{
    id: string | number;
    type: 'note' | 'prompt' | 'ai-response' | 'system-prompt';
    title: string;
    content: string;
    date: string;
    isMarkedForSession: boolean;
  }>;
}
