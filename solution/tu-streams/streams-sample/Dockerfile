FROM gradle:5.2.1-jdk11-slim
WORKDIR /home/gradle/project
COPY build.gradle ./
COPY src ./src
USER root
RUN gradle build

FROM openjdk:11.0.2-jre-slim
ENV JMX_PORT=9999 \
    JMX_HOSTNAME=127.0.0.1
COPY --from=builder /home/gradle/project/build/distributions/project.tar /app/
WORKDIR /app
RUN tar -xvf project.tar
WORKDIR /app/project
CMD java \
        -Dcom.sun.management.jmxremote \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -Dcom.sun.management.jmxremote.ssl=false \
        -Djava.rmi.server.hostname=${JMX_HOSTNAME} \
        -Dcom.sun.management.jmxremote.rmi.port=${JMX_PORT} \
        -Dcom.sun.management.jmxremote.port=${JMX_PORT} \
        -classpath "lib/*" streams.SampleStreamsApp