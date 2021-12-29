package com.bjdv.dbconnector.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @description:
 * @author: LX
 * @create: 2021-10-26 09:22
 **/
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Slf4j
public class SenderApplication {
    @Autowired
    public MqttService mqttService;

    @Test
    public void send() throws MqttPublishException, InterruptedException {
        mqttService.sender();
    }

}
