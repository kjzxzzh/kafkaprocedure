package testConsumer;
import java.util.ArrayList;

import org.apache.hadoop.hdfs.protocol.proto.HdfsProtos.SnapshottableDirectoryListingProto;

import bean.NodeInformation;


public class Constants  {
	public static ArrayList<NodeInformation> nodelist = new ArrayList<NodeInformation>(); 
	
	public static void set() {
		nodelist.add(new NodeInformation(host_1,zkPort_1,kafka_1));
		nodelist.add(new NodeInformation(host_2,zkPort_2,kafka_2));
		nodelist.add(new NodeInformation(host_3,zkPort_3,kafka_3));
		nodelist.add(new NodeInformation(host_4,zkPort_4,kafka_4));
	}
	
	// node_1(main)
	public static String host_1 = "192.168.254.129";
	public static String zkPort_1 = "2181";
	public static String kafka_1 = "192.168.254.129:9091";	
	
	// node_2(main)
	public static String host_2 = "192.168.254.129";
	public static String zkPort_2 = "2182";
	public static String kafka_2 = "192.168.254.129:9092";
	
	// node_1(main)
	public static String host_3 = "192.168.254.129";
	public static String zkPort_3 = "2183";
	public static String kafka_3 = "192.168.254.129:9093";
	
	// node_1(main)
	public static String host_4 = "192.168.254.129";
	public static String zkPort_4 = "2184";
	public static String kafka_4 = "192.168.254.129:9094";

    public static int reqThredhold = 100;
    public static boolean isMain = false;
    
    public static String kafkaList = "192.168.254.129:9091,192.168.254.129:9092,192.168.254.129:9093,192.168.254.129:9094";
    
    public static String topic = "original_request";
}