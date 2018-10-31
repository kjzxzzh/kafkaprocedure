package producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.alibaba.fastjson.JSONObject;

import bean.Transaction;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class JavaProducer {
//    private static final String TOPIC = "original_request"; 
	private static final String TOPIC = "original_request"; 
    private static final String CONTENT = "This is a single message using java 1"; 
    private static final String BROKER_LIST = "192.168.254.129:9091";
    private static final String SERIALIZER_CLASS = "kafka.serializer.StringEncoder"; 

    public  static  void main(String[] args) throws InterruptedException {

        Properties props = new Properties();
        props.put("serializer.class", SERIALIZER_CLASS);
        props.put("metadata.broker.list", BROKER_LIST);


        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);

        //Send  message.
        KeyedMessage<String, String> message ;
        for (int i = 1; i < 500000; i++) {
            message = new KeyedMessage<String, String>(TOPIC,JSONObject.toJSONString(new Transaction("in1","out",i)));
            producer.send(message);
            System.out.println(i);
            Thread.sleep(50);
        }
        producer.close();
    }
}

