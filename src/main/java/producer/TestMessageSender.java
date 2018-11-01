package producer;

import com.alibaba.fastjson.JSONObject;

import bean.Transaction;
import kafka.producer.KeyedMessage;
import networkManager.KafkaMeddageSender;

public class TestMessageSender {
	public  static  void main(String[] args) throws InterruptedException {
		KafkaMeddageSender sender = new KafkaMeddageSender("192.168.254.129:9091,192.168.254.129:9092,192.168.254.129:9093,192.168.254.129:9094","original_request");
//		KafkaMeddageSender sender = new KafkaMeddageSender("192.168.254.129:9091","original_request");

        for (int i = 0; i < 1000; i++) {
        	sender.sendMessage(JSONObject.toJSONString(new Transaction("in1","out",i)));
            Thread.sleep(50);
        }
        System.out.println("done");
	}
}
