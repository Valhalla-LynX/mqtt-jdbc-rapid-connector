package com.bjdv.dbconnector.process;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-19 16:08
 **/
@Slf4j
@Component
public class MessageFormatErrorRecord {
    public void sqlErrorRecord(String json) {
        log.error("json-{}", json);
    }
}
