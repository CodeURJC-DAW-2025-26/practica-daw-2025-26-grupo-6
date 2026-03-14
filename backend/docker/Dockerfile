# Compiling image
FROM maven:3.9-eclipse-temurin-21 AS builder

# Working directory for the build process
WORKDIR /project

# Copy dependencies configuration file
COPY pom.xml /project/

# Download project dependencies
RUN mvn dependency:go-offline

# Copy project source code
COPY /src /project/src

# Compile project and package it into a JAR file, no test
RUN mvn -B package -DskipTests

# JRE image
FROM eclipse-temurin:21-jre

# JAR directory
WORKDIR /usr/src/app/

# Copy JAR
COPY --from=builder /project/target/*.jar /usr/src/app/app.jar

# Listening port
EXPOSE 8443

# Command to run the application
CMD [ "java", "-jar", "app.jar" ]