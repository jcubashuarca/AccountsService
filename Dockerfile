FROM maven:3.8.5-openjdk-8-slim
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src src
RUN mvn package
RUN ls
EXPOSE 8081
ENTRYPOINT ["java","-jar","target/accounts-service-0.0.4-SNAPSHOT.jar"]
