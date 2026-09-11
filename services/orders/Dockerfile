FROM eclipse-temurin:21-jre-ubi10-minimal

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]