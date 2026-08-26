#Knowledge Management System  
  
Several docker containers must be running:  
Minio  
DragonflyDB    
pgvector (postgres with extension)  
  
##KMS  
cd kms/  
  
###Start bun server:  
bun --watch frontend/app/index.ts   
(on port 3007)  
  
cd frontend/
###Start vite server:  
bun run dev  
(on port 3006)  
  
##Dragonfly  
At command line, to check on dragonfly:  
if localhost and default port of 6379  
redis-cli  
  
KEYS *  
  
TYPE <key>  
  
example:   
127.0.0.1:6379> TYPE kms:session:session_researcher_alpha:response-1786997187053:record  
string  
  
127.0.0.1:6379> GET kms:session:session_researcher_alpha:response-1786997187053:record  
  
currently returns the ai chat response  
  
vite server sends chat prompt to bun server, which sends to gemini.  
chat response returns from gemini to bun server where it is duplicated, returned to vite server and placed into dragonfly.  
the response is a text string in dragonfly - not a list (but lists can easily be made for other purposes)  
gemini said llama wants a string.  



gemini suggested project structure (need to work onnt this ):
my-kms-app/
├── bin/                           # Compiled C++ binaries (Git ignored)
│   └── category_processor         # The actual binary executable run by Bun
├── cpp-src/                       # C++ Source Code (The Mathematical Engine)
│   ├── include/
│   │   ├── CategoryGraph.hpp      # Sparse matrix graph structures
│   │   ├── IsomorphismSolver.hpp  # Abstract base class for solvers
│   │   └── json.hpp               # Sub-dependency (e.g., nlohmann/json for parsing)
│   ├── src/
│   │   ├── LaptopSignatureSolver.cpp
│   │   ├── ServerMatrixSolver.cpp
│   │   └── main.cpp               # Standard input scanner and flag router
│   └── Makefile                   # Compilation script for laptop native builds
├── src/                           # Bun Backend Source Code (TypeScript)
│   ├── config/
│   │   └── database.ts            # Initializes connections to PG, Neo4j, MinIO, Dragonfly
│   ├── core/
│   │   ├── cpp-bridge.ts          # The Bun.spawn orchestrator we just designed
│   │   └── telemetry-worker.ts    # The Dragonfly interaction tracking rules engine
│   ├── db/
│   │   ├── postgres-adapter.ts    # Handles SQL schema updates and fingerprint matches
│   │   ├── neo4j-adapter.ts       # Executes the transactional APOC Cypher morphic links
│   │   └── minio-adapter.ts       # Archives raw session markdown blocks to cold storage
│   ├── http/
│   │   ├── server.ts              # Main Bun.serve entrypoint (HTTP Router & WebSockets)
│   │   └── routes/
│   │       ├── chat.ts            # Routes Gemini streaming LLM traffic
│   │       └── session.ts         # Handles the Phase 2 'Commit' validation route
│   └── index.ts                   # Master bootstrap file for the entire server
├── .env                           # Environment configurations (COMPUTE_TIER=lightweight)
├── package.json                   # Bun configuration and npm dependencies
└── tsconfig.json                  # TypeScript compiler settings
