FROM openjdk:22-jdk-slim
WORKDIR /app
COPY target/todo-0.0.1-SNAPSHOT.jar app.jar
COPY cacerts cacerts
ENTRYPOINT ["java","-jar","/app/app.jar"]