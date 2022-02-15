package com.bjdv.dbconnector.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.bjdv.dbconnector.process.MessageFormatter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MqttSubscriber2JDBC implements MqttCallback {
    private MqttTopicHolder mqttTopicHolder;
    private MessageFormatter messageFormatter;
    // private MqttService mqttService;

    @Autowired
    public void initMqttSubscriber2ClickHouse(MqttTopicHolder mqttTopicHolder, MessageFormatter messageFactory) {
        this.mqttTopicHolder = mqttTopicHolder;
        this.messageFormatter = messageFactory;
    }

    @SneakyThrows
    @Override
    public void connectionLost(Throwable throwable) {
        // 连接丢失后，一般在这里面进行重连
        log.error("LOG---lost connection with mqtt: " + throwable.getMessage());
        // mqttService.clientReconnect();
    }

    /**
     * @description:
     * @author: LX
     * @create: 2021-07-28 10:29
     **/
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) {
        if (mqttTopicHolder.getTopics().containsKey(topic)) {
            messageFormatter.offerMessage(topic, mqttMessage);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    }

    protected void setMqttService(MqttService mqttService) {
        // this.mqttService = mqttService;
    }
}