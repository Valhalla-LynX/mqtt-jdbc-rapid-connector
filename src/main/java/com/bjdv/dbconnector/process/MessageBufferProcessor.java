package com.bjdv.dbconnector.process;

import com.bjdv.dbconnector.direct.JDBCHolder;
import com.bjdv.dbconnector.mqtt.MqttTopicHolder;
import com.bjdv.dbconnector.utils.NamedThreadFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-16 15:39
 **/
@Slf4j
@SuppressWarnings("unused")
public class MessageBufferProcessor extends Thread {
    private static final String NAME_TITLE = "Processor:";
    private static final int BLOCK = 1000;
    private static final int[] waitTime = {50 ,100, 150, 200, 250};

    private final String datasource;
    private final String topic;
    private final String table;
    private final JDBCHolder jdbcHolder;
    private final MqttTopicHolder mqttTopicHolder;
    private final MessageBuffer list;
    private final ExecutorService executor;
    private boolean flag = true;
    private int waitFactor = 0;

    public MessageBufferProcessor(String datasource, String topic, String table, JDBCHolder jdbcHolder, MqttTopicHolder mqttTopicHolder) throws Exception {
        this.datasource = datasource;
        this.topic = topic;
        this.table = table;
        if (jdbcHolder == null) {
            log.error("no jdbc holder");
            throw new Exception("no jdbc holder");
        }
        this.jdbcHolder = jdbcHolder;
        this.mqttTopicHolder = mqttTopicHolder;
        this.list = new MessageBuffer(datasource, topic, table);
        executor = Executors.newSingleThreadExecutor(new NamedThreadFactory(NAME_TITLE + this.topic));
    }

    public void asyncExecuteInsert() {
        MessageBuffer queue = syncPollAll();
        executor.execute(new ExecuteInsertProcess(queue));
    }

    public synchronized void syncOffer(String value) {
        list.offer(value);
    }

    public synchronized boolean syncOffer(String[] values) {
        for (String str : values) {
            list.offer(str);
        }
        return true;
    }

    public synchronized String syncPoll() {
        return list.poll();
    }

    public synchronized MessageBuffer syncPollAll() {
        return pollAll();
    }

    public synchronized LinkedList<String> syncPollMaxInBlock() {
        if (list.size() < BLOCK) {
            return pollAll();
        } else {
            MessageBuffer newList = new MessageBuffer(datasource, topic, table);
            newList.setTopic(list.getTopic());
            Iterator<String> i = list.iterator();
            int n = 0;
            for (; ; ) {
                String s = i.next();
                n++;
                newList.offer(s);
                if (n == BLOCK || !i.hasNext()) {
                    for (int j = 0; j < n; j++) {
                        list.poll();
                    }
                    list.updateTime();
                    System.out.println("retain:" + list.size());
                    return newList;
                }
            }
        }
    }

    private MessageBuffer pollAll() {
        MessageBuffer queue = new MessageBuffer(list);
        list.clear();
        list.updateTime();
        return queue;
    }

    // 负责定时处理message池
    @SuppressWarnings({"BusyWait"})
    @SneakyThrows
    @Override
    public void run() {
        while (flag) {
            // todo balance factor
            Thread.sleep(waitTime[waitFactor]);
            if (list.size() > 0) {
                asyncExecuteInsert();
            }
        }
    }

    public void close() {
        flag = false;
    }

    public String getMessageString() {
        Iterator<String> it = list.iterator();
        if (!it.hasNext())
            return null;

        StringBuilder sb = new StringBuilder();
        for (; ; ) {
            String e = it.next();
            sb.append(e);
            if (!it.hasNext())
                return sb.toString();
            sb.append(',');
        }
    }

    // todo balance factor
    public void judgeWaitTime(long executeTime) {
        if (executeTime > waitTime[waitFactor] && waitFactor < waitTime.length - 1) {
            waitFactor += 1;
            log.debug("线程名称-{},自平衡+1", Thread.currentThread().getName());
        } else if (executeTime < waitTime[waitFactor] && waitFactor > 0) {
            waitFactor -= 1;
            log.debug("线程名称-{},自平衡-1", Thread.currentThread().getName());
        }
    }

    // 负责组装sql
    private class ExecuteInsertProcess implements Runnable {
        private MessageBuffer messageBuffer;

        public ExecuteInsertProcess(MessageBuffer messageBuffer) {
            this.messageBuffer = messageBuffer;
        }

        @Override
        public void run() {
            StringBuilder stringBuilder = new StringBuilder();
            Iterator<String> iterator = messageBuffer.iterator();
            stringBuilder.append("INSERT INTO ");
            stringBuilder.append(messageBuffer.getTable());
            stringBuilder.append(" (");
            String[] strings = mqttTopicHolder.getTopics().get(messageBuffer.getTopic()).getColumn();
            for (int i = 0; i < strings.length; i++) {
                stringBuilder.append(strings[i]);
                if (i != strings.length - 1) {
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append(") VALUES ");
            while (iterator.hasNext()) {
                stringBuilder.append(iterator.next());
                if (iterator.hasNext()) {
                    stringBuilder.append(',');
                }
            }
            String sql = stringBuilder.toString();
            log.info("线程名称-{}，提供插入信息-{}", Thread.currentThread().getName(), messageBuffer.getId() + "-" + sql);
            jdbcHolder.insert(messageBuffer, sql);
            messageBuffer = null;
        }
    }
}
