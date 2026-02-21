added RouteLocator in DemoApplication.java

curl http://127.0.0.1:9080/get - nothing happened

changed port to 9080 in application.properties

don't know if needed, but opened firewall: sudo ufw allow 9080/tcp

disabled eureka (discovery) - set to false in application.properties

added config/SecurityConfig.java

added actuator to pom.xml

added actuator to SecurityConfig.java

run app with: ar@pop-os:~/desperado/woodchuck/gateway$ java -jar target/demo-0.0.1-SNAPSHOT.jar --debug

in Firefox, able to view: http://localhost:9080/actuator/health 

curl http://127.0.0.1:9080/actuator/health

responds with: {"groups":["liveness","readiness"],"status":"UP"}

curl http://127.0.0.1:9080/get  - no response yet - should have many lines of response

http://localhost:9080/get    hits a login page - thymeleaf is running temporarily

want to put this back:
			<!-- <dependency>
				<groupId>net.ttddyy.observation</groupId>
				<artifactId>datasource-micrometer-bom</artifactId>
				<version>${datasource-micrometer.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency> -->

