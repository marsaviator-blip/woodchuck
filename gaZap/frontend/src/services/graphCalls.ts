// Define the structure matching your Java GraphStats record
export interface GraphStats {
  totalNodes: number;
  totalEdges: number;
  nodeTypes: string[]; 
}

/**
 * Fetches node and edge counts from the Neo4j backend.
 * Uses the '/api' prefix configuration handled by your Vite proxy.
 */
export async function getGraphCounts(): Promise<GraphStats> {
  try {
    const response = await fetch('/api/graph/counts', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data: GraphStats = await response.json();
    return data;
  } catch (error) {
    console.error('Error in graphCall.ts -> getGraphCounts:', error);
    // Return fallback zeroes to avoid breaking UI components on network failure
    return { totalNodes: 0, totalEdges: 0 };
  }
}
