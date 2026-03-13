
Docker install run opensearch and dashboard

ar@pop-os:~/desperado/woodchuck/opensearch_docker$ docker pull opensearchproject/opensearch:latest
latest: Pulling from opensearchproject/opensearch
3ffbc2e88330: Pull complete 
d81d13ffd72e: Pull complete 
bf4014304c4b: Pull complete 
69b15574866b: Pull complete 
4f4fb700ef54: Pull complete 
dd722a4f4ece: Pull complete 
014e856758af: Pull complete 
Digest: sha256:919ff4e7d0d57dbc4bd0999ddf0e43e961bba844ec2a5b6734fc979eb4e32399
Status: Downloaded newer image for opensearchproject/opensearch:latest
docker.io/opensearchproject/opensearch:latest
ar@pop-os:~/desperado/woodchuck/opensearch_docker$ docker pull opensearchproject/opensearch-dashboards:latest
latest: Pulling from opensearchproject/opensearch-dashboards
3ffbc2e88330: Already exists 
4f2ff7cdb2df: Pull complete 
cf595e6a13d1: Pull complete 
3a0951145d7f: Pull complete 
79735d702d22: Pull complete 
4f4fb700ef54: Pull complete 
Digest: sha256:4c556b690142d0de436ef91359ea235df1c6448464a3b4e925314817dbcb99a9
Status: Downloaded newer image for opensearchproject/opensearch-dashboards:latest
docker.io/opensearchproject/opensearch-dashboards:latest

-------------------------------------------------------------------------------------------------
Docker install run neo4j server

ar@pop-os:~/desperado/woodchuck/opensearch_docker$ docker pull neo4j:latest
latest: Pulling from library/neo4j
206356c42440: Pull complete 
32303d1841b2: Pull complete 
110041cf8111: Pull complete 
bb3791b25a58: Pull complete 
4f4fb700ef54: Pull complete 
Digest: sha256:7bbe414ef4c1e1184284b2f4dcb19fb8c7e03126b540a77907b2e4d07014b4af
Status: Downloaded newer image for neo4j:latest
docker.io/library/neo4j:latest
ar@pop-os:~/desperado/woodchuck/opensearch_docker$ docker run \
--publish=7474:7474 --publish=7687:7687 \
--volume=$HOME/neo4j/data:/data \
--env=NEO4J_AUTH=neo4j/your_password \
--name neo4j_container \
neo4j
Warning: Folder mounted to "/data" is not writable from inside container. Changing folder owner to neo4j.
Changed password for user 'neo4j'. IMPORTANT: this change will only take effect if performed before the database is started for the first time.
2026-03-13 13:11:57.431+0000 INFO  Logging config in use: File '/var/lib/neo4j/conf/user-logs.xml'
2026-03-13 13:11:57.445+0000 INFO  Starting...
2026-03-13 13:11:57.963+0000 INFO  This instance is ServerId{a4d9d4d0} (a4d9d4d0-1005-4388-ad56-b4ebc3b625e2)
2026-03-13 13:11:58.677+0000 INFO  ======== Neo4j 2026.02.2 ========
2026-03-13 13:12:00.162+0000 INFO  Anonymous Usage Data is being sent to Neo4j, see https://neo4j.com/docs/usage-data/
2026-03-13 13:12:01.138+0000 INFO  Bolt enabled on 0.0.0.0:7687.
2026-03-13 13:12:02.322+0000 INFO  HTTP enabled on 0.0.0.0:7474.
2026-03-13 13:12:02.323+0000 INFO  Remote interface available at http://localhost:7474/
2026-03-13 13:12:02.325+0000 INFO  id: C9EAF5253C702C7030DE60866B776A846770CA1D0B4B5849F081C0CEB7EEFCE7
2026-03-13 13:12:02.325+0000 INFO  name: system
2026-03-13 13:12:02.325+0000 INFO  creationDate: 2026-03-13T13:11:59.534Z
2026-03-13 13:12:02.325+0000 INFO  Started.


Stop the Container
To stop the container, use the following command in your terminal:
bash

docker stop neo4j_container

To start it again later:
bash

docker start neo4j_container

----------------------------------------------------------------------------------------
funky query - need to learn meaning

MATCH (n) OPTIONAL MATCH (n)-[r]->(m) RETURN n, r, m LIMIT 100


