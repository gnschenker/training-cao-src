package streams;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import java.util.Properties;

public class SampleStreamsApp {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("*** Starting Kafka Streams Sample Application ***");

        Properties config = new ConfigProvider().getConfig();
        Topology topology = new TopologyProvider().getTopology();
        KafkaStreams streams = new KafkaStreams(topology, config);
        streams.start();

        System.out.println("*** Kafka Streams Sample Application started ***");


        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("### Stopping Kafka Streams Sample Application ###");
            streams.close();
        }));
    }
}
