FROM openjdk:21-jdk-slim
WORKDIR /app
COPY gf-auth-server/target/server-0.0.1-SNAPSHOT.jar /app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/server-0.0.1-SNAPSHOT.jar"]