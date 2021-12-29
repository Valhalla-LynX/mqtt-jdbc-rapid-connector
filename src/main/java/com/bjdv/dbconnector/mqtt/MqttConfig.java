package com.bjdv.dbconnector.mqtt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: LX
 * @create: 2021-08-04 14:12
 **/
@Configuration
@Data
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {
    private String host;
    private String clientId;
    private String username;
    private String password;

    private MqttTopicHolder mqttTopicSerializable;
    private MqttService mqttService;

    @Autowired
    public void initMqttConfig(MqttTopicHolder mqttTopicSerializable, MqttService mqttService) {
        this.mqttTopicSerializable = mqttTopicSerializable;
        this.mqttService = mqttService;
    }

    @Bean
    public void initMqtt() {
        mqttService.clientInit();
    }
}
