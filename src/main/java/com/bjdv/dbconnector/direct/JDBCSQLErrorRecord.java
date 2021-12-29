package com.bjdv.dbconnector.direct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-19 14:13
 **/
@Slf4j
@Component
public class JDBCSQLErrorRecord {
    public void sqlErrorRecord(String reason, String sql) {
        log.error("REASON-{},SQL-{}", reason, sql);
    }
}
