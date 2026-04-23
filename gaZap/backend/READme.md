

In your .bashrc set the following:
export MP_API_KEY="<your api key for MP"

variable is used in MPservice.java to pass into the RestClient


mvn clean package
java -jar target/<jarfile


### Running temporal enabled rcsb service ###

In one terminal run
```
temporal server start-dev --ui-port 8080
```
Should see Temporal UI at ```localhost:8080```

Then in another go to folder gaZap/backend and
```
mvn -q -Dspring-boot.run.arguments="--app.runner.enabled=true --spring.main.keep-alive=false" spring-boot:run
```

Should be the same results as before.  The app stays up with
```
ManualTemporalWorkerBootstrap: workerFactory started for BioTaskQueue
```
