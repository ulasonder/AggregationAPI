FROM openjdk:21-jdk-slim
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} AggregationApi.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/AggregationApi.jar"]