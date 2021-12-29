package com.bjdv.dbconnector.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bjdv.dbconnector.direct.JDBCHolder;
import com.bjdv.dbconnector.mqtt.MqttTopicHolder;
import com.bjdv.dbconnector.mqtt.MqttTopicModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.validation.Valid;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: LX
 * @create: 2021-10-21 11:19
 **/
@RestController
@RequestMapping("/api")
@Slf4j
public class Controller {
    private WebApplicationContext applicationContext;
    private MqttTopicHolder topicSerializable;
    private JDBCHolder jdbcHolder;

    @Autowired
    @DependsOn({"initMqttTopicHolder", "initMqttSubscriber2ClickHouse"})
    public void initController(WebApplicationContext applicationContext,
                               JDBCHolder jdbcHolder,
                               MqttTopicHolder topicSerializable) {
        this.applicationContext = applicationContext;
        this.jdbcHolder = jdbcHolder;
        this.topicSerializable = topicSerializable;
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello";
    }

    @RequestMapping("/getTopic")
    public String getTopic() {
        return JSON.toJSONString(topicSerializable.getTopics());
    }

    @RequestMapping("/setTopic")
    public String setTopic(@Valid MqttTopicModel topic) {
        if (topicSerializable.addTopic(topic)) {
            log.info("保存订阅");
            return "success";
        } else {
            return "false";
        }
    }

    @RequestMapping("/rmTopic")
    public String rmTopic(String name) {
        MqttTopicModel mqttTopicModel = topicSerializable.getTopics().get(name);
        if (mqttTopicModel!=null) {
            topicSerializable.removeTopic(mqttTopicModel);
        }
        log.info("取消订阅");
        return "success";
    }

    @RequestMapping("/getApi")
    public Object getAllUrl() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();

        List<Map<String, String>> list = new ArrayList<>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
            Map<String, String> map1 = new HashMap<>();
            RequestMappingInfo info = m.getKey();
            HandlerMethod method = m.getValue();
            PatternsRequestCondition p = info.getPatternsCondition();
            for (String url : p.getPatterns()) {
                map1.put("url", url);
            }
            map1.put("className", method.getMethod().getDeclaringClass().getName()); // 类名
            map1.put("method", method.getMethod().getName()); // 方法名
            RequestMethodsRequestCondition methodsCondition = info.getMethodsCondition();
            for (RequestMethod requestMethod : methodsCondition.getMethods()) {
                map1.put("type", requestMethod.toString());
            }

            list.add(map1);
        }
        return list;
    }

    @RequestMapping("/getConnection")
    public JSONObject getConnection() {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, Map<String, Connection>> map : jdbcHolder.getConnectionMap().entrySet()) {
            JSONArray ja = new JSONArray();
            for (Map.Entry<String, Connection> connection : map.getValue().entrySet()) {
                ja.add(connection.getKey());
            }
            jsonObject.put(map.getKey(), ja);
        }
        return jsonObject;
    }

    @RequestMapping("/getDataSource")
    public JSONArray getDataSource() {
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<String, javax.sql.DataSource> map : jdbcHolder.getDatasourceMap().entrySet()) {
            jsonArray.add(map.getKey());
        }
        return jsonArray;
    }
}
