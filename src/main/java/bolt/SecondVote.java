package bolt;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import bean.VoteInformationFirst;
import bean.VoteInformationSec;
import networkManager.KafkaMeddageSender;
import scala.collection.generic.BitOperations.Int;
import topology.Constants;
import topology.KafkaTopology;

public class SecondVote extends BaseRichBolt {
    private OutputCollector collector;
    org.slf4j.Logger logger;
    private KafkaMeddageSender sender;
    public Hashtable<Integer, VoteInformationSec> voteInformations = new Hashtable<Integer, VoteInformationSec>();
    private Set<Integer> already = new HashSet<Integer>();
    
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
        this.sender = new KafkaMeddageSender(Constants.kafkaList,"vote_2");

	}

	public void execute(Tuple input) {
		String jsonString = input.getString(0);
		VoteInformationFirst voteInformationFirst = JSON.parseObject(jsonString,VoteInformationFirst.class);
		
		if (already.contains(voteInformationFirst.blockheight)) {
	        collector.ack(input);
	        return;
		}
		
		if (voteInformations.containsKey(voteInformationFirst.blockheight) == false){
			VoteInformationSec tmp = new VoteInformationSec();
			tmp.blockheight = voteInformationFirst.blockheight;
			voteInformations.put(voteInformationFirst.blockheight, tmp);
		}
		voteInformations.get(voteInformationFirst.blockheight).Add(voteInformationFirst);
        collector.emit(new Values(input.getString(0),1));
        collector.ack(input);
        
//        logger.error("receive vote 1 = " + jsonString);
//        logger.error("voteInformationFirst.blockheight = " + voteInformationFirst.blockheight + "\t" + voteInformations.get(voteInformationFirst.blockheight).voteInformations.size());
		if (voteInformations.get(voteInformationFirst.blockheight).valid()) {
			this.sender.sendMessage(JSONObject.toJSONString(voteInformations.get(voteInformationFirst.blockheight)));
			voteInformations.remove(voteInformationFirst.blockheight);
			already.add(voteInformationFirst.blockheight);
		}

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

}
