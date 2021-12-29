package com.bjdv.dbconnector.mqtt;

import com.bjdv.dbconnector.direct.DirectService;
import com.bjdv.dbconnector.process.MessageBufferProcessorManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: LX
 * @create: 2021-10-26 17:32
 **/
@Configuration
@Slf4j
public class MqttTopicHolder implements Serializable {
    private static final String SER = System.getProperty("user.dir") + "/data/topic.ser";
    private final Map<String, MqttTopicModel> topics = new HashMap<>();
    //    private CheckService checkService;
    private DirectService directService;
    private MqttService mqttService;
    private MessageBufferProcessorManager messageProcessorManager;

    public Map<String, MqttTopicModel> getTopics() {
        return topics;
    }

//    @Autowired
//    public void initMqttTopicHolder(CheckService checkService, MqttService mqttService, MessageBufferProcessorManager messageProcessorManager) {
//        this.checkService = checkService;
//        this.mqttService = mqttService;
//        this.messageProcessorManager = messageProcessorManager;
//    }

    @Autowired
    public void initMqttTopicHolder(DirectService directService, MqttService mqttService, MessageBufferProcessorManager messageProcessorManager) {
        this.directService = directService;
        this.mqttService = mqttService;
        this.messageProcessorManager = messageProcessorManager;
    }

    public boolean addTopic(MqttTopicModel topic) {
//        if (checkService.checkTable(topic.getTable())) {
        if (directService.checkTable(topic.getDatasource(), topic.getTable())) {
            if (topics.get(topic.getTopic()) != null) {
                mqttService.unsubscribe(topic);
                messageProcessorManager.inactiveTopicProcessor(topic);
            }
            topics.put(topic.getTopic(), topic);
            messageProcessorManager.activeTopicProcessor(topic);
            mqttService.subscribe(topic);
            serialize();
            return true;
        }
        return false;
    }

    public void removeTopic(MqttTopicModel topic) {
        String name = topic.getTopic();
        if (messageProcessorManager.getManagerMap().containsKey(topic.getTopic())) {
            mqttService.unsubscribe(topic);
            messageProcessorManager.inactiveTopicProcessor(topic);
            topics.remove(name);
            serialize();
        }
    }

    public void serialize() {
        File file = new File(SER);
        if (isFileExistsOrCreate(file)) {
            saveFile(file);
        } else {
            log.info("保存订阅失败");
        }
    }

    public void deSerialize() {
        File file = new File(SER);
        if (isFileExistsOrCreate(file)) {
            try (FileInputStream fis = new FileInputStream(file); ObjectInputStream in = new ObjectInputStream(fis)) {
                Map<?, ?> map = (Map<?, ?>) in.readObject();
                for (Map.Entry<?, ?> topic : map.entrySet()) {
                    topics.put(String.valueOf(topic.getKey()), (MqttTopicModel) topic.getValue());
                    MqttTopicModel topicModel = (MqttTopicModel) topic.getValue();
                    messageProcessorManager.activeTopicProcessor(topicModel);
                    mqttService.subscribe((MqttTopicModel) topic.getValue());
                }
                log.info("加载订阅");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            log.info("加载订阅失败");
        }
    }

    private void saveFile(File file) {
        try (FileOutputStream fos = new FileOutputStream(file); ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(topics);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isFileExistsOrCreate(File file) {
        if (file.exists()) {
            return true;
        } else {
            try {
                File dir = file.getParentFile();
                if (dir.mkdirs()) {
                    log.info("创建订阅文件目录");
                }
                if (file.createNewFile()) {
                    log.info("创建订阅文件");
                    saveFile(file);
                    return true;
                } else {
                    log.error("创建订阅文件失败");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
