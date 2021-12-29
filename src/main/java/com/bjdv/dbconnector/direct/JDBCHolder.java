package com.bjdv.dbconnector.direct;

import com.bjdv.dbconnector.process.MessageBuffer;
import com.bjdv.dbconnector.utils.JDBCConnectionThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-16 10:55
 **/
// todo 扩展Connection至所JDBCHolder线程->new Thread时新建
@Component
@Slf4j
public class JDBCHolder {
    //    private static final int MAX_CONNECTION = 8;
    private static final int MAX_Block = 1;
    private static final String MASTER = "master";
    private static final String PRIVATE_CONNECTION = "private-connection";
    private final Map<String, DataSource> datasourceMap = new HashMap<>();
    private final Map<String, Map<String, Connection>> connectionMap = new HashMap<>();

    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private int MAX_CONNECTION;
    @Value("${spring.datasource.hikari.connection-timeout}")
    private int TIMEOUT;
    private JDBCSQLErrorRecord jdbcErrorSQLRecord;
//    private ExecutorService executor;
    private Map<String, ExecutorService> executorMap;

    public static String getMaster() {
        return MASTER;
    }

    public static String getPrivateConnectionName() {
        return PRIVATE_CONNECTION;
    }

    public int getMaxConnection() {
        return MAX_CONNECTION;
    }

    @Autowired
    @DependsOn()
    public void initJDBCHolder(JDBCSQLErrorRecord jdbcErrorSQLRecord) {
        this.jdbcErrorSQLRecord = jdbcErrorSQLRecord;
//        JDBCConnectionThreadFactory jdbcConnectionThreadFactory = new JDBCConnectionThreadFactory(MASTER, this);
//        JDBCReExecutePolicy jdbcReExecutePolicy = new JDBCReExecutePolicy(this);

//        this.executor = new ThreadPoolExecutor(MAX_CONNECTION, MAX_CONNECTION,
//                TIMEOUT, TimeUnit.MILLISECONDS,
//                new LinkedBlockingDeque<>(MAX_Block), jdbcConnectionThreadFactory, jdbcReExecutePolicy);
//        this.executor = Executors.newFixedThreadPool(MAX_CONNECTION, jdbcConnectionThreadFactory);

//        this.executorMap = new HashMap<>();
//        for (Map.Entry<String, DataSource> kv: datasourceMap.entrySet()) {
//            executorMap.put(kv.getKey(),  Executors.newFixedThreadPool(MAX_CONNECTION, new JDBCConnectionThreadFactory(kv.getKey(), this)));
//        }
    }

    // 暂时只支持一个数据源插入数据
    public void insert(MessageBuffer messageBuffer, String sql) {
//        executor.execute(new InsertRunnable(MASTER, messageBuffer, sql));
        executorMap.get(messageBuffer.getDatasource()).execute(new InsertRunnable(MASTER, messageBuffer, sql));
    }

    // DatasourceMap
    public Map<String, DataSource> getDatasourceMap() {
        return datasourceMap;
    }

    // Datasource
    public DataSource getDatasource(String name) {
        return datasourceMap.get(name);
    }
    public void setDatasource(String name,DataSource dataSource) {
        datasourceMap.put(name, dataSource);
    }

    // MasterDatasource
    public DataSource getMasterDatasource() {
        return datasourceMap.get(MASTER);
    }
    public void setMasterDatasource(DataSource dataSource) {
        datasourceMap.put(MASTER, dataSource);
    }

    // ConnectionMap
    public Map<String, Map<String, Connection>> getConnectionMap() {
        return connectionMap;
    }

    // MasterConnectionMap
    public Map<String, Connection> getMasterConnectionMap() {
        return connectionMap.get(MASTER);
    }

    // PrivateConnection
    public Connection getPrivateConnection(String datasourceName) {
        return connectionMap.get(datasourceName).get(PRIVATE_CONNECTION);
    }
    public void setPrivateConnection(String datasourceName, Connection connection) {
        connectionMap.get(datasourceName).put(PRIVATE_CONNECTION, connection);
    }

    // MasterPrivateConnection
    public Connection getMasterPrivateConnection() {
        return connectionMap.get(MASTER).get(PRIVATE_CONNECTION);
    }

    // ExecutorMap
    public Map<String, ExecutorService> getExecutorMap() {
        return executorMap;
    }
    public void setExecutorMap(Map<String, ExecutorService> map) {
        executorMap = map;
    }

//    @Scheduled(cron = "0 */1 * * * ?")
//    public void gc(){
//        System.gc();
//    }


    // 负责执行sql
    private class InsertRunnable implements Runnable {
        private final String dataSource;
        private final MessageBuffer messageBuffer;
        private final String sql;

        public InsertRunnable(String dataSource, MessageBuffer messageBuffer, String sql) {
            this.dataSource = dataSource;
            this.messageBuffer = messageBuffer;
            this.sql = sql;
        }

        @Override
        public void run() {
            Connection connection = connectionMap.get(MASTER).get(Thread.currentThread().getName());
            try {
                if (connection.isClosed()) {
                    log.info("获取连接");
                    connection = datasourceMap.get(dataSource).getConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            boolean flag = false;
            for (int i = 0; i < 2; i++) {
                flag = doPreparedStatement(connection);
                if (flag) {
                    break;
                }
            }
            if (!flag) {
                doPreparedStatementAgain(connection);
            }
        }

        private boolean doPreparedStatement(Connection connection) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                long a = System.currentTimeMillis();
//                preparedStatement.executeQuery(sql);
                preparedStatement.execute(sql);
                long finish = System.currentTimeMillis() - a;
                log.info("线程名称-{}，完成插入信息-{},用时-{}", Thread.currentThread().getName(), messageBuffer.getId(), finish);
                // todo balance factor
                // messageProcessorManager.getManagerMap().get(topic).judgeWaitTime(finish);
                return true;
            } catch (SQLException e) {
                log.error("线程名称-{}，插入信息失败-{},原因-{},再次尝试", Thread.currentThread().getName(), messageBuffer, e.getMessage());
                return false;
            }
        }

        private void doPreparedStatementAgain(Connection connection) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                long a = System.currentTimeMillis();
                preparedStatement.executeQuery(sql);
                long finish = System.currentTimeMillis() - a;
                log.info("线程名称-{}，完成插入信息-{},用时-{}", Thread.currentThread().getName(), messageBuffer.getId(), finish);
                // todo balance factor
                // messageProcessorManager.getManagerMap().get(topic).judgeWaitTime(finish);
            } catch (SQLException e) {
                log.error("线程名称-{}，插入信息失败-{},原因-{},记录错误至ErrorRecord", Thread.currentThread().getName(), messageBuffer, e.getMessage());
                jdbcErrorSQLRecord.sqlErrorRecord(e.getMessage(), sql);
            }
        }

    }
}
