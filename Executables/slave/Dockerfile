FROM openjdk:14-jdk-alpine3.10

COPY slave.jar /myapp/slave.jar
WORKDIR /myapp

EXPOSE 8080

CMD java -jar slave.jar