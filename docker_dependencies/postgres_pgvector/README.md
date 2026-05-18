


start not using docker-compose:

docker run -p 5001:5001 -e DOCLING_SERVE_ENABLE_UI=true quay.io/docling-project/docling-serve



SOME useful postgres commands:

docker ps

docker exec -it <container_name> psql -U <username> -d <database_name> -c "SELECT COUNT(*) FROM <table_name>;"

docker exec -it pgvector-db psql -U postgres -d postgres -c "SELECT COUNT(*) FROM vector_store;"
  
docker exec -it pgvector-db psql -U postgres -c "select current_database();"

docker exec -it pgvector-db psql -U postgres -d postgres -c "SELECT table_name from information_schema.tables where table_schema = 'public' and table_type = 'BASE TABLE';"
