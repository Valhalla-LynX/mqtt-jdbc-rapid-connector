package com.bjdv.dbconnector;

import com.bjdv.dbconnector.mqtt.MqttTopicHolder;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableTransactionManagement
//@MapperScan("com.bjdv.dbconnector.dynamic.mapper")
//@Import({DynamicDataSourceRegister.class, MqttTopicHolder.class})
@Import({MqttTopicHolder.class})
@EnableAdminServer
public class DBConnectorApplication {
    public static void main(String[] args) {
        System.setProperty("reactor.netty.ioWorkerCount", "1");
        System.setProperty("reactor.netty.ioSelectCount", "1");
        System.setProperty("reactor.netty.pool.maxConnerConnections", "1");
        SpringApplication.run(DBConnectorApplication.class, args);
    }
}
