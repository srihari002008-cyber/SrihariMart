# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the pom.xml and checkstyle.xml and resolve dependencies (for caching)
COPY pom.xml checkstyle.xml ./
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the application using Tomcat 9 
# (Tomcat 9 is required because the app uses javax.servlet instead of jakarta.servlet)
FROM tomcat:9.0-jre17

# Remove default Tomcat applications
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy the generated WAR file into Tomcat's webapps directory as ROOT.war
# This makes the application accessible at the root path '/'
COPY --from=build /app/target/sriharimart.war /usr/local/tomcat/webapps/ROOT.war

# Expose Tomcat's default port
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]
