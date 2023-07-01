#FROM maven:3.6.3-jdk-11 AS build
#COPY . .
#RUN mvn clean package -DskipTests
#
#FROM openjdk:11-jdk-slim-sid
#COPY --from=build /target/adelispringboot-0.0.1-SNAPSHOT.jar demo.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "demo.jar"]


FROM openjdk:11
ADD ./adelispringboot-0.0.1-SNAPSHOT.jar demo.jar
ENTRYPOINT ["java", "-jar", "demo.jar"]