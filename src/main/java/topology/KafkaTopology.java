package topology;

import backtype.storm.tuple.Fields;
import bean.NodeInformation;
import bolt.FirstVote;
import bolt.GenerateBlock;
import bolt.GenerateHashMap;
import bolt.SecondVote;
import bolt.SurfBolt;
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
    	// 设置node 信息
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
        config.put("spout.single.thread", true); 
        /**
         * 接受请求，发送hashmap
         */
        BrokerHosts brokerHosts = new ZkHosts(CommonUtil.joinHostPort(Constants.host, Constants.zkPort));
        SpoutConfig spoutConfig = new SpoutConfig(brokerHosts,"original_request", "", "topo");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
        spoutConfig.zkServers = CommonUtil.strToList(Constants.host);
        spoutConfig.zkPort = Integer.valueOf(Constants.zkPort);
        
        builder.setSpout("original_request", new KafkaSpout(spoutConfig), 1);
        builder.setBolt("boltKafka", new GenerateHashMap(), 1).shuffleGrouping("original_request");


        /**
         * 主节点收到hashmap 建块
         */
        if ("0".equals(nodeLabel)) {
            brokerHosts = new ZkHosts(CommonUtil.joinHostPort(Constants.host, Constants.zkPort));
            spoutConfig = new SpoutConfig(brokerHosts,"hash_map", "", "topoforhashmap");
            spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
            spoutConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
            spoutConfig.zkServers = CommonUtil.strToList(Constants.host);
            spoutConfig.zkPort = Integer.valueOf(Constants.zkPort);
            builder.setSpout("hash_map", new KafkaSpout(spoutConfig), 1);
            builder.setBolt("hash_map_bolt", new GenerateBlock(), 1).shuffleGrouping("hash_map");
        }

        /**
         * 所有节点验证block ，投票
         */
        brokerHosts = new ZkHosts(CommonUtil.joinHostPort(Constants.host, Constants.zkPort));
        spoutConfig = new SpoutConfig(brokerHosts,"block_info", "", "topo");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
        spoutConfig.zkServers = CommonUtil.strToList(Constants.host);
        spoutConfig.zkPort = Integer.valueOf(Constants.zkPort);
        
        builder.setSpout("block_info", new KafkaSpout(spoutConfig), 1);
        builder.setBolt("block_info_bolt", new FirstVote(), 1).shuffleGrouping("block_info");
        
        /**
         * 转发投票结果
         */
        brokerHosts = new ZkHosts(CommonUtil.joinHostPort(Constants.host, Constants.zkPort));
        spoutConfig = new SpoutConfig(brokerHosts,"vote_1", "", "topo");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
        spoutConfig.zkServers = CommonUtil.strToList(Constants.host);
        spoutConfig.zkPort = Integer.valueOf(Constants.zkPort);
        
        builder.setSpout("vote_1", new KafkaSpout(spoutConfig), 1);
        builder.setBolt("vote_1_bolt", new SecondVote(), 1).shuffleGrouping("vote_1");
        
        /**
         * 成功，存储
         */
        brokerHosts = new ZkHosts(CommonUtil.joinHostPort(Constants.host, Constants.zkPort));
        spoutConfig = new SpoutConfig(brokerHosts,"vote_2", "", "topo");
        spoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());
        spoutConfig.startOffsetTime = kafka.api.OffsetRequest.EarliestTime();
        spoutConfig.zkServers = CommonUtil.strToList(Constants.host);
        spoutConfig.zkPort = Integer.valueOf(Constants.zkPort);
        
        builder.setSpout("vote_2", new KafkaSpout(spoutConfig), 1);
        builder.setBolt("vote_2_bolt", new SurfBolt(), 1).shuffleGrouping("vote_2");
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

        
        
        if (args != null && args.length > 1) {
        	Constants.isMain = false;
            config.setNumWorkers(1);
            
            config.put(Config.NIMBUS_HOST, args[0]);
            
            
            System.out.println("WordCountTopology main start,node number :" + nodeLabel);
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