package bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import topology.KafkaTopology;

import java.util.Map;

public class SurfBolt extends BaseRichBolt{
    private OutputCollector collector;

    org.slf4j.Logger logger;
    
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
    }

    public void execute(Tuple input) {
        logger.error("Block Success=[" + input.getString(0) + "]");
        collector.ack(input);
        collector.emit(new Values(input.getString(0),1));

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }

}
