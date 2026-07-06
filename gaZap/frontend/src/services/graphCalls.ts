// Define the structure matching your Java GraphStats record
export interface GraphStats {
  totalNodes: number;
  totalEdges: number;
  nodeTypes: string[]; 
}

export interface VisNode {
  id: string;
  label: string;
  properties: Record<string, unknown>;
}

export interface VisLink {
  source: string;
  target: string;
  type: string;
}

export interface NetworkGraphPayload {
  nodes: VisNode[];
  links: VisLink[];
}

/**
 * Fetches node and edge counts from the Neo4j backend.
 * Uses the '/api' prefix configuration handled by your Vite proxy.
 */
// export async function getGraphCounts(): Promise<GraphStats> {
//   try {
//     const response = await fetch('/api/graph/counts', {
//       method: 'GET',
//       headers: {
//         'Content-Type': 'application/json',
//       },
//     });

//     if (!response.ok) {
//       throw new Error(`HTTP error! status: ${response.status}`);
//     }

//     const data: GraphStats = await response.json();
//     return data;
//   } catch (error) {
//     console.error('Error in graphCall.ts -> getGraphCounts:', error);
//     // Return fallback zeroes to avoid breaking UI components on network failure
//     return { totalNodes: 0, totalEdges: 0 };
//   }

//   const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

//export default {
  /**
   * Retrieves high-performance Neo4j database counts and map-grouped schemas
   * @returns {Promise<Object>} API Payload matched for GraphStats
   */
//    async getDatabaseCounts() {
export async function getDatabaseCounts(): Promise<GraphStats> { 
    try {
      const response = await fetch('/api/graph/counts', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        }
      });

      // Fetch does not throw on 4xx/5xx errors, handle manually
      if (!response.ok) {
        throw new Error(`HTTP network error! Status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error('Failed fetching graph database stats via Fetch:', error);
      throw error;
    }
};

export async function clearGraph(): Promise<void> {
  try {
    const response = await fetch('/api/graph/clear', {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`Failed to clear graph! Status: ${response.status}`);
    } else {
      await getDatabaseCounts().then((stats) => {
        stats.value = {
          totalNodes: 0,
          totalEdges: 0,
          nodeTypes: [],
        }; 
        console.log('Graph cleared successfully. Current stats:', stats);

      }).catch((err) => {
        console.error('Error fetching updated graph stats after clear:', err);
      });
    }
  } catch (error) {
    console.error('Failed to clear graph:', error);
    throw error;
  }
}

export async function fetchGraphTopology(limit = 150): Promise<NetworkGraphPayload> {
  try {
    const response = await fetch(`/api/graph/topology?limit=${limit}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error(`Knowledge Graph Fetch Error! Status: ${response.status}`);
    }

    return await response.json();
  } catch (error) {
    console.error('Failed processing request timeline:', error);
    throw error;
  }
}

  export interface AuthorInfo {
    id: string;
    name: string;
  }

  export interface DocumentInfo {
    id: string;
    title: string;
  }

  export interface AuthorWithDocs {
    authorId: string;
    authorName: string;
    relatedDocuments: DocumentInfo[];
  }

  export interface AuthorPayload {
    authorCount: number;
    authorList: AuthorInfo[];
    authorsWithDocs: AuthorWithDocs[];
  }

export async function getAuthorAnalytics(): Promise<AuthorPayload> {
  try {
    const response = await fetch('/api/v1/graph/authors-analytics', { method: 'GET' });
    if (!response.ok) throw new Error('Failed fetching author graph structures');
    return await response.json();
  } catch (error) {
    console.error(error);
    return { authorCount: 0, authorList: [], authorsWithDocs: [] };
  }
}
