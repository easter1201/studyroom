FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/studyroom-0.0.1-SNAPSHOT.jar studyroom.jar

CMD ["java", "-jar", "studyroom.jar"]