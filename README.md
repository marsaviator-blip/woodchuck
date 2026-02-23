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

port 9090

added hateaos and actuator to pom

does hateoas add value to retrieving photos and thumbnails?ans: NO
spring data rest uses hateoas

## keycloak_in_docker
contains postgres and keycloak

currently keycoak is throwing an error in the Firefox when trying to reach the console.

Chrome was ok

cd docker

docker compose up

Then if it says serving ```Listening on: http://0.0.0.0:8080``` check with

curl http://0.0.0.0:8080

In the keycloak console, as admin, client_id and secret were created.

## gateway has become 2 projects - gateway_reactive, port 9080 - gateway_servlet, post 9082
need to get both projects connected to keycloak listening at 8080 as shown above

spring.io initializr created - no scaffolding

added resiliency, circuitbreaker and discovery - not used yet

## ui
vue created - no scaffolding - next step is to add keycloak.js to talk to keycloak and axios to talk to gateway.

## crud_scaffold
created using bootify.io - spring CRUD - also has Swagger interface

dependent on - mysql in docker

also has a spring ui frontend
