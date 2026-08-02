export type StreamNodeType = 'user_prompt' | 'ai_response' | 'user_note';

export interface StreamConceptNode {
  id: number;
  prompt: string;
  nodeType: StreamNodeType; // Explictly tags the exact content style
  timestamp: Date;
  userId: string;
}

export interface KnowledgeMorphismGroup {
  groupId: string;                  // Unique uuid structural index string
  savedAt: string;                  // ISO datetime serialization format
  focusArea: string;                // Context category theoretic focus
  category: string;                 // The mathematical object domain
  subject: string;                  // Morphism target definitions
  topic: string;                    // Content classifications
  curatedNodes: StreamConceptNode[]; // Polymorphic message set array
}
