package com.bjdv.dbconnector.process;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedList;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-18 14:25
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageBuffer extends LinkedList<String> {
    private String datasource;
    private String topic;
    private String table;
    private Long time;

    public MessageBuffer(String datasource, String topic, String table) {
        this.datasource = datasource;
        this.topic = topic;
        this.table = table;
        updateTime();
    }

    public MessageBuffer(MessageBuffer messageBuffer) {
        for (String content : messageBuffer) {
            offer(content);
        }
        this.datasource = messageBuffer.getDatasource();
        this.topic = messageBuffer.getTopic();
        this.table = messageBuffer.getTable();
        updateTime();
    }

    public void updateTime() {
        time = System.currentTimeMillis();
    }

    public String getId() {
        return topic + "-" + time;
    }
}
