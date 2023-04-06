FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/unknown-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} docker-springboot.jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/docker-springboot.jar"]