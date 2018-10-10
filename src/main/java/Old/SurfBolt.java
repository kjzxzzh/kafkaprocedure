package Old;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;

import TestConsumer.KafkaTopology;

public class SurfBolt extends BaseRichBolt{
    private OutputCollector collector;

    org.slf4j.Logger logger;
    
    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        logger = org.slf4j.LoggerFactory.getLogger(KafkaTopology.class);
    }

    @Override
    public void execute(Tuple input) {
        logger.error("SurfBolt run success. first msg=[" + input.getString(0) + "]");
        System.out.println("SurfBolt run success. first msg=[" + input.getString(0) + "]");
        collector.ack(input);
        collector.emit(new Values(input.getString(0),1));

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }

}
