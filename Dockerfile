FROM navikt/java:11

COPY build/libs/*.jar app.jar

RUN mkdir -p /test/resources

COPY src/test/resources/* /test/resources/
