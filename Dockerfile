FROM openjdk:11-jdk
ARG JAR_FILE=JAR_FILE_MUST_BE_SPECIFIED_AS_BUILD_ARG
COPY ${JAR_FILE} DataExtractor.jar
COPY /target/lib lib
ENTRYPOINT ["java", "-Djava.security.edg=file:/dev/./urandom","-jar","DataExtractor.jar"]