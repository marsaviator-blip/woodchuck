
https://www.baeldung.com/spring-boot-keycloak

3.4

    oauth2Login authorization is based on sessions and oauth2ResourceServer is based on Bearer tokens
    Because it is session-based, oauth2Login requires protection against CSRF. But because of their stateless nature, resource servers don’t need it
    Authorities mapping and the type of Authentication in the security context differ in oauth2Login, oauth2ResourceServer with JWT decoder, and oauth2ResourceServer with introspection
