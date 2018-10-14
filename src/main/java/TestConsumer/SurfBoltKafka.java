package TestConsumer;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Map;
import java.util.Properties;

public class SurfBoltKafka extends BaseRichBolt{
    private OutputCollector collector;

    org.slf4j.Logger logger;

    
    private static final String TOPIC = "test_out"; //kafka閸掓稑缂撻惃鍓噊pic
    private static final String BROKER_LIST = "192.168.254.129:9092";//,192.168.71.144:32769"; //broker閻ㄥ嫬婀撮崸锟介崪宀�顏崣锟�
    private static final String SERIALIZER_CLASS = "kafka.serializer.StringEncoder"; // 鎼村繐鍨崠鏍
    private Producer<String, String> producer;
    
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
        
        Properties props = new Properties();
        props.put("serializer.class", SERIALIZER_CLASS);
        props.put("metadata.broker.list", BROKER_LIST);
        ProducerConfig config = new ProducerConfig(props);
        producer = new Producer<String, String>(config);

    }

    public void execute(Tuple input) {
    	
        logger.error("SurfBolt run success. first msg=[" + input.getString(0) + "]");
        
        KeyedMessage<String, String> message = new KeyedMessage<String, String>(TOPIC, input.getString(0));
        producer.send(message);
        
        collector.emit(new Values(input.getString(0),1));
        collector.ack(input);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }

}
