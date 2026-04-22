FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY . /app

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

ENTRYPOINT ["java","-jar","target/eventsbooking-0.0.1-SNAPSHOT.jar"]