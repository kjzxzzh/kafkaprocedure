package bolt;

import java.util.HashSet;
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
import networkManager.KafkaMeddageSender;
import topology.Constants;
import topology.KafkaTopology;

public class GenerateBlock extends BaseRichBolt {
    private OutputCollector collector;
    org.slf4j.Logger logger;
    private KafkaMeddageSender sender;
    List<String> hashSets = new LinkedList<String>();
    public int height = 0;
    
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
        this.sender = new KafkaMeddageSender(Constants.kafkaList,"block_info");
	}

	public void execute(Tuple input) {
		String jsonString = input.getString(0);
		hashSets.add(jsonString);
        collector.emit(new Values(input.getString(0),1));
        collector.ack(input);
		if (hashSets.size() >= 4) {
			// 获取交集
			HashSet<Transaction> transactionSet = null;
			for (String s: hashSets) {
				HashSet<Transaction> transet = JSON.parseObject(s,new TypeReference<HashSet<Transaction>>(){});
				if (transactionSet == null) transactionSet = transet;
				else transactionSet.retainAll(transet);
//				for (Transaction t :transactionSet) {
//					logger.error("transactionSet = [" + JSONObject.toJSONString(t) + "]"); 
//				}
//				for (Transaction t :transet) {
//					logger.error("transet = [" + JSONObject.toJSONString(t) + "]"); 
//				}
			}
			// 构建块
			Block block = new Block(transactionSet, this.height);
			
			//广播
			this.sender.sendMessage(JSONObject.toJSONString(block));
			this.height = this.height + 1;
			//清空hashSets
			hashSets.clear();
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

}
