package bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import bean.Block;
import bean.VoteInformationFirst;
import bean.VoteInformationSec;
import topology.KafkaTopology;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.sun.tools.javac.util.List;

public class SurfBolt extends BaseRichBolt{
    private OutputCollector collector;
	public Hashtable<Integer, LinkedList<VoteInformationSec>> voteInformations = new Hashtable<Integer, LinkedList<VoteInformationSec>>();
	private Set<Integer> already = new HashSet<Integer>();
    org.slf4j.Logger logger;
    
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
    }

    public void execute(Tuple input) {
    	String jsonString = input.getString(0);
    	VoteInformationSec voteInformationSec = JSON.parseObject(jsonString,VoteInformationSec.class);
    	
		if (already.contains(voteInformationSec.blockheight)) {
	        collector.ack(input);
	        return;
		}
		
		if (voteInformations.containsKey(voteInformationSec.blockheight) == false){
			voteInformations.put(voteInformationSec.blockheight, new LinkedList<VoteInformationSec>());
		}
		voteInformations.get(voteInformationSec.blockheight).add(voteInformationSec);
        collector.ack(input);
        
        if (voteInformations.get(voteInformationSec.blockheight).size() >= 3){
        	Block block = voteInformations.get(voteInformationSec.blockheight).get(0).voteInformations.get(0).block;
        	collector.emit(new Values(   String.valueOf(block.height), block.pre_hash ,block.hash,block.timestamp, block.merkle_root , String.valueOf(block.transactions.size())	));
        	
        	voteInformations.remove(voteInformationSec.blockheight);
        	logger.error("Block Success=[" + block.toString() + "]");
        	already.add(voteInformationSec.blockheight);
        }
        
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("height", "pre_hash" ,"hash", "timestamp","merkle_root","transactions"));
    }

}
