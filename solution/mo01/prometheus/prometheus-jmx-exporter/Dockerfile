# FROM openjdk:11-jdk-slim
FROM java:8

COPY jmx_prometheus_httpserver.jar /opt/jmx_prometheus_httpserver/jmx_prometheus_httpserver.jar
COPY confd-0.11.0-linux-amd64 /usr/local/bin/confd
RUN chmod +x /usr/local/bin/confd
COPY confd /etc/confd

COPY entrypoint.sh /opt/entrypoint.sh
ENTRYPOINT ["/opt/entrypoint.sh"]