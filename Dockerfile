# Use a base image with JDK 21 for building the application
FROM openjdk:21-jdk
COPY target/order-0.0.1-SNAPSHOT.jar order-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","order-0.0.1-SNAPSHOT.jar"]
