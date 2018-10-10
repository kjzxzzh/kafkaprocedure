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

        //Send one message.
        KeyedMessage<String, String> message =
                new KeyedMessage<String, String>(TOPIC, CONTENT);
        producer.send(message);
        producer.close();
        System.out.println("close");
        //Send multiple messages.
//        List<KeyedMessage<String,String>> messages =
//                new ArrayList<KeyedMessage<String, String>>();
        for (int i = 1; i < 10000; i++) {
            producer = new Producer<String, String>(config);
            message =
                    new KeyedMessage<String, String>(TOPIC, "This is a single message using java " + i);
            producer.send(message);
            producer.close();
            Thread.sleep(500);
//            messages.add(new KeyedMessage<String, String>
//                    (TOPIC, "Multiple message at a time. " + i));
        }
//        producer.send(messages);
//        producer.close();
    }
}

