package TestProducer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class JavaProducer {
    private static final String TOPIC = "test_in"; 
    private static final String CONTENT = "This is a single message using java 1"; 
    private static final String BROKER_LIST = "192.168.254.129:9092";
    private static final String SERIALIZER_CLASS = "kafka.serializer.StringEncoder"; 

    public  static  void main(String[] args) throws InterruptedException {

        Properties props = new Properties();
        props.put("serializer.class", SERIALIZER_CLASS);
        props.put("metadata.broker.list", BROKER_LIST);


        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);

        //Send  message.
        KeyedMessage<String, String> message ;
        for (int i = 1; i < 10000; i++) {
            message = new KeyedMessage<String, String>(TOPIC, "This is a single message using java " + i);
            producer.send(message);
            Thread.sleep(500);
        }
        producer.close();
    }
}

