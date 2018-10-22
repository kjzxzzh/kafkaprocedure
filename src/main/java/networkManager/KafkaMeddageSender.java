package networkManager;

import java.util.ArrayList;
import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaMeddageSender {
	
    private String topic; 
    private String ip_list;
    private static final String SERIALIZER_CLASS = "kafka.serializer.StringEncoder"; 
    
    ArrayList producer_list = new ArrayList<Producer>(); 
    
	public KafkaMeddageSender(String ip_list , String topic) {
		this.topic = topic;
		
        String[] source_ip = ip_list.split(",");
        for (int i = 0; i < source_ip.length; i++) {
        	// 构建kafka连接
        	Properties props = new Properties();
        	props.put("serializer.class", SERIALIZER_CLASS);
        	props.put("metadata.broker.list", source_ip[i]);
        	System.out.println(source_ip[i]);
            ProducerConfig config = new ProducerConfig(props);
            Producer<String, String> producer = new Producer<String, String>(config);
            //加入到list中
            this.producer_list.add(producer);
        }
	}
        
    public void sendMessage(String mess) {
    	KeyedMessage<String, String> message =  new KeyedMessage<String, String>(this.topic, mess);
        for (int i = 0;i < this.producer_list.size(); i ++) {
        	Producer<String, String> producer  = (Producer<String, String>) (producer_list.get(i));
        	producer.send(message);
        }
    }	
}
