package Old;


import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.HashMap;
import java.util.Map;


public class WordCountBolt extends BaseBasicBolt {
    private Map<String, Integer> counts = new HashMap<String, Integer>();

    public void execute(Tuple input, BasicOutputCollector collector) {
        String level = input.getStringByField("value");
        Integer count = counts.get(level);
        if (count == null)
            count = 0;
        count++;
        counts.put(level, count);
        System.out.println("WordCountBolt Receive : "+level+"   "+count);
        collector.emit(new Values(level, count.toString()));
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "count"));
    }
}