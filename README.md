# Project contains various scaffolding projects to select best way forward
The goal is to build a system as presented in the woodchuck.png diagram.
backend
docker
gateway
ui
my-app (failed to push)

## backend
spring.io initializr created - no scaffolding

## docker
contains postgres and keycloak
currently keycoak is throwing an error in the browser when trying to reach the console
```
cd docker
docker compose up
```
Then if it says serving ```Listening on: http://0.0.0.0:8080``` check with
```
curl http://0.0.0.0:8080
```
 


## gateway
spring.io initializr created - no scaffolding

## ui
vue created - no scaffolding - next step is to add axios

## my-app
not loaded yet - failed push
bootify.io created - CRUD - with Swagger interface
mysql in docker dependent
also has a spring ui frontend
