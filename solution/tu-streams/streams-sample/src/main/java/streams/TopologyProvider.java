package streams;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.Materialized;

import java.util.Arrays;
import java.util.regex.Pattern;

public class TopologyProvider {
    public Topology getTopology(){
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();
        final Serde<PageView> pageViewSerde = new JsonSerdeProvider<PageView>().getSerde(PageView.class);
        final Serde<User> userSerde = new JsonSerdeProvider<User>().getSerde(User.class);
        final Serde<UserClick> userClickSerde = new JsonSerdeProvider<UserClick>().getSerde(UserClick.class);

        final StreamsBuilder builder = new StreamsBuilder();
    
        final KStream<String, PageView> pageviews = builder.stream("pageviews", Consumed.with(stringSerde, pageViewSerde));
        final KStream<String, PageView> pageviewsRekeyed = pageviews
            .selectKey((key,value) -> value.userid);
        final KTable<String, User> users = builder.table("users", Consumed.with(stringSerde, userSerde));
        
        final KStream<String, UserClick> enriched = pageviewsRekeyed
            .join(users, (lv, rv) -> {
                UserClick x = new UserClick(lv.userid, rv.regionid, rv.gender, lv.pageid);
                return x;
            }, Joined.with(stringSerde, pageViewSerde, userSerde));
            
        final KTable<String, Long> grouped = enriched
            .groupBy((k,v) -> v.regionid, Grouped.as("group").with(stringSerde, userClickSerde))
            .count(Materialized.as("counts").with(stringSerde, longSerde));

        grouped.toStream()
            .to("pageviews-per-region", Produced.with(stringSerde, longSerde));

        Topology topology = builder.build();
        return topology;
    }
}
