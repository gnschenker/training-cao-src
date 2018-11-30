FROM microsoft/dotnet:2.1-sdk AS builder
RUN apt-get update && apt-get install -y apt-transport-https \
    && wget -qO - https://packages.confluent.io/deb/5.0/archive.key | apt-key add - \
    && echo "deb [arch=amd64] https://packages.confluent.io/deb/5.0 stable main" >> /etc/apt/sources.list \
    && apt-get update && apt-get install -y confluent-librdkafka-plugins

WORKDIR /app
COPY app.csproj ./
RUN dotnet restore
COPY . .
RUN dotnet publish -c Release -o output

FROM microsoft/dotnet:2.1-runtime AS runtime
COPY --from=builder /app/output .
# Copy over monitoring interceptors
COPY --from=builder /usr/lib/x86_64-linux-gnu/monitoring-interceptor.so.1 /usr/lib/x86_64-linux-gnu/monitoring-interceptor.so
ENTRYPOINT ["dotnet", "app.dll"]