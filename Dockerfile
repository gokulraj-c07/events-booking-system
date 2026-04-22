# Use a lightweight OpenJDK image
FROM eclipse-temurin:17-jdk-alpine

# Create a directory for the application
WORKDIR /app

# Copy the built JAR file from the target directory to the container
# The JAR file name is 'eventsbooking-0.0.1-SNAPSHOT.jar' based on pom.xml
COPY ..
 RUN ./mvnw clean package -DskipTests
# Run the application
ENTRYPOINT ["java","-jar","app.jar"]