package com.bjdv.dbconnector.direct;

import com.bjdv.dbconnector.utils.JDBCReExecuteThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-19 10:03
 **/
@Slf4j
public class JDBCReExecutePolicy implements RejectedExecutionHandler {
    public final static String NAME = "BusyProcessor";
    private final ExecutorService overloadExecutor;

    public JDBCReExecutePolicy(JDBCHolder jdbcHolder) {
        this.overloadExecutor = Executors.newSingleThreadExecutor(new JDBCReExecuteThreadFactory(jdbcHolder));
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        log.info("线程名称-{},JDBC线程池过载,代理执行", Thread.currentThread().getName());
        overloadExecutor.execute(r);
    }

}
