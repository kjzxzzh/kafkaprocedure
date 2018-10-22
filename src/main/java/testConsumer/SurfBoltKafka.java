package testConsumer;

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
import networkManager.KafkaMeddageSender;

import java.util.Map;
import java.util.Properties;

public class SurfBoltKafka extends BaseRichBolt{
    private OutputCollector collector;

    org.slf4j.Logger logger;
    private KafkaMeddageSender sender;
    
    
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
        this.sender = new KafkaMeddageSender("192.168.254.129:9091,192.168.254.129:9092,192.168.254.129:9093,192.168.254.129:9094","test_out");
    }

    public void execute(Tuple input) {
        logger.error("SurfBolt run success. first msg=[" + input.getString(0) + "]");  
        this.sender.sendMessage(input.getString(0));
        collector.emit(new Values(input.getString(0),1));
        collector.ack(input);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }

}
