export NS=io.confluent.monitoring.clients.interceptor
kafka-console-consumer \
    --group group-with-custom-settings \
    --bootstrap-server kafka-1:9092 \
    --topic tuning-topic \
    --consumer-property "interceptor.classes=${NS}.MonitoringConsumerInterceptor" \
    --consumer-property "client.id=tuning-custom-client" \
    --consumer-property "fetch.max.wait.ms=200" \
    --consumer-property "fetch.min.bytes=104857600"     # 100 MB
