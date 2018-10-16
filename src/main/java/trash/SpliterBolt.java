package Old;

import java.util.StringTokenizer;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class SpliterBolt extends BaseBasicBolt{
    @Override
    public void execute(Tuple tuple, BasicOutputCollector collector){

        String sentence = tuple.getString(0);

        StringTokenizer iter = new StringTokenizer(sentence);

        while(iter.hasMoreElements()){
            collector.emit(new Values(iter.nextToken()));
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer){

        declarer.declare(new Fields("word"));
    }
}