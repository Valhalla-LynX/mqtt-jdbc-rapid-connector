package com.bjdv.kafka;

import com.alibaba.fastjson.JSONObject;
import com.bjdv.dbconnector.model.TopicModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
* @description:
* @author: LX
* @create: 2022-01-07 11:28
**/
@Service
@Slf4j
public class KafkaService {
    private KafkaConsumer consumer;
    private ExecutorService proExe = Executors.newSingleThreadExecutor();
    private ExecutorService conExe = Executors.newSingleThreadExecutor();
    private DemoCon DemoCon = new DemoCon();
    private DemoPro DemoPro = new DemoPro();

    private void clientConnect() {
        /*Properties props = new Properties();
        props.setProperty("bootstrap.servers","192.168.50.222:9092,192.168.50.223:9092,192.168.50.224:9092");
        props.setProperty("group.id", "dbc");
        //设置数据key和value的序列化处理类
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");*/

        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers","192.168.50.222:9092,192.168.50.223:9092,192.168.50.224:9092");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(properties);
        //consumer.subscribe(topicList);
        try{
            while (true) {
                //拉取消息
                if (consumer.subscription().size()>0) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, String> record : records) {
                        System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                    }
                }
            }
        }finally{
            consumer.close();
        }
    }

    public void subscribe(TopicModel topic) {
        subscribe(topic.getTopic());
    }

    private void subscribe(String topic) {
        consumer.subscribe(List.of(topic));
        log.info("LOG---subscribe topic: {}", topic);
    }

    class DemoProducerCallback implements Callback{
        @Override
        public void onCompletion(RecordMetadata recordMetadata, Exception e) {
            if (e != null) {//如果Kafka返回一个错误，onCompletion方法抛出一个non null异常。
                e.printStackTrace();//对异常进行一些处理，这里只是简单打印出来
            }
        }
    }

    @SuppressWarnings({"BusyWait"})
    public void sender() throws InterruptedException {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers","192.168.50.222:9092,192.168.50.223:9092,192.168.50.224:9092");
        props.setProperty("group.id", "dbc");
        //设置数据key和value的序列化处理类
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer producer = new KafkaProducer(props);

        while (true) {
            Thread.sleep(1000);
            ProducerRecord<String, String> asyncRecord = new ProducerRecord<>("dbc_test", "test","test_v");
            producer.send(asyncRecord, new DemoProducerCallback());//发送消息时，传递一个回调对象，该回调对象必须实现org.apache.kafka.clients.producer.Callback接口
            System.out.println(asyncRecord.key()+" "+asyncRecord.value());
        }
    }

    public class DemoPro implements Runnable {
        @SneakyThrows
        @Override
        public void run() {
            Properties props = new Properties();
            props.setProperty("bootstrap.servers","192.168.50.222:9092,192.168.50.223:9092,192.168.50.224:9092");
            props.setProperty("group.id", "dbc");
            //设置数据key和value的序列化处理类
            props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

            KafkaProducer producer = new KafkaProducer(props);

            while (true) {
                Thread.sleep(1000);
                ProducerRecord<String, String> asyncRecord = new ProducerRecord<>("dbc_test", "test","test_v");
                producer.send(asyncRecord, new DemoProducerCallback());//发送消息时，传递一个回调对象，该回调对象必须实现org.apache.kafka.clients.producer.Callback接口
                System.out.println(asyncRecord.key()+" "+asyncRecord.value());
            }
        }
    }
    public class DemoCon implements Runnable {
        @Override
        public void run() {
            Properties properties = new Properties();
            properties.setProperty("bootstrap.servers","192.168.50.222:9092,192.168.50.223:9092,192.168.50.224:9092");
            properties.setProperty("group.id", "dbc");
            properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
            consumer = new KafkaConsumer<>(properties);
            //consumer.subscribe(topicList);
            try{
                while (true) {
                    Thread.sleep(1000);
                    //拉取消息
                    if (consumer.subscription().size()>0) {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                        for (ConsumerRecord<String, String> record : records) {
                            System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
                        }
                    }
                }
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            } finally{
                consumer.close();
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        KafkaService kafka = new KafkaService();
        kafka.conExe.execute(kafka.DemoCon);
        Thread.sleep(3000);
        kafka.consumer.subscribe(List.of("dbc_test"));
        kafka.proExe.execute(kafka.DemoPro);
    }
}
