#!/bin/bash
# Capture the first argument as BASE_DIR
BASE_DIR="$1"

# Check if a directory was actually provided
if [ -z "$BASE_DIR" ]; then
    echo "Usage: $0 /path/to/directory"
    exit 1
fi

echo "The base directory is: $BASE_DIR"
cd "$BASE_DIR" || exit

echo 'starting kafka and zookeeper'
cd apache_kafka
docker-compose -f docker-compose.yml up -d
echo 'starting docling'
cd ../docling
docker-compose -f docker-compose.yml up -d
echo 'do not have a docker-compose file for temporal yet'
cd ../temporal
#docker-compose -f docker-compose.yml up -d
echo 'do not have a docker-compose file for postgres and pgvector'
cd ../postgres_pgvector
#docker-compose -f docker-compose.yml up -d
echo 'starting neo4j'
cd ../neo4j
docker-compose -f docker-compose.yml up -d



