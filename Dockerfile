# Use the recommended Eclipse Temurin image for JDK 21 on a slim Ubuntu base
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# The build artifact must exist in your local 'target' directory before running 'docker build'
COPY target/AI-Finance-Manager-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
