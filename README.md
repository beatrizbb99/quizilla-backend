# Backend for Quizilla Website

# Spring Boot Application Google App Engine Standard with Java 21

## Local Setup
$ mvn clean package
$ mvn spring-boot:run

Open localhost:9090/

## Deploying to Google Cloud

```bash
gcloud app deploy
```

To view the app, use command:
```
gcloud app browse
```
Or navigate to `https://<project-id>.appspot.com`.
