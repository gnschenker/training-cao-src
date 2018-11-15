package clients;

import java.lang.Math;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import clients.model.TemperatureKey;
import clients.model.TemperatureValue;

public class TemperaturesProducer {
    List<Station> _allStations;
    KafkaProducer<Object,Object> _producer;
    final String _topicName = getEnvrionmentValue("TOPIC_NAME", "temperatures_topic");

    private KafkaProducer<Object,Object> createProducer() throws IOException {
        Properties props = new Properties();
    	InputStream input = new FileInputStream("/app/producer.properties");
        props.load(input);
        KafkaProducer<Object, Object> producer = new KafkaProducer<>(props);
        return producer;
    }

    private List<Station> getStations(String fileName, int minIndex, int maxIndex){
        if(_allStations == null){
            _allStations = new CsvParser().parse(fileName);
        }
        return _allStations.subList(minIndex, maxIndex+1);
    }

    private float getNextTemperature(float currentTemp, float average, float deltaTemp){
        float mu = (currentTemp - average) / deltaTemp;
        float rand = (float)Math.random();
        if(Math.abs(mu) < 0.5f){
            return currentTemp + Math.signum(rand - 0.5f) * deltaTemp;
        } 
        if (Math.abs(mu) > Math.abs(rand)){
            return currentTemp - deltaTemp * Math.signum(mu);
        } 
        return currentTemp + deltaTemp * Math.signum(mu);
    }

    private TemperatureKey getTemperatureKey(String stationId) {
        return new TemperatureKey(stationId);
    }

    private TemperatureValue getTemperatureValue(String stationId, float temperature) {
        Date date= new Date();
        long probeTime = date.getTime();
        return new TemperatureValue(stationId, probeTime, temperature);
    }

    private void publishTemperature(String stationId, float temperature){
        System.out.printf("%s: %a%n", stationId, temperature);
        TemperatureKey tempKey = getTemperatureKey(stationId);
        TemperatureValue tempValue = getTemperatureValue(stationId, temperature);
        ProducerRecord<Object, Object> avroRecord = new ProducerRecord<>(_topicName, tempKey, tempValue);
        _producer.send(avroRecord);
    }

    // generate (random) start temperature for each station
    private void generateStartTemperatures(List<Station> stations){
        for(Station station : stations){
            float avg = station.AverageTemperature;
            float deltaT = station.TemperatureDelta;
            float T0 = avg + deltaT * (2.0f * (float)Math.random() - 1.0f);
            station.CurrentTemperature = T0;
        }
    }

    private String getEnvrionmentValue(String key, String defaultValue){
        String value = System.getenv(key);
        return value == null ? defaultValue : value;
    }

    public void produce() throws IOException{
        _producer = createProducer();
        int minIndex = Integer.parseInt(getEnvrionmentValue("MIN_INDEX", "0"));
        int maxIndex = Integer.parseInt(getEnvrionmentValue("MAX_INDEX", "1000"));
        List<Station> stations = getStations("/app/stations.csv", minIndex, maxIndex);
        generateStartTemperatures(stations);

        while(true){
            for(Station station : stations){
                String stationId = station.StationId;
                float avg = station.AverageTemperature;
                float deltaT = station.TemperatureDelta;
                float T = station.CurrentTemperature;
                T = getNextTemperature(T, avg, deltaT);
                publishTemperature(stationId, T);
                station.CurrentTemperature = T;
            }
        }
    }

    private void terminate(){
        _producer.flush();
    }

    public static void main(String[] args) {
        System.out.println("*** Starting Temperature Producer ***");

        TemperaturesProducer producer = new TemperaturesProducer();
        try{
            producer.produce();
        } catch(IOException ex){
            ex.printStackTrace();
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("### Stopping Temperature Producer ###");
            producer.terminate();
        }));
    }
}
