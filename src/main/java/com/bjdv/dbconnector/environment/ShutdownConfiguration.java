package com.bjdv.dbconnector.environment;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-22 11:45
 **/
@Configuration
public class ShutdownConfiguration {
    @PreDestroy
    public void preDestroy() {
        System.out.println("==============================");
        System.out.println("Destroying Spring");
        System.out.println("==============================");
    }
}
