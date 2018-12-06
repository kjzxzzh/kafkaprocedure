package producer;

import com.alibaba.fastjson.JSONObject;

import bean.Transaction;
import kafka.producer.KeyedMessage;
import networkManager.KafkaMeddageSender;
import topology.Constants;

public class TestMessageSender {
	public  static  void main(String[] args) throws InterruptedException {
		KafkaMeddageSender sender = new KafkaMeddageSender("192.168.71.144:9091,192.168.71.144:9092,192.168.71.144:9093,192.168.71.144:9094","original_request");
//		KafkaMeddageSender sender = new KafkaMeddageSender("192.168.254.129:9094","original_request");

		long startTime = System.currentTimeMillis();    //��ȡ��ʼʱ��
	
        for (int i = 0; i < 100; i++) {
        	for (int j = 0 ; j < Constants.reqThredhold ;j++) {
        		sender.sendMessage(JSONObject.toJSONString(new Transaction("1","2",i * Constants.reqThredhold + j)));
        	}
        	System.out.println(i);
            Thread.sleep(600);
        }
        System.out.println("done");
		long endTime = System.currentTimeMillis();    //��ȡ����ʱ��

		System.out.println("��������ʱ�䣺" + (endTime - startTime) / 1000  + "s");    //�����������ʱ��
	}
}
