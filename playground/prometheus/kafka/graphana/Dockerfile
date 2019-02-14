FROM ubuntu:18.04
RUN apt-get update && apt-get install -y curl
RUN curl -L -O https://grafanarel.s3.amazonaws.com/builds/grafana-2.5.0.linux-x64.tar.gz
RUN tar zxf grafana-2.5.0.linux-x64.tar.gz
WORKDIR /grafana-2.5.0
EXPOSE 3000
CMD ./bin/grafana-server web

# login is admin/admin