package com.bjdv.dbconnector.utils;

import com.bjdv.dbconnector.direct.JDBCHolder;

/**
 * @description:
 * @author: LX
 * @create: 2021-11-19 18:18
 **/
public class JDBCReExecuteThreadFactory extends JDBCConnectionThreadFactory {
    private final static String Name = "BusyProcessor";

    public JDBCReExecuteThreadFactory(JDBCHolder jdbcHolder) {
        super(Name, jdbcHolder);
    }
}
