package streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;
import java.util.Properties;
import java.lang.System;

public class ConfigProvider {
    public Properties getConfig() {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "StreamsSampleApp");
        String bootstrapServers = System.getenv("BOOTSTRAP_SERVERS");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        String replicas = System.getenv("NUM_STANDBY_REPLICAS");
        config.put(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, replicas);
        return config;
    }
}
