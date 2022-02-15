package com.bjdv.kafka;

import com.bjdv.dbconnector.direct.DirectService;
import com.bjdv.dbconnector.model.TopicModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: LX
 * @create: 2022-01-07 11:08
 **/
@Configuration
@Slf4j
public class KafkaTopicHolder {
    private static final String SER = System.getProperty("user.dir") + "/data/kafka_topic.ser";
    private final Map<String, TopicModel> topics = new HashMap<>();
    private DirectService directService;

}
