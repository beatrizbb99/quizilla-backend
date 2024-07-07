# Backend for Quizilla Website

# Spring Boot Application Google App Engine Standard with Java 21

## Local Setup
$ mvn clean package
$ mvn spring-boot:run

Open localhost:8080/

## Deploying to Google Cloud

```bash
gcloud app deploy
```

To view your app, use command:
```
gcloud app browse
```
Or navigate to `https://<your-project-id>.appspot.com`.
