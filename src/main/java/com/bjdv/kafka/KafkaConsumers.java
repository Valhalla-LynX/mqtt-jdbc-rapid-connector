//package com.bjdv.kafka;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.stereotype.Service;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//import java.util.Properties;
//
///**
// * @description:
// * @author: LX
// * @create: 2022-01-07 10:54
// **/
//@Service
//@Slf4j
//public class KafkaConsumers implements InitializingBean {
//    private static KafkaConsumer<String, String> consumer;
//
//    private List<String> topicList;
//
//    public static String getNewTopic() {
//        try {
//            return org.apache.commons.io.FileUtils.readLines(new File("D:/topic.txt"), "utf-8").get(0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public KafkaConsumer<String, String> getInitConsumer(List<String> topicList) {
//        //配置信息
//        Properties props = new Properties();
//        //kafka服务器地址
//        props.put("bootstrap.servers", "192.168.9.185:9092");
//        //必须指定消费者组
//        props.put("group.id", "haha");
//        //设置数据key和value的序列化处理类
//        props.put("key.deserializer", StringDeserializer.class);
//        props.put("value.deserializer", StringDeserializer.class);
//        //创建消息者实例
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
//        //订阅topic的消息
//        consumer.subscribe(topicList);
//        return consumer;
//    }
//
//    /**
//     * 开启消费者线程
//     * 异常请自己根据需求自己处理
//     */
//    @Override
//    public void afterPropertiesSet() {
//        // 初始化topic
//        topicList = Splitter.on(","). splitToList(Objects.requireNonNull(getNewTopic()));
//        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(topicList)) {
//            consumer = getInitConsumer(topicList);
//            // 开启一个消费者线程
//            new Thread(() -> {
//                while (true) {
//                    // 模拟从配置源中获取最新的topic（字符串，逗号隔开）
//                    final List<String> newTopic = Splitter.on(",").splitToList(Objects.requireNonNull(getNewTopic()));
//                    // 如果topic发生变化
//                    if (!topicList.equals(newTopic)) {
//                        log.info("topic 发生变化：newTopic:{},oldTopic:{}-------------------------", newTopic, topicList);
//                        // method one：重新订阅topic:
//                        topicList = newTopic;
//                        consumer.subscribe(newTopic);
//                        // method two：关闭原来的消费者，重新初始化一个消费者
//                        //consumer.close();
//                        //topicList = newTopic;
//                        //consumer = getInitConsumer(newTopic);
//                        continue;
//                    }
//                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
//                    for (ConsumerRecord<String, String> record : records) {
//                        System.out.println("key:" + record.key() + "" + ",value:" + record.value());
//                    }
//                }
//            }).start();
//        }
//    }
//}
