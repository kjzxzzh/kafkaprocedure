package bolt;

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
import networkManager.KafkaMeddageSender;
import topology.Constants;
import topology.KafkaTopology;

public class FirstVote extends BaseRichBolt {
    private OutputCollector collector;
    org.slf4j.Logger logger;
    private KafkaMeddageSender sender;
    
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
        this.sender = new KafkaMeddageSender(Constants.kafkaList,"vote_1");
	}

	public void execute(Tuple input) {
		String jsonString = input.getString(0);
		Block block = (Block) JSON.parseObject(jsonString, Block.class);  
		
		 
		VoteInformationFirst voteInformationFirst = new VoteInformationFirst();
		voteInformationFirst.blockheight = block.height;
		voteInformationFirst.vote = block.valid();
		voteInformationFirst.block = block;
		
		this.sender.sendMessage(JSONObject.toJSONString(voteInformationFirst));
        collector.emit(new Values(input.getString(0),1));
        collector.ack(input);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

}
