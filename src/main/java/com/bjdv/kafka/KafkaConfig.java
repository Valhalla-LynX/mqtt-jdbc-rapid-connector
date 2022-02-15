package com.bjdv.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: LX
 * @create: 2022-01-07 11:19
 **/
@Configuration
@Data
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {
}
