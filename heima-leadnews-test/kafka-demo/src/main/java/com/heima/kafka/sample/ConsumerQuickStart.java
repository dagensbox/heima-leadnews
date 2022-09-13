package com.heima.kafka.sample;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

/**
 * @author 12141
 */
public class ConsumerQuickStart {

    public static void main(String[] args) {
        //1. kafka的配置信息
        Properties props = new Properties();
        //kafka的连接地址
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.80.1:9092");
        //消费者组
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group2");
        //消息key的序列化器
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        //消息value的序列化器
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);


        //2、消费者对象
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        //3、 订阅主题
        consumer.subscribe(Collections.singletonList("itcast-topic-out"));




        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(2));
                for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                    System.out.println(consumerRecord.key());
                    System.out.println(consumerRecord.value());
                    System.out.println(consumerRecord.offset());
                    System.out.println(consumerRecord.partition());
                    System.out.println(consumerRecord);
                }
                consumer.commitAsync();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("记录错误信息: " + e);
        }finally {
            try {
                consumer.commitSync();
            }finally {
                consumer.close();
            }

        }


    }
}
