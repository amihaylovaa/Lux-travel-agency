FROM openjdk:8
EXPOSE 8080
ADD target/lux-travel-1.0-SNAPSHOT.jar lux-travel-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/lux-travel-1.0-SNAPSHOT.jar"]
