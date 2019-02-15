export NS=io.confluent.monitoring.clients.interceptor
kafka-console-consumer \
    --group group-with-default-settings \
    --bootstrap-server kafka-1:9092 \
    --topic tuning-topic \
    --consumer-property "interceptor.classes=${NS}.MonitoringConsumerInterceptor" \
    --consumer-property "client.id=tuning-default-client"
