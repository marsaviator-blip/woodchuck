# Project contains various scaffolding projects to select best way forward
The goal is to build a system as presented in the woodchuck.png diagram.
* backend
* keycloak_in_docker
    keycloak port 8080
    postgres port 5432
* gateway_reactive
    port 9080
* gateway_servlet
    port 9082
* ui
* crud_scaffold

Too many project running on 8080 - gotta fix that.

## backend
spring.io initializr created - no scaffolding

## docker
contains postgres and keycloak

currently keycoak is throwing an error in the Firefox when trying to reach the console.

Chrome was ok

cd docker

docker compose up

Then if it says serving ```Listening on: http://0.0.0.0:8080``` check with

curl http://0.0.0.0:8080


## gateway
spring.io initializr created - no scaffolding

added circuitbreaker and discovery - not used yet

## ui
vue created - no scaffolding - next step is to add keyclaok.js to talk to keycloak and axios to talk to gateway.

## crud_scaffold
created using bootify.io - spring CRUD - also has Swagger interface

mysql in docker dependent
also has a spring ui frontend
