FROM gradle:4.10.2-jdk11-slim AS builder
WORKDIR /home/gradle/project
ADD ./*.gradle ./
COPY ./src ./src
USER root
RUN gradle build 

FROM openjdk:11-jre-slim
COPY --from=builder /home/gradle/project/build/libs/app.jar /app/
EXPOSE 4445
CMD java \
        -Dcom.sun.management.jmxremote \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -Dcom.sun.management.jmxremote.ssl=false \
        -Djava.rmi.server.hostname=127.0.0.1 \
        -Dcom.sun.management.jmxremote.rmi.port=4445 \
        -Dcom.sun.management.jmxremote.port=4445 \
        -jar /app/app.jar