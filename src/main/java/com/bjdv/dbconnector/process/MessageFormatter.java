package com.bjdv.dbconnector.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bjdv.dbconnector.mqtt.MqttTopicHolder;
import com.bjdv.dbconnector.model.TopicModel;
import com.bjdv.dbconnector.utils.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-17 13:47
 **/
@Component
@Slf4j
public class MessageFormatter {
    private static final String NAME = "MessageFormatter";
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new NamedThreadFactory(NAME));
    private MessageBufferProcessorManager messageProcessorManager;
    private MessageFormatErrorRecord messageFormatErrorRecord;
    private MqttTopicHolder mqttTopicHolder;

    @Autowired
    @DependsOn("initMqttTopicHolder")
    public void initMessageFormatter(MqttTopicHolder mqttTopicHolder,MessageBufferProcessorManager messageProcessorManager, MessageFormatErrorRecord messageFormatErrorRecord) {
        this.mqttTopicHolder = mqttTopicHolder;
        this.messageProcessorManager = messageProcessorManager;
        this.messageFormatErrorRecord = messageFormatErrorRecord;
    }

    public void offerMessage(String topic, MqttMessage mqttMessage) {
        executor.execute(new MessageChecker(topic, mqttMessage));
    }

    // todo remove
    public void offerMessage(TopicModel mqttTopicModel, MqttMessage mqttMessage) {
        executor.execute(new MessageChecker(mqttTopicModel, mqttMessage));
    }

    // todo remove
    public void offerMessage(TopicModel mqttTopicModel, JSONObject jsonObject) {
        executor.execute(new MessageChecker(mqttTopicModel, jsonObject));
    }

    // 负责接受并校验message到message池
    class MessageChecker implements Runnable {
        String topic;
        String data;
        StringBuilder singleData;
        StringBuilder multipleData;

        public MessageChecker(String topic, MqttMessage mqttMessage) {
            TopicModel mqttTopicModel = mqttTopicHolder.getTopics().get(topic);
            this.topic = mqttTopicModel.getTopic();
            data = getAndCheckMessage(mqttTopicModel, JSONObject.parseObject(mqttMessage.toString()));
            if (data == null) {
                messageFormatErrorRecord.sqlErrorRecord(mqttMessage.toString());
                log.error("线程名称-{}，订阅主题-{},错误解析，记录至MessageErrorFormat-{}", Thread.currentThread().getName(), topic, mqttMessage);
            }
        }

        // todo remove
        public MessageChecker(TopicModel mqttTopicModel, MqttMessage mqttMessage) {
            this.topic = mqttTopicModel.getTopic();
            data = getAndCheckMessage(mqttTopicModel, JSONObject.parseObject(mqttMessage.toString()));
            if (data == null) {
                messageFormatErrorRecord.sqlErrorRecord(mqttMessage.toString());
                log.error("线程名称-{}，订阅主题-{},错误解析，记录至MessageErrorFormat-{}", Thread.currentThread().getName(), topic, mqttMessage);
            }
        }

        // todo remove
        public MessageChecker(TopicModel mqttTopicModel, JSONObject jsonObject) {
            this.topic = mqttTopicModel.getTopic();
            data = getAndCheckMessage(mqttTopicModel, jsonObject);
            if (data == null) {
                messageFormatErrorRecord.sqlErrorRecord(jsonObject.toString());
                log.error("线程名称-{}，订阅主题-{},错误解析，记录至MessageErrorFormat-{}", Thread.currentThread().getName(), topic, jsonObject);
            }
        }

        public String getAndCheckMessage(TopicModel mqttTopicModel, JSONObject jsonObject) {
            String type = jsonObject.getString("type");
            String table = jsonObject.getString("table");
            JSONArray array = jsonObject.getJSONArray("data");
            if (type != null && table != null && array != null && !array.isEmpty()) {
                String data;
                switch (type) {
                    case "single":
                        singleData = new StringBuilder();
                        data = getValueAndCheck(mqttTopicModel, array);
                        singleData = null;
                        break;
                    case "multiple":
                        singleData = new StringBuilder();
                        multipleData = new StringBuilder();
                        data = getValuesAndCheck(mqttTopicModel, array);
                        singleData = null;
                        multipleData = null;
                        break;
                    default:
                        return null;
                }
                log.debug("线程名称-{}，订阅主题-{},获取并解析数据{}", Thread.currentThread().getName(), table, data);
                return data;
            } else {
                return null;
            }
        }

        private String getValueAndCheck(TopicModel mqttTopicModel, JSONArray array) {
            String colVal;
            singleData.delete(0, singleData.length());
            singleData.append('(');
            for (int i = 0; i < array.size(); i++) {
                colVal = array.getString(i);
                if (colVal == null) {
                    return null;
                }
                if (mqttTopicModel.getType()[i] == TopicModel.DataType.Number) {
                    try {
                        Long.valueOf(colVal);
                        singleData.append(colVal);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else {
                    singleData.append('\'');
                    singleData.append(colVal);
                    singleData.append('\'');
                }
                if (i != array.size() - 1) {
                    singleData.append(',');
                }
            }
            singleData.append(')');
            return singleData.toString();
        }

        private String getValuesAndCheck(TopicModel mqttTopicModel, JSONArray array) {
            multipleData.delete(0, multipleData.length());
            JSONArray singleJson;
            String singleVal;
            for (int i = 0; i < array.size(); i++) {
                singleJson = array.getJSONArray(i);
                if (singleJson == null || singleJson.isEmpty()) {
                    return null;
                }
                singleVal = getValueAndCheck(mqttTopicModel, singleJson);
                if (singleVal == null) {
                    return null;
                }
                multipleData.append(singleVal);
                if (i != array.size() - 1) {
                    multipleData.append(',');
                }
            }
            return multipleData.toString();
        }

        @Override
        public void run() {
            if (data != null) {
                messageProcessorManager.getManagerMap().get(topic).syncOffer(data);
                topic = null;
                data = null;
                singleData = null;
                multipleData = null;
            }
        }
    }
}
