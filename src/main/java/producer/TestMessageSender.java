package producer;

import networkManager.KafkaMeddageSender;

public class TestMessageSender {
	public  static  void main(String[] args) throws InterruptedException {
		KafkaMeddageSender sender = new KafkaMeddageSender("192.168.254.129:9091,192.168.254.129:9092,192.168.254.129:9093,192.168.254.129:9094","test_in");
//		KafkaMeddageSender sender = new KafkaMeddageSender("192.168.254.129:9094","test_in");
		for (int i = 1; i < 10000; i++) {
			sender.sendMessage("This is a message using java " + i);
			Thread.sleep(500);
		}
	}
}
