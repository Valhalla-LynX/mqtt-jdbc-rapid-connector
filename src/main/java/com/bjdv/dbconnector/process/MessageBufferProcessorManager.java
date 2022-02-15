package com.bjdv.dbconnector.process;

import com.bjdv.dbconnector.direct.JDBCHolder;
import com.bjdv.dbconnector.mqtt.MqttTopicHolder;
import com.bjdv.dbconnector.model.TopicModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-17 13:56
 **/
@Component
@SuppressWarnings("unused")
public class MessageBufferProcessorManager {
    private final Map<String, MessageBufferProcessor> managerMap = new HashMap<>();
    private JDBCHolder jdbcHolder;
    private MqttTopicHolder mqttTopicHolder;

    @Autowired
    @DependsOn("initMqttTopicHolder")
    public void initMessageBufferProcessorManager(JDBCHolder jdbcHolder, MqttTopicHolder mqttTopicHolder) {
        this.jdbcHolder = jdbcHolder;
        this.mqttTopicHolder = mqttTopicHolder;
    }

    public void activeTopicProcessor(TopicModel mqttTopicModel) {
        String datasource = mqttTopicModel.getDatasource();
        String topic = mqttTopicModel.getTopic();
        String table = mqttTopicModel.getTable();
        try {
            managerMap.put(topic, new MessageBufferProcessor(datasource, topic, table, jdbcHolder, mqttTopicHolder));
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        managerMap.get(topic).start();
    }

    public void inactiveTopicProcessor(TopicModel mqttTopicModel) {
        String topic = mqttTopicModel.getTopic();
        close(topic);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        managerMap.remove(topic);
    }


    public void close(String topic) {
        managerMap.get(topic).close();
    }

    public Map<String, MessageBufferProcessor> getManagerMap() {
        return managerMap;
    }
}
