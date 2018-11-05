package bolt;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import bean.Block;
import bean.Transaction;
import bean.VoteInformationFirst;
import message.HashMapMessage;
import networkManager.KafkaMeddageSender;
import topology.Constants;
import topology.KafkaTopology;

public class GenerateBlock extends BaseRichBolt {
    private OutputCollector collector;
    org.slf4j.Logger logger;
    private KafkaMeddageSender sender;
    private Hashtable<Integer, List<HashMapMessage>> hashMapMessages = new Hashtable<Integer, List<HashMapMessage>>();
    public int height = 0;
    
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
        this.sender = new KafkaMeddageSender(Constants.kafkaList,"block_info");
	}

	public void execute(Tuple input) {
		String jsonString = input.getString(0);
		HashMapMessage message = JSON.parseObject(jsonString,HashMapMessage.class);
		if (hashMapMessages.containsKey(message.batchNum) == false) hashMapMessages.put(message.batchNum, new LinkedList<HashMapMessage>());
		hashMapMessages.get(message.batchNum).add(message);
		
        collector.emit(new Values(input.getString(0),1));
        collector.ack(input);
        
		if (hashMapMessages.get(message.batchNum).size() >= 4) {
			// 获取交集
			HashSet<Transaction> transactionSet = null;
			for (HashMapMessage m: hashMapMessages.get(message.batchNum)) {
				HashSet<Transaction> transet = m.transactionSet;
				if (transactionSet == null) transactionSet = transet;
				else transactionSet.retainAll(transet);
			}
			// 构建块
			Block block = new Block(transactionSet, this.height,hashMapMessages.get(message.batchNum).get(0).batchNum);
			
			//广播
			this.sender.sendMessage(JSONObject.toJSONString(block));
			this.height = this.height + 1;
			//清空hashSets
			hashMapMessages.remove(message.batchNum);
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

}
