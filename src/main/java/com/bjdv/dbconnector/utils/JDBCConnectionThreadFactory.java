package com.bjdv.dbconnector.utils;

import com.bjdv.dbconnector.direct.JDBCHolder;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.util.Map;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-18 13:20
 **/
public class JDBCConnectionThreadFactory extends NamedThreadFactory {
    private final static String Name = "JDBCConnection";
    private final JDBCHolder jdbcHolder;
    private final String datasource;


    public JDBCConnectionThreadFactory(String datasource, JDBCHolder jdbcHolder) {
        super(Name + "-" + datasource);
        this.datasource = datasource;
        this.jdbcHolder = jdbcHolder;
    }

    @SneakyThrows
    @Override
    public Thread newThread(Runnable r) {
        Thread t = super.newThread(r);
        Map<String, Connection> map = jdbcHolder.getMasterConnectionMap();
        Connection connection = map.get(t.getName());
        if (connection == null || connection.isClosed()) {
            map.put(t.getName(), jdbcHolder.getDatasource(datasource).getConnection());
            //map.put(t.getName(), jdbcHolder.getMasterDatasourceMap().getConnection());
        }
        return t;
    }

    private synchronized void renewConnection() {

    }
}
