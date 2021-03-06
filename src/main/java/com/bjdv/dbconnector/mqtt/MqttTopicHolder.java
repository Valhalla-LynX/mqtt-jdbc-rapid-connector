package com.bjdv.dbconnector.mqtt;

import com.bjdv.dbconnector.direct.DirectService;
import com.bjdv.dbconnector.model.TopicModel;
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
    private static final String SER = System.getProperty("user.dir") + "/data/mqtt_topic.ser";
    private final Map<String, TopicModel> topics = new HashMap<>();
    private DirectService directService;
    private MqttService mqttService;
    private MessageBufferProcessorManager messageProcessorManager;

    public Map<String, TopicModel> getTopics() {
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

    public boolean addTopic(TopicModel topic) {
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

    public void removeTopic(TopicModel topic) {
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
            log.info("??????????????????");
        }
    }

    public void deSerialize() {
        File file = new File(SER);
        if (isFileExistsOrCreate(file)) {
            try (FileInputStream fis = new FileInputStream(file); ObjectInputStream in = new ObjectInputStream(fis)) {
                Map<?, ?> map = (Map<?, ?>) in.readObject();
                for (Map.Entry<?, ?> topic : map.entrySet()) {
                    topics.put(String.valueOf(topic.getKey()), (TopicModel) topic.getValue());
                    TopicModel topicModel = (TopicModel) topic.getValue();
                    messageProcessorManager.activeTopicProcessor(topicModel);
                    mqttService.subscribe((TopicModel) topic.getValue());
                }
                log.info("????????????");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            log.info("??????????????????");
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
                    log.info("????????????????????????");
                }
                if (file.createNewFile()) {
                    log.info("??????????????????");
                    saveFile(file);
                    return true;
                } else {
                    log.error("????????????????????????");
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
