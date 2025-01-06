# Package stage
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app /app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/target/acft-0.0.1-SNAPSHOT.jar"]