package streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.Deserializer;
import io.confluent.kafka.serializers.KafkaJsonSerializer;
import io.confluent.kafka.serializers.KafkaJsonDeserializer;
import java.util.HashMap;
import java.util.Map;

public class JsonSerdeProvider<T extends Object>{
    public Serde<T> getSerde(Class<T> type){
        Map<String, Object> serdeProps = new HashMap<>();
        serdeProps.put("json.value.type", type);

        final Serializer<T> serializer = new KafkaJsonSerializer<>();
        serializer.configure(serdeProps, false);
                
        final Deserializer<T> deserializer = new KafkaJsonDeserializer<>();
        deserializer.configure(serdeProps, false);

        return Serdes.serdeFrom(serializer, deserializer);
    }
}