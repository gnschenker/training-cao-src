import random
import csv
import signal, sys
import os
from configparser import ConfigParser
from confluent_kafka import avro
from confluent_kafka.avro import AvroProducer

topic_name = 'temperatures'
dT = 0.1

config = ConfigParser()
config.read('producer.properties')
properties = {}
for key in config['DEFAULT']:
    properties[key] = config['DEFAULT'][key]

value_schema_str = """
{"namespace": "app",
 "type": "record",
 "name": "TemperatureValue",
 "fields": [
     {"name": "station_id", "type": "string"},
     {"name": "temperature", "type": "float"}
 ]
}
"""

value_schema = avro.loads(value_schema_str)
producer = AvroProducer(properties, default_value_schema=value_schema)

def signal_handler(sig, frame):
    print('You pressed Ctrl+C!')
    # Wait for any outstanding messages to be delivered and delivery report
    # callbacks to be triggered.
    producer.flush()
    sys.exit(0)


def sign(x): return 1 if x >= 0 else -1


def get_stations(file_name, min, max):
    with open(file_name, 'r') as f:
        reader = csv.reader(f)
        stations_list = list(reader)
        return stations_list[min:max+1]


def get_next_temp(curr_temp, average, DT):
    mu=(curr_temp-average)/DT
    rand = random.random()
    if abs(mu)<0.5:
        T=curr_temp+sign(rand-0.5)*dT
    elif abs(mu)>abs(rand):
        T=curr_temp-dT*sign(mu)
    else:
        T=curr_temp+dT*sign(mu)
    return T


def publish_temperature(station_id, temperature):
    print('{}: {}'.format(station_id, temperature))
    producer.produce(topic_name, key=station_id, value=temperature)

def generate_temperatures(file_name, min_index, max_index):
    stations = get_stations(file_name, min_index, max_index)

    # generate (random) start temperature for each station
    for station in stations:
        avg = float(station[1])
        DT = float(station[2])
        T0 = avg + DT * (2.0 * random.random() - 1.0)
        station.append(T0)

    while True:
        for station in stations:
            station_id = station[0]
            avg = float(station[1])
            DT = float(station[2])
            T = float(station[3])
            T = get_next_temp(T, avg, DT)
            publish_temperature(station_id, T)
            station[3] = T


signal.signal(signal.SIGINT, signal_handler)

min_index = int(os.getenv('MIN_INDEX','0'))
max_index = int(os.getenv('MAX_INDEX', '1000'))
print('Handling stations from index {} to {}'.format(min_index, max_index))
generate_temperatures('stations.csv', min_index, max_index)