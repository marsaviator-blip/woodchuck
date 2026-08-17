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
  
vite server send chat prompt to bun server sends to gemini.  
chat response reurns from gemini to bun server where it is duplicated, returned to vite server and placed into dragonfly.  
the response is a text string in dragonfly - not a list (but lists can easily be made for oher purposes)  
gemini said llama wants a string.  
  
  
