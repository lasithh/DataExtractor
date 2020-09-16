FROM openjdk:8-jdk-alpine
ARG JAR_FILE=JAR_FILE_MUST_BE_SPECIFIED_AS_BUILD_ARG
COPY ${JAR_FILE} DataExtractor.jar
ENTRYPOINT ["java", "-Djava.security.edg=file:/dev/./urandom","-jar","/DataExtractor.jar"]