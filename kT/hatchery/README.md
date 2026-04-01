# kT - knowledge Tracker


## hatchery
Spring Initializr started project for AI

Pop out of the shell and into a brand new world.  Start learning - but how?

That is what this project is about - calling LLMs to provide assistive learning.

Could this provide context to graph models in the gaZap project?  That is the goal of the project.

Will hatchery divide into sibling projects?

There may be an MCP server.  A web interface to allow control of agents to gather info for the MCP server.  The server sits in a docker container.  How will gaZap and LIM communicate with the MCP server or other (vector) servers?

## first example just talks to a weather service
Learn the principles of MCP server and tools.

For now: mvn clean install -DskipTests

java -jar target/collect-0.0.1-SNAPSHOT.jar

server with tomcat is running, 

in another terminal start an mcp inspector: npx @modelcontextprotocol/inspector

in browser: http://localhost:8080/mcp

i am working at this point to get connected and ccall the weather service

this all comes from: https://spring.io/blog/2025/09/16/spring-ai-mcp-intro-blog 

look in the pom - turned off most LLM calls - because Spring starts trying to connect due to spring annotations in the mcp server.

depending on a particular goal - will get some of them running soon.

