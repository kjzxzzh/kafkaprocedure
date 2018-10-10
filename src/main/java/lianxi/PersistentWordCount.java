package lianxi; /**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import org.apache.storm.hbase.bolt.HBaseBolt;
import org.apache.storm.hbase.bolt.mapper.SimpleHBaseMapper;

import java.util.HashMap;
import java.util.Map;

public class PersistentWordCount {
    private static final String WORD_SPOUT = "WORD_SPOUT";
    private static final String COUNT_BOLT = "COUNT_BOLT";
    private static final String HBASE_BOLT = "HBASE_BOLT";


    public static void main(String[] args) throws Exception {
        Config config = new Config();

        Map<String, Object> hbConf = new HashMap<String, Object>();

        hbConf.put("hbase.rootdir", "hdfs://192.168.71.144:9000/hbase");
        config.put("hbase.conf", hbConf);

        WordSpout spout = new WordSpout();
        WordCounter bolt = new WordCounter();

        hbConf.put("hbase.rootdir", "hdfs://192.168.71.144:9000/hbase");
        hbConf.put("hbase.zookeeper.quorum", "192.168.71.144");
        hbConf.put("hbase.zookeeper.property.clientPort", "2181");
        SimpleHBaseMapper mapper = new SimpleHBaseMapper()
                .withRowKeyField("word")
                .withColumnFields(new Fields("word"))
                .withCounterFields(new Fields("count"))
                .withColumnFamily("cf");
        HBaseBolt hbase = new HBaseBolt("WordCount", mapper)
                .withConfigKey("hbase.conf");

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout(WORD_SPOUT, spout, 1);
        builder.setBolt(COUNT_BOLT, bolt, 1).shuffleGrouping(WORD_SPOUT);
        builder.setBolt(HBASE_BOLT, hbase, 1).fieldsGrouping(COUNT_BOLT, new Fields("word"));

        String topoName = "test";

        if (args != null && args.length > 0) {
            config.setNumWorkers(1);
//            config.put(Config.NIMBUS_HOST, args[0]);
            try {
                StormSubmitter.submitTopology("WordCountTopology", config, builder.createTopology());
                System.out.println("submitTopology success!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("local mode");
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("WordCountTopology", config, builder.createTopology());
//            cluster.submitTopology(topoName, config, builder.createTopology());
        }

    }
}
