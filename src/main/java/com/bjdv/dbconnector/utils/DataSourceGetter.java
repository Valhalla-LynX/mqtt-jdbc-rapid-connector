package com.bjdv.dbconnector.utils;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

/**
 * @description:
 * @author: LX
 * @create: 2021-12-20 10:38
 **/
public class DataSourceGetter {
    public static Class<? extends DataSource>  getDataSourceType(String typeStr) {
        Class<? extends DataSource> type;
        try {
            if (StringUtils.hasLength(typeStr)) {
                Class<?> clazz = Class.forName(typeStr);
                type = clazz.asSubclass(DataSource.class);
            } else {
                type = HikariDataSource.class;
            }
            return type;
        } catch (Exception e) {
            throw new IllegalArgumentException("can not resolve class with type: " + typeStr); //无法通过反射获取class对象的情况则抛出异常，该情况一般是写错了，所以此次抛出一个runtimeexception
        }
    }
}
