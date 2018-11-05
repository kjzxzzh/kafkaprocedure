package bolt;

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
import message.HashMapMessage;
import networkManager.KafkaMeddageSender;
import topology.Constants;
import topology.KafkaTopology;

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
    int batchNum = 0;
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
        this.sender = new KafkaMeddageSender(Constants.kafka_1,"hash_map");
        this.transactionSet = new HashSet<Transaction>(); // Ŀǰ�̲߳���ȫ
    }

    public void execute(Tuple input) {
        //��������
    	String jsonString = input.getString(0);
    	Transaction trans = JSON.parseObject(jsonString,new TypeReference<Transaction>(){});
        
        //��֤requestHashSetǩ����Ч��,�����׼��뵽hashmap��
        if (trans.valid()) {
        	transactionSet.add(trans);
//        	logger.error("Correct request = [" + jsonString + "]"); 
        }
        else {
//        	logger.error("Error request = [" + jsonString + "]"); 
        }
        collector.emit(new Values(input.getString(0),1));
        collector.ack(input);
        
        // �������100������ �� ���͸����ڵ�
        if (transactionSet.size() >= Constants.reqThredhold) {
        	HashMapMessage message = new HashMapMessage();
        	message.batchNum = batchNum;
        	message.nodeLabel = Constants.nodeLabel;
        	message.transactionSet = transactionSet;
        	logger.error("Correct request = [" + jsonString + "]"); 
        	this.sender.sendMessage(JSONObject.toJSONString(message));
        	transactionSet.clear();
        	batchNum += 1;
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }

}
