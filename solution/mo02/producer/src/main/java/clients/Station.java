package clients;

public class Station {
    public String StationId;
    public Float AverageTemperature;
    public Float TemperatureDelta;
    public Float CurrentTemperature;

    public Station(String line){
        String[] values = line.split(",");
        StationId = values[0];
        AverageTemperature = Float.parseFloat(values[1]);
        TemperatureDelta = Float.parseFloat(values[2]);
    }
}