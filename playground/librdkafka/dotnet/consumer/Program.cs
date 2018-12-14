using System;
using System.Threading;
using Confluent.Kafka;

class Program
{
    public static void Main(string[] args)
    {
        Console.WriteLine(">>> Starting .NET Consumer - reading from \"test-topic\"");
        string topicName = "test-topic";
        var conf = new ConsumerConfig
        { 
            GroupId = "test-consumer-group",
            BootstrapServers = "kafka:9092",
            AutoOffsetReset = AutoOffsetResetType.Earliest,
            PluginLibraryPaths = "monitoring-interceptor"
        };

        using (var c = new Consumer<Ignore, string>(conf))
        {
            c.Subscribe(topicName);

            bool consuming = true;
            // The client will automatically recover from non-fatal errors. You typically
            // don't need to take any action unless an error is marked as fatal.
            c.OnError += (_, e) => consuming = !e.IsFatal;

            while (consuming)
            {
                try
                {
                    var cr = c.Consume();
                    // Console.WriteLine($"Consumed message '{cr.Value}' at: '{cr.TopicPartitionOffset}'.");
                    Console.Write("+");
                    Thread.Sleep(10);
                }
                catch (ConsumeException e)
                {
                    Console.WriteLine($"Error occured: {e.Error.Reason}");
                }
            }
            
            c.Close();
            Console.WriteLine("\r\n<<< Ending .NET Producer");
        }
    }
}