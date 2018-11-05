package producer;

import com.alibaba.fastjson.JSONObject;

import bean.Transaction;
import kafka.producer.KeyedMessage;
import networkManager.KafkaMeddageSender;

public class TestMessageSender {
	public  static  void main(String[] args) throws InterruptedException {
		KafkaMeddageSender sender = new KafkaMeddageSender("192.168.254.129:9091,192.168.254.129:9092,192.168.254.129:9093,192.168.254.129:9094","original_request");
//		KafkaMeddageSender sender = new KafkaMeddageSender("192.168.254.129:9094","original_request");

        for (int i = 0; i < 100; i++) {
        	for (int j = 0 ; j < 100 ;j++) {
        		sender.sendMessage(JSONObject.toJSONString(new Transaction("1","2",i * 100 + j)));
        	}
        	System.out.println(i);
            Thread.sleep(1000);
        }
        System.out.println("done");
	}
}
