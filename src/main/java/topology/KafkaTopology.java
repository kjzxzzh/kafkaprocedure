package topology;

import backtype.storm.tuple.Fields;
import bean.NodeInformation;
import bolt.GenerateHashMap;
import networkManager.KafkaMeddageSender;
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.storm.hbase.bolt.HBaseBolt;
//import org.apache.storm.hbase.bolt.mapper.SimpleHBaseMapper;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

import storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import storm.kafka.bolt.selector.DefaultTopicSelector;
import storm.kafka.StringScheme;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.spout.SchemeAsMultiScheme;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.RequestingUserName;

public class KafkaTopology {

    public static void main(String[] args) throws InterruptedException {
    	// ����node ��Ϣ
    	String nodeLabel;
        if (args != null && args.length > 0) {
        	nodeLabel = args[0];
        }
        else {
        	nodeLabel = "0";
        }
        Constants.set(Integer.parseInt(nodeLabel));
        System.out.println("WordCountTopology main start,node number :" + nodeLabel);
        

        TopologyBuilder builder = new TopologyBuilder();
        Config config = new Config();
        config.setDebug(false);
        /**
         * �������󣬷���hashmap
         */
        BrokerHosts brokerHosts = new ZkHosts(CommonUtil.joinHostPort(Constants.host, Constants.zkPort));
        SpoutConfig spoutConfig = new SpoutConfig(brokerHosts,"original_request", "", "topo");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
        spoutConfig.zkServers = CommonUtil.strToList(Constants.host);
        spoutConfig.zkPort = Integer.valueOf(Constants.zkPort);
        builder.setSpout("RandomSentence", new KafkaSpout(spoutConfig), 1);
        builder.setBolt("boltKafka", new GenerateHashMap(), 1).shuffleGrouping("RandomSentence");


        /**
         * ���ڵ��յ�hashmap ����
         */
        if ("0".equals(nodeLabel)) {
        	
        }
        
        
        /**
         * ���нڵ���֤block ��ͶƱ
         */

        /**
         * ת��ͶƱ���
         */
        
        /**
         * �ɹ����洢
         */
        
        /**
         *hbase
         */
//        Configuration config1 = HBaseConfiguration.create();
//        SimpleHBaseMapper Mapper = new SimpleHBaseMapper()
//                .withRowKeyField("word")
//                .withColumnFields(new Fields("count"))
//                .withColumnFamily("result");
//        HBaseBolt hbaseBolt = new HBaseBolt("wordcount", Mapper).withConfigKey("hbase");
//        builder.setBolt("HbaseBolt", hbaseBolt, 1)
//                .addConfiguration("hbase", new HashMap<String, Object>())
//                .shuffleGrouping("countBolt");
//        System.setProperty("hadoop.home.dir", "C://hadoop//");
//
//                SimpleHBaseMapper mapper = new SimpleHBaseMapper()
//                .withRowKeyField("word")
//                .withColumnFields(new Fields("count"))
////                .withCounterFields(new Fields("count"))
//                .withColumnFamily("result");
//
//        Map<String, Object> hbConf = new HashMap<String, Object>();
//        hbConf.put("hbase.rootdir", "hdfs://192.168.71.144:9000/hbase");
//        hbConf.put("hbase.zookeeper.quorum", "192.168.71.144:32768");
//        config.put("hbase.conf", hbConf);
////        HBaseBolt hbase = new HBaseBolt("wordCount", mapper).withConfigKey("hbase.conf");
//        HBaseBolt hBaseBolt = new HBaseBolt("wordcount", mapper).withConfigKey("hbase.conf");

//        builder.setBolt("hbaseBolt", hbase, 1).shuffleGrouping("SurfBolt");
//        builder.setBolt("hbase", hBaseBolt, 3).shuffleGrouping("writer");

        
        
        if (args != null && args.length > 0) {
        	System.out.println(args[0]);
        	Constants.isMain = false;
            config.setNumWorkers(1);
            config.put(Config.NIMBUS_HOST, args[0]);
            try {
                StormSubmitter.submitTopology("WordCountTopology", config, builder.createTopology());
                System.out.println("submitTopology success!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            config.setMaxTaskParallelism(1);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("WordCountTopology", config, builder.createTopology());
        }

        System.out.println("WordCountTopology main end!");
    }
}