package producer;

import com.alibaba.fastjson.JSONObject;

import bean.Transaction;
import kafka.producer.KeyedMessage;
import networkManager.KafkaMeddageSender;
import topology.Constants;

public class TestMessageSender {
	public  static  void main(String[] args) throws InterruptedException {
		KafkaMeddageSender sender = new KafkaMeddageSender("192.168.254.129:9091,192.168.254.129:9092,192.168.254.129:9093,192.168.254.129:9094","original_request");
//		KafkaMeddageSender sender = new KafkaMeddageSender("192.168.254.129:9094","original_request");

		
		 for (int i = 0; i < 1; i++) {
	        	for (int j = 0 ; j < Constants.reqThredhold ;j++) {
	        		sender.sendMessage(JSONObject.toJSONString(new Transaction("1","2",i * Constants.reqThredhold + j)));
	        	}
	            Thread.sleep(15000);
	        }
		long startTime = System.currentTimeMillis();    //获取开始时间
		System.out.println("start");
        for (int i = 0; i < 5000; i++) {
        	for (int j = 0 ; j < Constants.reqThredhold ;j++) {
        		sender.sendMessage(JSONObject.toJSONString(new Transaction("1","2",i * Constants.reqThredhold + j)));
        	}
        	System.out.println(i);
            Thread.sleep(1000);
        }
        System.out.println("done");
		long endTime = System.currentTimeMillis();    //获取结束时间

		System.out.println("程序运行时间：" + (endTime - startTime) / 1000  + "s");    //输出程序运行时间
	}
}
