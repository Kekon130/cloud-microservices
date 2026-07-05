### BUILD STAGE ###
FROM maven:3.9.12-eclipse-temurin-21-noble as BUILD
WORKDIR /app
COPY ./pom.xml .
RUN mvn dependency:go-offline
COPY ./src ./src
RUN mvn clean package -DskipTests

### RUNTIME STAGE ###
FROM eclipse-temurin:21-jre-noble as RUNTIME
WORKDIR /app
COPY --from=BUILD /app/target/*.jar app.jar
EXPOSE 8000
CMD ["java","-jar","app.jar","--spring.profiles.active=docker"]