package testConsumer;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import bean.Transaction;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import networkManager.KafkaMeddageSender;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

public class GenerateHashMap extends BaseRichBolt{
    private OutputCollector collector;

    org.slf4j.Logger logger;
    private KafkaMeddageSender sender;
    HashSet<Transaction> transactionSet ;
    
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
        this.sender = new KafkaMeddageSender(Constants.kafka_1,"hash_map");
        this.transactionSet = new HashSet<Transaction>(); // 目前线程不安全
    }

    public void execute(Tuple input) {
        //解析交易
    	String jsonString = input.getString(0);
    	Transaction trans = JSON.parseObject(jsonString,new TypeReference<Transaction>(){});
        
        //验证requestHashSet签名有效性,将交易加入到hashmap中
        if (trans.valid()) {
        	transactionSet.add(trans);
        	logger.error("Correct request = [" + jsonString + "]"); 
        }
        else {
        	logger.error("Error request = [" + jsonString + "]"); 
        }
        collector.emit(new Values(input.getString(0),1));
        collector.ack(input);
        
        // 如果超过100个请求 ， 发送给主节点
        if (transactionSet.size() > Constants.reqThredhold) {
        	this.sender.sendMessage(JSONObject.toJSONString(transactionSet));
        	transactionSet.clear();
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }

}
