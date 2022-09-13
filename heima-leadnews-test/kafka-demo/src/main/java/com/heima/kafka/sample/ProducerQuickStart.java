package com.heima.kafka.sample;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

/**
 * 生产者
 *
 * @author 12141
 */
public class ProducerQuickStart {

    public static void main(String[] args) {
        //1. kafka的配置信息
        Properties props = new Properties();
        //kafka的连接地址
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.80.1:9092");
        //发送失败，失败的重试次数
        props.put(ProducerConfig.RETRIES_CONFIG, 5);
        //消息key的序列化器
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //消息value的序列化器
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        //ack配置 消息确认机制
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        //数据压缩
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");


        //2、生产者对象
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        //封装发送的消息
//        ProducerRecord<String, String> record = new ProducerRecord<>("itcast_topic_input", 0, "100001", "hello kafka");

        //3、消息发送
//        producer.send(record);

/*        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.out.println("记录异常信息到日志表");
            }
            System.out.println(metadata.offset());
        });*/

        for (int i = 0; i < 5; i++) {
            ProducerRecord<String,String> kvProducerRecord = new ProducerRecord<String,String>("itcast-topic-input","hello sb");
            producer.send(kvProducerRecord);
        }


        //4、关闭消息通道，必须关闭，否则，消息发送不成功
        producer.close();

    }

}
