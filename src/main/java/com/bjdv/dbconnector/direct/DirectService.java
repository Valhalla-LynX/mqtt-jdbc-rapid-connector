package com.bjdv.dbconnector.direct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @description:
 * @author: LX
 * @create: 2021-12-03 17:41
 **/
@Component
public class DirectService {
    private JDBCHolder jdbcHolder;

    @Autowired
    public void initDirectService(JDBCHolder jdbcHolder) {
        this.jdbcHolder = jdbcHolder;
    }

    private Connection getConnection(String datasourceName) {
        Connection connection = jdbcHolder.getPrivateConnection(datasourceName);
        try {
            if (connection.isClosed()) {
                connection = jdbcHolder.getDatasource(datasourceName).getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkTable(String datasourceName, String name) {
        try (Connection connection = getConnection(datasourceName);PreparedStatement preparedStatement = connection.prepareStatement(String.format("Select COUNT(*) From %s Limit 1", name))) {
            boolean b = preparedStatement.execute();
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
