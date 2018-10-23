package trash;
import java.util.Arrays;
import java.util.Properties;
//import org.apache.kafka.clients.consumer.ConsumerRecord;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;

public class TestConJava {
    public static void main(String[] args) {
        Properties props = new Properties();

        props.put("bootstrap.servers", "192.168.71.144:9092");
        System.out.println("this is the group part test 1");
        //娑堣垂鑰呯殑缁刬d
        props.put("group.id", "Group1");//杩欓噷鏄疓roupA鎴栬�匞roupB

        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");

        //浠巔oll(鎷�)鐨勫洖璇濆鐞嗘椂闀�
        props.put("session.timeout.ms", "30000");


        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

//        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
//        //璁㈤槄涓婚鍒楄〃topic
//        consumer.subscribe(Arrays.asList("test2"));
//
//        while (true) {
//            ConsumerRecords<String, String> records = consumer.poll(100);
//            for (ConsumerRecord<String, String> record : records)
//                //銆�姝ｅ父杩欓噷搴旇浣跨敤绾跨▼姹犲鐞嗭紝涓嶅簲璇ュ湪杩欓噷澶勭悊
//                System.out.printf("offset = %d, key = %s, value = %s", record.offset(), record.key(), record.value() + "\n");
//        }
    }
}