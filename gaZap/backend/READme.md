

In your .bashrc set the following:
export MP_API_KEY="\<your api key for MP\>"

variable is used in MPservice.java to pass into the RestClient


mvn clean package
java -jar target/<jarfile



### Running temporal enabled rcsb service ###

In one terminal run
```
temporal server start-dev
```
Should see Temporal UI at ```localhost:8233```

Then in another go to folder gaZap/backend and
```
mvn -q  spring-boot:run
```
or
```
java  -jar ./target/woodchuck_mvc-0.0.1-SNAPSHOT.jar
```

Should be the same results as before.  The app stays up with
```
workers-auto-discovery: workerFactory started for app
```
