package bolt;

import java.util.HashSet;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import bean.Transaction;
import networkManager.KafkaMeddageSender;
import topology.Constants;
import topology.KafkaTopology;

public class GenerateBlock extends BaseRichBolt {
    private OutputCollector collector;
    org.slf4j.Logger logger;
    private KafkaMeddageSender sender;

    
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
        this.sender = new KafkaMeddageSender(Constants.kafkaList,"hash_map");
	}

	public void execute(Tuple input) {
		// TODO Auto-generated method stub

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

}
