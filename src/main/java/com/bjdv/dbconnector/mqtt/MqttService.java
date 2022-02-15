package com.bjdv.dbconnector.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.bjdv.dbconnector.model.TopicModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: LX
 * @create: 2021-07-28 10:29
 **/
@Service
@Slf4j
public class MqttService {
    private MqttConfig mqttConfig;
    private MqttTopicHolder mqttTopicHolder;
    private MqttSubscriber2JDBC subscriber;
    private MqttClient client;

    @Autowired
    @DependsOn("initMqttSubscriber2ClickHouse")
    private void intiMqttSubscriber(MqttConfig mqttConfig, MqttTopicHolder mqttTopicHolder, MqttSubscriber2JDBC subscriber) {
        this.mqttConfig = mqttConfig;
        this.mqttTopicHolder = mqttTopicHolder;
        this.subscriber = subscriber;
    }

    protected void clientInit() {
        if (mqttConfig.getHost() == null || mqttConfig.getHost().isEmpty()) {
            log.info("LOG---without mqtt connection");
        } else {
            log.info("LOG---init mqtt connection");
            clientConnect();
            if (!client.isConnected()) {
                clientReconnect();
            }
        }
    }

    private void clientConnect() {
        try {
            MqttConnectOptions options = new MqttConnectOptions();
            String[] URIs = new String[1];
            URIs[0] = mqttConfig.getHost();
            options.setServerURIs(URIs);
            if (mqttConfig.getUsername() != null) {
                options.setUserName(mqttConfig.getUsername());
            }
            if (mqttConfig.getPassword() != null) {
                options.setPassword(mqttConfig.getPassword().toCharArray());
            }
            options.setAutomaticReconnect(true); // 自动连接
            options.setCleanSession(true); // 可靠性
            client = new MqttClient(mqttConfig.getHost(), mqttConfig.getClientId(), new MemoryPersistence());
            client.setCallback(subscriber);
            client.connect(options);
            log.info("LOG---connect to mqtt: success");
            mqttTopicHolder.deSerialize();
        } catch (MqttException me) {
            me.printStackTrace();
            log.error("LOG---connect to mqtt error: {}", me.getMessage());
        }
    }

    @SuppressWarnings({"BusyWait"})
    public void clientReconnect() {
        try {
            short tryRec;
            tryRec = 1;
            while (!client.isConnected() && tryRec < 5) {
                log.info("LOG---reconnect to mqtt: try");
                client.close();
                Thread.sleep(10000);
                clientConnect();
                tryRec++;
            }
            if (!client.isConnected()) {
                log.error("LOG---reconnect to mqtt: abort reconnection");
            }
        } catch (MqttException me) {
            me.printStackTrace();
            log.error("LOG---reconnect to mqtt error: {}", me.getMessage());
        } catch (InterruptedException ie) {
            log.error("LOG---reconnect to mqtt error in waiting: {}", ie.getMessage());
        }
    }

    public void subscribe(TopicModel topic) {
        if (topic.getShare() != null) {
            subscribe(topic.getShare() + topic.getTopic());
        } else {
            subscribe(topic.getTopic());
        }
    }

    private void subscribe(String topic) {
        try {
            client.subscribe(topic, 0);
            log.info("LOG---subscribe topic: {}", topic);
        } catch (MqttException me) {
            log.error("LOG---subscribe error: {}", me.getMessage());
        }
    }

    public void unsubscribe(TopicModel topic) {
        if (topic.getShare() != null) {
            unsubscribe(topic.getShare() + topic.getTopic());
        } else {
            unsubscribe(topic.getTopic());
        }
    }

    private void unsubscribe(String topic) {
        try {
            client.unsubscribe(topic);
            log.info("LOG---unsubscribe topic: {}", topic);
        } catch (MqttException me) {
            log.error("LOG---unsubscribe error: {}", me.getMessage());
        }
    }

    public void publish(int qos, boolean retained, String topic, String pushMessage) throws MqttPublishException {
        MqttMessage message = new MqttMessage();
        message.setQos(qos);
        message.setRetained(retained);
        message.setPayload(pushMessage.getBytes());
        MqttTopic mTopic = client.getTopic(topic);
        if (mTopic == null) {
            log.info("LOG---publish to mqtt error: lost topic");
            return;
        } else if (!client.isConnected()) {
            log.info("LOG---publish to mqtt error: client is offline");
            return;
        }
        MqttDeliveryToken token;
        try {
            token = mTopic.publish(message);
            token.waitForCompletion(4000);
        } catch (MqttException me) {
            throw new MqttPublishException(me, pushMessage);
        }
    }

    public void publish(MqttMessage message, MqttTopic mTopic) {
        try {
            MqttDeliveryToken token = mTopic.publish(message);
        } catch (MqttException me) {
            me.printStackTrace();
        }
    }

    @SuppressWarnings({"BusyWait"})
    public void sender() throws InterruptedException {
        MqttMessage message = new MqttMessage();
        message.setQos(0);
        message.setRetained(false);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("table", "test");
        jsonObject.put("id", 1);
        jsonObject.put("name", "name");
        message.setPayload(jsonObject.toString().getBytes());
        MqttTopic mTopic = client.getTopic("dbc_test");
        while (true) {
            publish(message, mTopic);
            Thread.sleep(1000);
        }
    }
}