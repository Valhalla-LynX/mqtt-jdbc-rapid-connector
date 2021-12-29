package com.bjdv.dbconnector.direct;

import com.bjdv.dbconnector.dynamic.datasource.DynamicDataSourceStringMap;
import com.bjdv.dbconnector.utils.JDBCConnectionThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-02 10:15
 **/
@Component
public class JDBCConfig {
    @DependsOn({"initJDBCFactory", "initJDBCHolder"})
    @Autowired
    public void initDirectJDBC(JDBCDataSourceFactory jdbcDataSourceFactory, JDBCHolder jdbcHolder) {
        try {
            DataSource masterDataSource = jdbcDataSourceFactory.createJDBCDataSource(JDBCHolder.getMaster());
            jdbcHolder.setMasterDatasource(masterDataSource);

            HashMap<String, Connection> masterConnectionHashMap = new HashMap<>();
            masterConnectionHashMap.put(JDBCHolder.getPrivateConnectionName(), masterDataSource.getConnection());
            jdbcHolder.getConnectionMap().put(JDBCHolder.getMaster(), masterConnectionHashMap);

            Map<String,DataSource> map = jdbcDataSourceFactory.createJDBCDataSources();
            for (Map.Entry<String, DataSource> kv: map.entrySet()) {
                jdbcHolder.setDatasource(kv.getKey(),kv.getValue());
                HashMap<String, Connection> connectionHashMap = new HashMap<>();
                connectionHashMap.put(JDBCHolder.getPrivateConnectionName(), kv.getValue().getConnection());
                jdbcHolder.getConnectionMap().put(kv.getKey(), connectionHashMap);
            }

            Map<String, ExecutorService> executorServiceMap = new HashMap<>();
            executorServiceMap.put(JDBCHolder.getMaster(),  Executors.newFixedThreadPool(jdbcHolder.getMaxConnection(), new JDBCConnectionThreadFactory(JDBCHolder.getMaster(), jdbcHolder)));
            for (Map.Entry<String,DataSource> kv: map.entrySet()) {
                executorServiceMap.put(kv.getKey(),  Executors.newFixedThreadPool(jdbcHolder.getMaxConnection(), new JDBCConnectionThreadFactory(kv.getKey(), jdbcHolder)));
            }
            jdbcHolder.setExecutorMap(executorServiceMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
