# Dockerfile-dev
FROM maven:3.9.5-eclipse-temurin-17

WORKDIR /app
COPY . /app

CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"]
