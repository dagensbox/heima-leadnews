package com.heima.kafka.sample;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.function.Function;

/**
 * @author 12141
 */
public class KafkaStreamQuickStart {

    public static void main(String[] args) {
        //kafka的配置中心
        Properties prop = new Properties();
        prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.80.1:9092");
        prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-quickstart");

        //stream 构建器
        StreamsBuilder streamsBuilder = new StreamsBuilder();

        //流式计算
        streamProcessor(streamsBuilder);

        //创建kafkaStream对象
        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), prop);
        //开启流式计算
        kafkaStreams.start();
    }


    /**
     * 流式计算
     * 消息的内容: hello kafka  hello box
     *
     * @param streamsBuilder
     */
    private static void streamProcessor(StreamsBuilder streamsBuilder) {
        //创建kstream对象，同时指定从哪个topic中接收消息
        KStream<String, String> stream = streamsBuilder.stream("itcast-topic-input");

        stream.flatMapValues((ValueMapper<String, Iterable<String>>) value -> Arrays.asList(value.split(" ")))
                .groupBy((key, value) -> value)
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                .count()
                .toStream()
                .map((key, value) -> {
                    System.out.println("key:" + key + ",vlaue:" + value);
                    return new KeyValue<>(key.key().toString(), value.toString());
                }).to("itcast-topic-out");
        System.out.println("hahaha");
    }
}
